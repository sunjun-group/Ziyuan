#!/bin/bash
LIBS=$TRUNK/etc/libs
REPO_ID=thirdparty
REPO_URL=http://202.94.70.100:8081/nexus/content/repositories/thirdparty

cd $M2_BIN

# install wala
WALA=$LIBS/wala
echo WALA = $WALA
mvn deploy:deploy-file -Dfile=$WALA/com.ibm.wala.core-1.3.4.jar -Dsources=$WALA/com.ibm.wala.core-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL
mvn deploy:deploy-file -Dfile=$WALA/com.ibm.wala.shrike-1.3.4.jar -Dsources=$WALA/com.ibm.wala.shrike-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL
mvn deploy:deploy-file -Dfile=$WALA/com.ibm.wala.util-1.3.4.jar -Dsources=$WALA/com.ibm.wala.util-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL

# install javaslicer
JAVA_SLICER=$LIBS/javaslicer/assembly
echo JAVA_SLICER = $JAVA_SLICER
JAVA_SLICER_SRC=$LIBS/javaslicer/src
mvn deploy:deploy-file -Dfile=$JAVA_SLICER/slicer.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=slicer -Dversion=20101004 -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL
mvn deploy:deploy-file -Dfile=$JAVA_SLICER/tracer.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=tracer -Dversion=20101004 -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL
mvn deploy:deploy-file -Dfile=$JAVA_SLICER/traceReader.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=traceReader -Dversion=20101004 -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL
mvn deploy:deploy-file -Dfile=$JAVA_SLICER/visualize.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=visualize -Dversion=20101004 -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL

# install javailp
JAVAILP=$LIBS/javailp
echo JAVAILP = $JAVAILP
mvn deploy:deploy-file -Dfile=$JAVAILP/javailp1.2a.jar -DgroupId=net.sf.javailp -DartifactId=javailp -Dversion=1.2a -Dpackaging=jar -DrepositoryId=$REPO_ID -Durl=$REPO_URL