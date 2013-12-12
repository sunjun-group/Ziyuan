package learner;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


import refiner.TzuYuRefinerV2;
import refiner.Witness;
import tester.TzuYuTesterV2;
import tzuyu.engine.bool.True;
import tzuyu.engine.experiment.JUnitFileWriter;
import tzuyu.engine.instrument.TzuYuInstrumentor;
import tzuyu.engine.model.Analytics;
import tzuyu.engine.model.Formula;
import tzuyu.engine.model.Query;
import tzuyu.engine.model.QueryResult;
import tzuyu.engine.model.QueryTrace;
import tzuyu.engine.model.Sequence;
import tzuyu.engine.model.Trace;
import tzuyu.engine.model.TzuYuAction;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.TzuYuException;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.model.dfa.TracesPair;
import tzuyu.engine.utils.Globals;
import lstar.LStar;
import lstar.Teacher;

public class QueryHandlerV2 implements Teacher {

  private LStar learner;
  private TzuYuTesterV2 tester;
  private TzuYuRefinerV2 refiner;
  private TzuYuAlphabet sigma;

  private boolean needRestart;
  private int maxMemberSize = 0;
  private int candidateCount = 1;
  
  public List<File> writeJUnitTestCases () {
    List<File> junitFiles = new ArrayList<File>();
    
    List<Sequence> sequences = this.tester.getAllTestCases();
    String targetClass = Analytics.getTarget().getSimpleName();
    String dir = Globals.userDir + Globals.fileSep + "testcases";
    int size = sequences.size();
    //We group all test cases into one file. The file name ends with the
    //suffix of the file number.
    JUnitFileWriter writer = new JUnitFileWriter(dir, "", targetClass, size);
    junitFiles.addAll(writer.createJUnitTestFiles(sequences));
    return junitFiles;
  }
  
  public int getCandidateCount() {
    return candidateCount;
  }

  public void setCandidateCount(int candidateCount) {
    this.candidateCount = candidateCount;
  }

  private int membershipCount = 0;
  private static final Logger logger = Logger.getRootLogger();

  public QueryHandlerV2(TzuYuAlphabet init) {
    sigma = init;
    tester = new TzuYuTesterV2(new TzuYuInstrumentor());
    learner = new LStar(this);
    refiner = new TzuYuRefinerV2();
  }
  
  public int getRefinementCount() {
    return refiner.getRefinementCount();
  }
  
  public int getSVMConsumedTime() {
    return refiner.getTimeConsumed();
  }

  /**
   * Learn the DFA for the classes under analysis. If any exceptions thrown 
   * return the DFA learned in last candidate query.
   * @return
   */
  public DFA learn() {
    int iterationCount = 0;
    do {
      needRestart = false;
      learner.setAlphabet(sigma);
      try {
        logger.info("-------restart iteration " + iterationCount++ + "-------");
        learner.start();
      } catch (TzuYuException tzuyu) {
        // handle TzuYu specific exceptions here
        tzuyu.printStackTrace(System.out);
        return null;
      }
    } while (needRestart);

    return learner.getDFA();
  }

