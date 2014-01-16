package refiner;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import libsvm.libsvm.svm;
import libsvm.libsvm.svm_model;
import libsvm.libsvm.svm_node;
import libsvm.libsvm.svm_parameter;
import libsvm.libsvm.svm_problem;
import refiner.bool.FieldVar;
import refiner.bool.LIATerm;
import tzuyu.engine.TzClass;
import tzuyu.engine.model.ArtFieldInfo;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.ObjectInfo;
import tzuyu.engine.model.Prestate;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.StatementKind;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.utils.Pair;



public class SVMWrapper {
  private static final double EPSILON = 0.0001;
  private svm_parameter param;
  private List<FieldVar> properties;
  private int currentLevel;
  
  private int svmCallCount = 0;
  
  private int timeConsumed = 0;

  public SVMWrapper() {
    this.param = new svm_parameter();
    // Use the penalty classification function
    param.svm_type = svm_parameter.C_SVC;
    // Only cares about linear relations
    param.kernel_type = svm_parameter.LINEAR;
    // The penalty parameter. A larger value means there
    // is less errors allowed in the generated model.
    param.C = 1000.0;
    param.cache_size = 40;  
    param.eps = 1e-3;
    param.shrinking = 1;
    param.probability = 0;
    param.nr_weight = 0;

    properties = new ArrayList<FieldVar>();
  }

  public int getSVMCallCount() {
    return svmCallCount;
  }
  
  /**
   * Find a divider for the counter examples found in candidate queries. If we
   * cannot find a divider just return null.
   * 
   * @param positive
   *          the positive data set whose size must be greater than
   *          <code>0</code>.
   * @param negative
   *          the negative data set whose size must be greater than
   *          <code>0</code>.
   * @return a formula which divides the positive data set from the negative
   *         data set. If the formula cannot be found, then <code>null</code>
   *         will be returned.
   */
  public Formula candidateDivide(TzuYuAction transition, 
      List<QueryTrace> positive, List<QueryTrace> negative, TzClass project) {
    
    long startTime = System.currentTimeMillis();
    if (positive == null || negative == null) {
      throw new IllegalArgumentException("The input sets must not be null");
    }

    if (positive.size() == 0 || negative.size() == 0) {
      throw new IllegalArgumentException("The size of negative and postive "
          + "data set for refinement must be greater than 0");
    }
    
    List<Prestate> positiveStates = new ArrayList<Prestate>();
    List<Prestate> negativeStates = new ArrayList<Prestate>();
    for (int index = 0; index < positive.size(); index++) {
      QueryTrace trace = positive.get(index);
      positiveStates.add(trace.getLastState());
    }

    for (int index = 0; index < negative.size(); index++) {
      QueryTrace trace = negative.get(index);
      negativeStates.add(trace.getLastState());
    }
    // For candidate query we need to check whether the non-receiver parameter
    // is relevant to the execution result in order to make SVM converge faster.
    List<Boolean> relevance = SVMPreprocessor.checkParametersRelevance(
        transition, positive, negative);

    // Call the preprocessor to balance the inputs
    Pair<List<Prestate>, List<Prestate>> scaled = 
        SVMPreprocessor.balanceTraningSet(positiveStates, negativeStates);
    
    StatementKind stmt = transition.getAction();
    List<Class<?>> inputTypes = stmt.getInputTypes();
    List<ClassInfo> wrapperTypes = new ArrayList<ClassInfo>(inputTypes.size());

    int maxLevel = project.getConfiguration().getClassMaxDepth();
    for (int index = 0; index < inputTypes.size(); index++) {
      Class<?> type = inputTypes.get(index);
      ClassInfo typeClassInfo = project.getClassInfo(type);
      wrapperTypes.add(typeClassInfo);
    }

    for (currentLevel = 0; currentLevel <= maxLevel; currentLevel++) {
      properties.clear();
      for (int index = 0; index < wrapperTypes.size(); index++) {
        //Don't include irrelevant parameters
        if (relevance.get(index)) {
          ClassInfo ci = wrapperTypes.get(index);

          List<ArtFieldInfo> tfields = ci.getFieldsOnLevel(currentLevel);
          for (ArtFieldInfo field : tfields) {
            properties.add(FieldVar.getVar(stmt, index, field));
          }
        }
      }

      if (properties.size() == 0) {
        continue;
      }

      Formula divider = 
          internalDivide(scaled.first(), scaled.second(), relevance);

      if (!divider.equals(Formula.TRUE)) {
        timeConsumed+= (System.currentTimeMillis() - startTime);
        return divider;
      } else if (divider.equals(Formula.FALSE)) {
        return null;
      } else {
        continue;
      }
    }
    
    timeConsumed += (System.currentTimeMillis() - startTime);
    // We cannot find a divider for these counter examples
    return null;
  }

  /**
   * Find a boolean formula which divides the positive data set from the
   * positive data set found during membership queries. If a formula cannot be
   * found return <code>null</code>.
   * 
   * @param positive
   *          the positive data set whose size must be greater than
   *          <code>0</code>.
   * @param negative
   *          the negative data set whose size must be greater than
   *          <code>0</code>.
   * @return the boolean formula divider or <code>null</code> when the formula
   *         cannot be found.
   */
  public Formula memberDivide(List<QueryTrace> positive, 
      List<QueryTrace> negative, TzClass project) {
    long startTime = System.currentTimeMillis();
    if (positive == null || negative == null) {
      throw new IllegalArgumentException("The input sets must not be null");
    }

    if (positive.size() == 0 || negative.size() == 0) {
      throw new IllegalArgumentException("The size of negative and postive "
          + "data set for refinement must be greater than 0");
    }
    
    List<Prestate> positiveStates = new ArrayList<Prestate>();
    List<Prestate> negativeStates = new ArrayList<Prestate>();
    for (int index = 0; index < positive.size(); index++) {
      QueryTrace trace = positive.get(index);
      positiveStates.add(trace.getLastState());
    }

    for (int index = 0; index < negative.size(); index++) {
      QueryTrace trace = negative.get(index);
      negativeStates.add(trace.getLastState());
    }

    TzuYuAction transition = negative.get(0).getNextAction();
    
    
    // For membership query we also need to check whether the non-receiver 
    // parameter is relevant to the execution result in order to make SVM 
    // converge faster.
    List<Boolean> relevance = SVMPreprocessor.checkParametersRelevance(
        transition, positive, negative);
    
    //Call the preprocessor to balance the inputs
    Pair<List<Prestate>, List<Prestate>> scaled = 
        SVMPreprocessor.balanceTraningSet(positiveStates, negativeStates);
    
    // The inconsistency may happen during the intermediate method calls,
    // so we firstly need to find where the first inconsistent happened.
    StatementKind stmt = transition.getAction();
    List<Class<?>> inputTypes = stmt.getInputTypes();
    List<ClassInfo> wrapperTypes = new ArrayList<ClassInfo>(inputTypes.size());

    int maxLevel = project.getConfiguration().getClassMaxDepth();
    for (int index = 0; index < inputTypes.size(); index++) {   
      Class<?> type  = inputTypes.get(index);
      ClassInfo targetType = project.getClassInfo(type);
      wrapperTypes.add(targetType);
    }

    for (currentLevel = 0; currentLevel < maxLevel; currentLevel++) {
      properties.clear();
      for (int index = 0; index < wrapperTypes.size(); index++) {
        //Only generate divider for relevant parameters
        if (relevance.get(index)) {
          ClassInfo ci = wrapperTypes.get(index);
          List<ArtFieldInfo> fields = ci.getFieldsOnLevel(currentLevel);
          for (ArtFieldInfo field : fields) {
            properties.add(FieldVar.getVar(stmt, index, field));
          }
        }
      }

      if (properties.size() == 0) {
        continue;
      }

      Formula divider = 
          internalDivide(scaled.first(), scaled.second(), relevance);
      if (!divider.equals(Formula.TRUE)) {
        timeConsumed += (System.currentTimeMillis() - startTime);
        return divider;
      } else if (divider.equals(Formula.FALSE)) {
        return null;
      }
    }
    timeConsumed += (System.currentTimeMillis() - startTime);
    // We cannot find a divider for these counter examples
    return null;
  }
  
  /**
   * Call LibSVM to divide the two samples training sets 
   * given as two sets of pre-states.
   * @param positive
   * @param negative
   * @return Forumla.True if we the two input sets are the same; Formula.False
   * if the two sets are not linear separable; otherwise, the found divider. 
   */
  private Formula internalDivide(List<Prestate> positive, 
      List<Prestate> negative, List<Boolean> relevance) {
    
    svmCallCount++;
    /**
     * Step 1: Initialize the problem size
     */
    svm_problem prob = initializeProblem(positive, negative, relevance);

    /**
     * Step 2: Get the model for the problem according to the specified
     * parameters
     */

    svm_model model = svm.svm_train(prob, param);

    /**
     * Step 3: Generate the divider from the postulated model. The halfspace
     * can be get by linear combination of supporting vectors.
     */
    Formula divider = getHalfspace(model, prob);

    return divider;
  }

   /**
   * Get the half space for linearly separable inputs.
   * 
   * @param model
   * @param param
   */
  private Formula getHalfspace(svm_model model, svm_problem prob) {
    /**
     * Step 1: Calculate the halfspace from the model.
     */
    int nr_class = model.nr_class;
    // Only when the number of class is two we can get the hyperplane
    if (nr_class != 2) {
      return null;
    }

    // Initialize the bias and one order multiple variables polynomial
    // for the final half-space.
    double bias = 0;
    int propertySize = properties.size();

    svm_node[] hyperplane = new svm_node[propertySize];
    for (int i = 0; i < propertySize; i++) {
      hyperplane[i] = new svm_node();
      hyperplane[i].index = i;
      hyperplane[i].value = 0.0;
    }
    // Start is the starting index for class i in the SV array.
    int[] start = new int[nr_class];
    start[0] = 0;
    for (int i = 1; i < nr_class; i++) {
      start[i] = start[i - 1] + model.nSV[i - 1];
    }

    int p = 0;
    for (int i = 0; i < nr_class; i++) {
      for (int j = i + 1; j < nr_class; j++) {
        int si = start[i];
        int sj = start[j];
        int ci = model.nSV[i];
        int cj = model.nSV[j];

        double[] coef1 = model.sv_coef[j - 1];
        double[] coef2 = model.sv_coef[i];

        for (int k = 0; k < ci; k++) {
          for (int m = 0; m < propertySize; m++) {
            hyperplane[m].value += coef1[si + k] * model.SV[si + k][m].value;
          }
        }

        for (int k = 0; k < cj; k++) {
          for (int m = 0; m < propertySize; m++) {
            hyperplane[m].value += coef2[sj + k] * model.SV[sj + k][m].value;
          }
        }

        bias = model.rho[p];
        p++;
      }
    }

    /**
     * Step 2: check whether the generated hyper-plane is valid. Since the 
     * hyper-plane returned by libSVM may not be correct if the data sets are 
     * not linear separable, but there is no means to check whether the data 
     * is linear separable beforehand, thus we check whether the divider 
     * returned by libSVM is an correct divider post-priori. If the returned 
     * divider is not correct we return false.
     */
    for (int i = 0; i < prob.x.length; i++) {
      svm_node[] data = prob.x[i];
      double leftHand = 0.0;
      for (int index = 0; index < propertySize; index++) {
        leftHand += data[index].value * hyperplane[index].value;
      }
      
      if (prob.y[i] == 1) {
        if (leftHand < bias ) {
          return Formula.FALSE;
        }
      } else {
        if (leftHand > bias ) {
          return Formula.FALSE;
        }
      }
    }
      
    
    /**
     * Step 2: Construct the boolean expression from the halfspace. We truncate
     * the coefficient and bias of type double to integer, this may introduce
     * some inaccuracy. But right now we don't have a better way to do this,
     * maybe we could get the closest integer other than truncation.
     */
    List<LIATerm> terms = new ArrayList<LIATerm>();
    for (int i = 0; i < propertySize; i++) {
      // If the coefficient is zero, ignore the field.
      if (Math.abs(hyperplane[i].value) < EPSILON) {
        continue;
      }
      FieldVar field = properties.get(hyperplane[i].index);
      LIATerm term = new LIATerm(field, hyperplane[i].value);
      terms.add(term);
    }

    if (terms.size() == 0) {
      return Formula.TRUE;
    }

    //return new LIAAtom(terms, Operator.GE, bias);
    
    // Call the divider processor to process the raw divider generated
    // by libSVM and return a human readable boolean formula
    Formula predicates = DividerProcessor.process(terms, bias);

    return predicates;
  }

  /**
   * Prepare the problem format accepted by LibSVM from the two sets of
   * training prestates.
   * 
   * @param positive
   * @param negative
   * @return
   */
  private svm_problem initializeProblem(List<Prestate> positive, 
      List<Prestate> negative, List<Boolean> filter) {
    int propertySize = properties.size();
    svm_problem prob = new svm_problem();

    prob.l = positive.size() + negative.size();
    // Since the input problem may be sparse, we don't know how many nodes
    // there are in each line.
    prob.x = new svm_node[prob.l][];
    prob.y = new double[prob.l];

    int pSize = positive.size();
    for (int i = 0; i < pSize; i++) {
      // Handle the positive values
      // Get the second to last main object from
      // both positive and negative examples
      Prestate state = positive.get(i);
      // Set the i-th point
      Vector<svm_node> nodes = new Vector<svm_node>();
      List<ObjectInfo> objs = state.getValuesOnLevel(currentLevel, filter);
      for (int j = 0; j < propertySize; j++) {
        svm_node node = new svm_node();
        node.index = j + 1;

        if (j > objs.size() - 1) {
          node.value = 0;
        } else {
          ObjectInfo obj = objs.get(j);
          node.value = obj.getNumericValue();
        }
        nodes.add(node);
      }

      prob.x[i] = nodes.toArray(new svm_node[nodes.size()]);
      // Set to the positive category
      prob.y[i] = 1;
    }

    int nSize = negative.size();
    for (int i = 0; i < nSize; i++) {
      // Handle the negative values
      Prestate state = negative.get(i);
      // Set the i-th point
      Vector<svm_node> nodes = new Vector<svm_node>();
      List<ObjectInfo> objs = state.getValuesOnLevel(currentLevel, filter);
      for (int j = 0; j < propertySize; j++) {
        svm_node node = new svm_node();
        node.index = j + 1;
        if (j > objs.size() - 1) {
          node.value = 0;
        } else {
          ObjectInfo obj = objs.get(j);
          node.value = obj.getNumericValue();
        }
        nodes.add(node);
      }

      prob.x[i + pSize] = nodes.toArray(new svm_node[nodes.size()]);
      // Set to the negative category
      prob.y[i + pSize] = -1;
    }
    return prob;
  }

  public int getTimeConsumed() {
    return timeConsumed;
  }
}
