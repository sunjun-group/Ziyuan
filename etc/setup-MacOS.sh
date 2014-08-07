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