  @Override
  public boolean membershipQuery(Trace str) {
    membershipCount++;
    // Update maximum membership query size
    if (str.size() > maxMemberSize) {
      maxMemberSize = str.size();
    }

    // Step 2: relay this query as membership query to the Oracle.
    Query query = new Query(str);

    QueryResult result = tester.test(query);

    if (query.isEpsilon()) {
      return true;
    }

    // Step 3: process the result from oracle membership query.
    if (result.positiveSet.size() == 0 && result.negativeSet.size() == 0) {
      // return the old value. Here we need to search the database
      // to find the counterpart. If there is no counterpart in the
      // database, we need to return true or false?

      List<QueryTrace> traces = result.unknownSet;

      QueryTrace queryTrace = traces.get(0);

      TzuYuAction stmt = queryTrace.getNextAction();
      boolean wishful = tester.confirmWishfulThinking(stmt);

      // Return true for unknown test trace as the wishful thinking, but we
      // need to know which guard condition failed in case latter we find
      // a passing trace for this guard, then we should return true for that
      // membership query.
      return wishful;
    } else if (result.positiveSet.size() == 0) {
      return false;
    } else if (result.negativeSet.size() == 0) {
      return true;
    } else {
      Formula divider = refiner.refineMembership(result);
      if (divider == null || divider.equals(Formula.FALSE)) {
        throw new TzuYuException("Cannot find "
            + "divider for the inconsistent transitons");
      }
      QueryTrace trace = result.negativeSet.get(0);

      TzuYuAction action = trace.query.getStatement(trace.lastActionIdx + 1);
      TzuYuAlphabet newSigma = sigma.refine(divider, action);

      
      if (newSigma.equals(sigma)) {
        return false;
      } else {
        sigma = newSigma;
        needRestart = true;
        // At this point, we need to notify the learner
        // to refine the alphabet and restart to learn.
        learner.stop();
        return false;
      }
    }
  }

  @Override
  public Trace candidateQuery(DFA dfa) {
    logger.info("------------Candidate Query Iteration " + candidateCount++
        + "------------------");
    dfa.print();
    logger.info("dfa state size: " + dfa.getStateSize());
    logger.info("alphabet size: " + (dfa.sigma.getSize() - 1));
    List<QueryTrace> traces = new ArrayList<QueryTrace>();
    
    // Step 1: Find all the generated test cases and execute them in order to 
    // get a better judgment of DFA based on more information (the old test 
    // cases). 
    QueryResult oldResult = tester.executeAllOldTestCases();
    
    // Step 2: Use automata testing strategies to generate the positive and
    // negative traces.
    TracesPair pair = dfa.randomWalk(maxMemberSize * 20, maxMemberSize + 3);

    List<Trace> acceptingTraces = pair.acceptingTraces;
    List<Trace> refusingTraces = pair.refusingTraces;
    logger.info("positive traces size:" + acceptingTraces.size());
    logger.info("negative traces size:" + refusingTraces.size());
    
    for (Trace trace : acceptingTraces) {
      Query query = new Query(trace);
      QueryResult result = tester.test(query);
      traces.addAll(result.positiveSet);
      traces.addAll(result.negativeSet);
    }

    for (Trace trace : refusingTraces) {
      Query query = new Query(trace);
      QueryResult result = tester.test(query);
      traces.addAll(result.positiveSet);
      traces.addAll(result.negativeSet);
    }
    
    // Step 3: merge old traces with the newly generated one by appending the 
    // old traces after the newly generated traces.
    traces.addAll(oldResult.positiveSet);
    traces.addAll(oldResult.negativeSet);
    
    Witness evid = refiner.refineCandidate(dfa, traces);

    if (!evid.success) {
      // If we can't find the counterpart data for the specified query,
      // we just return the inconsistent trace's query string as a
      // counterexample to L* and let L* do the control refinement
      logger.info("return counterexample.");
      return evid.counterexample;
    } else if (evid.divider == null) {
      // There is inconsistency, while TzuYu cannot find a divider,
      // we terminate TzuYu.
      throw new TzuYuException("cannot find a divider "
          + "for inconsistent transtion");
    } else if (evid.divider instanceof True) {
      return Trace.epsilon;
    }

    TzuYuAlphabet newSigma = sigma.refine(evid.divider, evid.action);
    logger.info("alphabet refined");
    if (newSigma.equals(sigma)) {
      // We find an alphabet which was found before, is this possible?
      return Trace.epsilon;
    } else {
      // At this point we need to notify the learner
      // to refine the alphabet and restart to learn.
      sigma = newSigma;
      needRestart = true;
      learner.stop();
      return Trace.epsilon;
    }
  }

  public int getMembershipCount() {
    return membershipCount;
  }

}
