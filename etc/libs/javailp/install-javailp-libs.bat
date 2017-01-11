echo on
call setenv.bat
set JAR_PATH=D:\_1_Projects\Tzuyu\tools\javailp\branches\1.2\1.2a\lib

call mvn install:install-file -Dfile=%JAR_PATH%\org.sat4j.core.jar -DgroupId=org.sat4j.core -DartifactId=org.sat4j.core -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAR_PATH%\mosek.jar -DgroupId=mosek -DartifactId=mosek -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAR_PATH%\glpk-java.jar -DgroupId=glpk -DartifactId=glpk -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAR_PATH%\org.sat4j.pb.jar -DgroupId=org.sat4j.pb -DartifactId=org.sat4j.pb -Dversion=2.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAR_PATH%\cplex.jar -DgroupId=cplex -DartifactId=cplex -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAR_PATH%\lpsolve55j.jar -DgroupId=lpsolve55j -DartifactId=lpsolve55j -Dversion=5.5 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAR_PATH%\gurobi.jar -DgroupId=gurobi -DartifactId=gurobi -Dversion=3.0 -Dpackaging=jar