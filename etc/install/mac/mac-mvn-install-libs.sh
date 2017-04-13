#!/bin/bash
LIBS=$TRUNK/etc/libs

# install wala
WALA=$LIBS/wala
echo WALA = $WALA
mvn install:install-file -Dfile=$WALA/com.ibm.wala.core-1.3.4.jar -Dsources=$WALA/com.ibm.wala.core-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4 -Dpackaging=jar
mvn install:install-file -Dfile=$WALA/com.ibm.wala.shrike-1.3.4.jar -Dsources=$WALA/com.ibm.wala.shrike-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4 -Dpackaging=jar
mvn install:install-file -Dfile=$WALA/com.ibm.wala.util-1.3.4.jar -Dsources=$WALA/com.ibm.wala.util-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4 -Dpackaging=jar

# install javaslicer
JAVA_SLICER=$LIBS/javaslicer/assembly
echo JAVA_SLICER = $JAVA_SLICER
JAVA_SLICER_SRC=$LIBS/javaslicer/src
mvn install:install-file -Dfile=$JAVA_SLICER/slicer.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=slicer -Dversion=20101004 -Dpackaging=jar
mvn install:install-file -Dfile=$JAVA_SLICER/tracer.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=tracer -Dversion=20101004 -Dpackaging=jar
mvn install:install-file -Dfile=$JAVA_SLICER/traceReader.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=traceReader -Dversion=20101004 -Dpackaging=jar
mvn install:install-file -Dfile=$JAVA_SLICER/visualize.jar -Dsources=$JAVA_SLICER_SRC/javaslicer_src_20101004.zip -DgroupId=de.unisb.cs.st -DartifactId=visualize -Dversion=20101004 -Dpackaging=jar

# install javailp
JAVAILP=$LIBS/javailp
echo JAVAILP = $JAVAILP
mvn install:install-file -Dfile=$JAVAILP/javailp1.2a.jar -DgroupId=net.sf.javailp -DartifactId=javailp -Dversion=1.2a -Dpackaging=jar


# install jacoco
GROUP_ID=org.jacoco
VERSION=0.7.10-SNAPSHOT
JACOCO=$LIBS/jacoco
FILE=$JACOCO/jacoco-core/$VERSION/org.jacoco.core-$VERSION.jar
SOURCES=$JACOCO/jacoco-core/$VERSION/org.jacoco.core-$VERSION-sources.jar
ARTIFACT_ID=org.jacoco.core
echo FILE = $FILE
echo SOURCES=$SOURCES
mvn install:install-file -Dfile=$FILE -Dsources=$SOURCES -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dpackaging=jar

FILE=$JACOCO/jacoco-agent/$VERSION/org.jacoco.agent-$VERSION.jar
SOURCES=$JACOCO/jacoco-agent/$VERSION/org.jacoco.agent-$VERSION-sources.jar
ARTIFACT_ID=org.jacoco.agent
echo FILE = $FILE
echo SOURCES=$SOURCES
mvn install:install-file -Dfile=$FILE -Dsources=$SOURCES -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dpackaging=jar
