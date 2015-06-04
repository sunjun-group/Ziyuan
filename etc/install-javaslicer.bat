echo on
call setenv.bat
set JARS_PATH=%TRUNK%/etc/javaslicer/assembly
set SRC_PATH=%TRUNK%/etc/javaslicer/src

call mvn install:install-file -Dfile=%JARS_PATH%/slicer.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=slicer -Dversion=20101004 -Dpackaging=jar

call mvn install:install-file -Dfile=%JARS_PATH%/tracer.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=tracer -Dversion=20101004 -Dpackaging=jar

call mvn install:install-file -Dfile=%JARS_PATH%/traceReader.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=traceReader -Dversion=20101004 -Dpackaging=jar

call mvn install:install-file -Dfile=%JARS_PATH%/visualize.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=visualize -Dversion=20101004 -Dpackaging=jar

rem THIS PART IS FOR DEPLOYING TO NEXUS SERVER
rem call mvn deploy:deploy-file -Dfile=%JARS_PATH%/slicer.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=slicer -Dversion=20101004 -Dpackaging=jar -DrepositoryId=tzuyu-thirdparty -Durl=http://202.94.70.100:8081/nexus/content/repositories/thirdparty

rem call mvn deploy:deploy-file -Dfile=%JARS_PATH%/tracer.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=tracer -Dversion=20101004 -Dpackaging=jar -DrepositoryId=tzuyu-thirdparty -Durl=http://202.94.70.100:8081/nexus/content/repositories/thirdparty

rem call mvn deploy:deploy-file -Dfile=%JARS_PATH%/traceReader.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=traceReader -Dversion=20101004 -Dpackaging=jar -DrepositoryId=tzuyu-thirdparty -Durl=http://202.94.70.100:8081/nexus/content/repositories/thirdparty

rem call mvn deploy:deploy-file -Dfile=%JARS_PATH%/visualize.jar -Dsources=%SRC_PATH%/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=visualize -Dversion=20101004 -Dpackaging=jar -DrepositoryId=tzuyu-thirdparty -Durl=http://202.94.70.100:8081/nexus/content/repositories/thirdparty
