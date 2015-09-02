echo on
rem set environment
call setenv.bat

set LIBS=%TRUNK%\etc\libs
set REPO_ID=thirdparty
set REPO_URL=http://202.94.70.100:8081/nexus/content/repositories/thirdparty
rem set REPO_URL=http://localhost:8081/nexus/content/repositories/thirdparty

rem install wala
set WALA=%LIBS%\wala
call mvn deploy:deploy-file -Dfile=%WALA%\com.ibm.wala.core-1.3.4.jar -Dsources=%WALA%\com.ibm.wala.core-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%
call mvn deploy:deploy-file -Dfile=%WALA%\com.ibm.wala.shrike-1.3.4.jar -Dsources=%WALA%\com.ibm.wala.shrike-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%
call mvn deploy:deploy-file -Dfile=%WALA%\com.ibm.wala.util-1.3.4.jar -Dsources=%WALA%\com.ibm.wala.util-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%

rem install javaslicer
set JAVA_SLICER=%LIBS%/javaslicer/assembly
set JAVA_SLICER_SRC=%LIBS%/javaslicer/src
call mvn deploy:deploy-file -Dfile=%JAVA_SLICER%/slicer.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=slicer -Dversion=20101004 -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%
call mvn deploy:deploy-file -Dfile=%JAVA_SLICER%/tracer.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=tracer -Dversion=20101004 -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%
call mvn deploy:deploy-file -Dfile=%JAVA_SLICER%/traceReader.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=traceReader -Dversion=20101004 -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%
call mvn deploy:deploy-file -Dfile=%JAVA_SLICER%/visualize.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=visualize -Dversion=20101004 -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%

rem install javailp
set JAR_PATH=%LIBS%/javailp
call mvn deploy:deploy-file -Dfile=%JAR_PATH%/javailp1.2a.jar -DgroupId=net.sf.javailp -DartifactId=javailp -Dversion=1.2a -Dpackaging=jar -DrepositoryId=%REPO_ID% -Durl=%REPO_URL%

