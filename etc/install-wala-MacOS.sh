#!/bin/bash

BASE_PATH=/Users/truongkhanh/Documents/workspace
echo $BASE_PATH

TRUNK=$BASE_PATH/Tzuyu
echo $TRUNK

M2_HOME=$BASE_PATH/apache-maven-3.2.2
echo $M2_HOME

JAVA_HOME=/usr/bin/java
echo $JAVA_HOME

PATH=$PATH:$M2_HOME/bin
PATH=$PATH:$JAVA_HOME/bin

echo $PATH

cd $TRUNK/app/tzuyuEclipsePlugin
ln -s $TRUNK/app/tzuyu/target/classes tzuyuCore
ln -s $TRUNK/app/icsetlv/target/classes icsetlvCore
ln -s $TRUNK/app/sav.commons/target/classes savCommons



WALA_JARS_PATH=$TRUNK/etc/wala

echo Print BASE_PATH
echo $BASE_PATH
cd $BASE_PATH/apache-maven-3.2.2/bin

./mvn install:install-file -Dfile=$WALA_JARS_PATH/com.ibm.wala.core-1.3.4-SNAPSHOT.jar -Dsources=$WALA_JARS_PATH/com.ibm.wala.core-1.3.4-SNAPSHOT-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar

./mvn install:install-file -Dfile=$WALA_JARS_PATH/com.ibm.wala.shrike-1.3.4-SNAPSHOT.jar -Dsources=$WALA_JARS_PATH/com.ibm.wala.shrike-1.3.4-SNAPSHOT-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar

./mvn install:install-file -Dfile=$WALA_JARS_PATH/com.ibm.wala.util-1.3.4-SNAPSHOT.jar -Dsources=$WALA_JARS_PATH/com.ibm.wala.util-1.3.4-SNAPSHOT-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar