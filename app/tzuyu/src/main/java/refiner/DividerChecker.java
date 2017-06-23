package refiner;

import java.util.ArrayList;
import java.util.List;

import tester.RuntimeExecutor;
import tzuyu.engine.bool.FieldVar;
import tzuyu.engine.bool.Var;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.VarIndex;
import tzuyu.engine.model.Variable;



/**
 * Check whether the found divider is an valid divider.
 * 
 * @author Spencer Xiao
 * 
 */
public class DividerChecker {

  public static boolean checkValidity(Formula divider, 
      List<QueryTrace> positive, List<QueryTrace> negative) {

    int nSize = negative.size();
    int pSize = positive.size();

    List<Var> vars = divider.getReferencedVariables();

    List<FieldVar> inputFields = new ArrayList<FieldVar>();
    for (Var var : vars) {
      FieldVar fieldVar = (FieldVar) var;
      // we only need to check non-receiver parameters
      if (!fieldVar.isReceiver()) {
        inputFields.add(fieldVar);
      }
    }
    // For static method which does not have a receiver,
    // the divider on input arguments is valid.
    if (inputFields.size() == 0) {
      return true;
    }

    // Testing positive traces with relevant parameters from negative traces
    for (int i = 0; i < nSize; i++) {
      QueryTrace negTrace = negative.get(i);
      // The negStmtIndex should be in [-1, query.size() - 2]
      int negStmtIndex = negTrace.lastActionIdx;

      TzuYuAction stmt = negTrace.getNextAction();
      // Find all the referenced non-receiver input arguments

      List<Variable> negVars = new ArrayList<Variable>(inputFields.size());
      for (FieldVar fieldVar : inputFields) {
        // Get the variable for the next statement
        Variable newVar = negTrace.getVariableForStatement(negStmtIndex + 1,
            fieldVar.getArgIndex());

        negVars.add(newVar);
      }

      int argSize = stmt.getInputTypes().size();

      for (int j = 0; j < pSize; j++) {
        QueryTrace posTrace = positive.get(j);
        int posStmtIndex = posTrace.lastActionIdx;

        // Prepare the input arguments
        List<Sequence> sequences = new ArrayList<Sequence>(argSize);
        List<VarIndex> indices = new ArrayList<VarIndex>(argSize);
        int totStmts = 0;

        
        for (int index = 0; index < argSize; index++) {
          Variable input = null;
          boolean found = false;
          for (int fIndex = 0; fIndex < inputFields.size(); fIndex++) {
            FieldVar fieldVar = inputFields.get(fIndex);
            if (fieldVar.getArgIndex() == index) {
              // use the new inputs from negative trace to substitute
              input = negVars.get(fIndex);
              found = true;
              break;
            } 
          }
          
          if (!found) {
            // use the positive trace input
            input = posTrace.getVariableForStatement(posStmtIndex + 1, index);
          }

          indices.add(new VarIndex(input.getStmtIdx() + totStmts, input.getArgIdx()));
          sequences.add(input.owner);
          totStmts += input.owner.size();
        }

        Sequence newSeq = Sequence.concatenate(sequences);

        List<Variable> newInputVars = new ArrayList<Variable>(argSize);
        for (int index = 0; index < argSize; index++) {
          VarIndex vIdx = indices.get(index);
          newInputVars.add(new Variable(newSeq, vIdx.getStmtIdx(), vIdx.getArgIdx()));
        }
        // Execute the statement with new relevant inputs from negative trace
        // and other inputs from positive trace. So if the execution result is
        // OK(normal) then which means input does not influence the execution.
        boolean normal = RuntimeExecutor.execute(stmt, newInputVars);

        if (normal == true) {
          // If the new trace trace conforms with positive trace even though it
          // uses relevant inputs from the negative trace, this means the found
          // divider is invalid on the input variables.
          // return false;
          continue;
        } else {
          // After changing the input parameters the execution does not conform
          // to the result of positive trace, so the found divider is valid on
          // the input variables.
          return true;
        }
      }
    }

    // Testing negative traces with relevant parameters from positive traces
    for (int i = 0; i < pSize; i++) {
      QueryTrace posTrace = positive.get(i);
      // The negStmtIndex should be in [-1, query.size() - 2]
      int negStmtIndex = posTrace.lastActionIdx;

      TzuYuAction stmt = posTrace.getNextAction();
      // Find all the referenced non-receiver input arguments

      List<Variable> posVars = new ArrayList<Variable>(inputFields.size());
      for (FieldVar fieldVar : inputFields) {
        // Get the variable for the next statement
        Variable newVar = posTrace.getVariableForStatement(negStmtIndex + 1,
            fieldVar.getArgIndex());

        posVars.add(newVar);
      }

      int argSize = stmt.getInputTypes().size();

      for (int j = 0; j < nSize; j++) {
        QueryTrace negTrace = negative.get(j);
        int posStmtIndex = negTrace.lastActionIdx;

        // Prepare the input arguments
        List<Sequence> sequences = new ArrayList<Sequence>(argSize);
        List<VarIndex> indices = new ArrayList<VarIndex>(argSize);
        int totStmts = 0;

        for (int index = 0; index < argSize; index++) {
          Variable input = null;
          boolean found = false;
          for (int fIndex = 0; fIndex < inputFields.size(); fIndex++) {
            FieldVar fieldVar = inputFields.get(fIndex);
            if (fieldVar.getArgIndex() == index) {
              // use the new inputs from positive trace to substitute
              input = posVars.get(fIndex);
              found = true;
              break;
            } 
          }
           
          if (!found) {// use the negative trace input
            input = negTrace.getVariableForStatement(posStmtIndex + 1, index);
          }

          indices.add(new VarIndex(input.getStmtIdx() + totStmts, input.getArgIdx()));
          sequences.add(input.owner);
          totStmts += input.owner.size();
        }

        Sequence newSeq = Sequence.concatenate(sequences);

        List<Variable> newInputVars = new ArrayList<Variable>(argSize);
        for (int index = 0; index < argSize; index++) {
          VarIndex vIdx = indices.get(index);
          newInputVars.add(new Variable(newSeq, vIdx.getStmtIdx(), vIdx.getArgIdx()));
        }
        // Execute the statement with new relevant inputs from positive trace
        // and other inputs from negative trace. So if the execution result is
        // Ok(normal) then which means input does influence the execution.
        boolean normal = RuntimeExecutor.execute(stmt, newInputVars);

        if (normal == true) {
          // If the new trace trace conforms with positive trace even though it
          // uses relevant inputs from the negative trace, this means the found
          // divider is valid on the input variables.
          return true;
        } else {
          // After changing the input parameters the execution still conforms
          // to the result of negative trace, so the found divider is invalid
          // on the input variables.
          continue;
        }
      }
    }

    return false;
  }

}
