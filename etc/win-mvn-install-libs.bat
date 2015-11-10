echo on
rem set environment
call setenv.bat

set LIBS=%TRUNK%\etc\libs

rem install wala
set WALA=%LIBS%\wala
call mvn install:install-file -Dfile=%WALA%\com.ibm.wala.core-1.3.4.jar -Dsources=%WALA%\com.ibm.wala.core-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4 -Dpackaging=jar
call mvn install:install-file -Dfile=%WALA%\com.ibm.wala.shrike-1.3.4.jar -Dsources=%WALA%\com.ibm.wala.shrike-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4 -Dpackaging=jar
call mvn install:install-file -Dfile=%WALA%\com.ibm.wala.util-1.3.4.jar -Dsources=%WALA%\com.ibm.wala.util-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4 -Dpackaging=jar

rem install javaslicer
set JAVA_SLICER=%LIBS%/javaslicer/assembly
set JAVA_SLICER_SRC=%LIBS%/javaslicer/src
call mvn install:install-file -Dfile=%JAVA_SLICER%/slicer.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=slicer -Dversion=20101004 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAVA_SLICER%/tracer.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=tracer -Dversion=20101004 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAVA_SLICER%/traceReader.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=traceReader -Dversion=20101004 -Dpackaging=jar
call mvn install:install-file -Dfile=%JAVA_SLICER%/visualize.jar -Dsources=%JAVA_SLICER_SRC%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=visualize -Dversion=20101004 -Dpackaging=jar

rem install javailp
set JAR_PATH=%LIBS%/javailp
call mvn install:install-file -Dfile=%JAR_PATH%/javailp1.2a.jar -DgroupId=net.sf.javailp -DartifactId=javailp -Dversion=1.2a -Dpackaging=jar

