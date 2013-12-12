package tzuyu.engine.main;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import analyzer.ClassAnalyzer;

//import learner.QueryHandler;
import learner.QueryHandlerV2;


import tzuyu.engine.model.Analytics;
import tzuyu.engine.model.ClassInfo;
import tzuyu.engine.model.TzuYuAlphabet;
import tzuyu.engine.model.dfa.DFA;
import tzuyu.engine.utils.Options;

public class Learn implements CommandHandler {

  private static final Logger logger = Logger.getRootLogger();

  @Override
  public boolean handle(Command command) {
    processCommand(command);
    TzuYuAlphabet initial = TzuYuAlphabet.constructInitialAlphabet();
    // QueryHandler handler = new QueryHandler(initial);
    QueryHandlerV2 handler = new QueryHandlerV2(initial);
    long startTime = System.currentTimeMillis();
    logger.info("============Start of Statistics for "
        + Analytics.getTarget().getSimpleName() + "============");
    DFA dfa = handler.learn();
    
    if (dfa == null) {
      return false;
    }
    
    long endTime = System.currentTimeMillis();
    logger.info("Alphabet Size in Final DFA: " + (dfa.sigma.getSize() -1));
    logger.info("Number of States in Final DFA: " + dfa.getStateSize());
    dfa.print();
    saveDFA(dfa);
    logger.info("Total NO. of membership queries: " + 
    handler.getMembershipCount());
    logger.info("Total NO. of candidate queries: " + 
    handler.getCandidateCount());
    logger.info("Total NO. of SVM Calls: " + handler.getRefinementCount());
    logger.info("Total Time consumed by SVM: " + handler.getSVMConsumedTime());

    logger.info("Total Time: " + (endTime - startTime) + " ms.");
    logger.info("============End of Statistics for "
        + Analytics.getTarget().getSimpleName() + "============");
    System.out.print("=========== Starting to print out JUnit test cases=====");
    handler.writeJUnitTestCases();
    System.out.print("================= End of TzuYu run ====================");
    return true;
  }

  @Override
  public boolean canHandle(Command command) {
    if (command.cmdStr.equals("learn")) {
      return true;
    }
    return false;
  }

  private void processCommand(Command cmd) {

    Class<?> targetClass = null;
    try {
      String className = Option.target.getValue();
      if (className.equals("")) {
        logger.error("Target class is not specified, system aborts");
        System.exit(1);
      }

      targetClass = Class.forName(className);
    } catch (ClassNotFoundException e) {
      logger.error("Invalid target class is specified, system aborts");
      System.exit(1);
    }

    String outDir = Option.output.getValue();
    File output = null;
    if (!outDir.equals("")) {
      output = new File(Option.output.getValue());
      if (!output.exists() || !output.isDirectory()) {
        logger.error("Invalid output directory, use the default");
        output = null;
      }
    }

    List<String> methods = Option.methods.getValue();

    ClassAnalyzer analyzer = new ClassAnalyzer(targetClass, methods);
    Map<Class<?>, ClassInfo> classes = analyzer.analysis();
    Analytics.initialize(targetClass, classes);

    boolean object2Int = Option.object2Integer.getValue();
    boolean im = Option.inheritedMethods.getValue();
    //boolean object2Int = false;
    int tpq = Option.testsPerQuery.getValue();

    Options.setOptions(object2Int, tpq, output, im);
  }

  private void saveDFA(DFA dfa) {
    if (dfa != null) {
      String dot = dfa.createDotRepresentation();
      try {
        String fileName = Options.getAbsoluteAddress(Analytics.getTarget()
            .getSimpleName() + ".dot");
        FileWriter writer = new FileWriter(fileName);
        writer.write(dot);
        writer.close();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }


}
