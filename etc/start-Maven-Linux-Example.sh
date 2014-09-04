#!/bin/bash

BASE_PATH="/Users/truongkhanh/Documents/Code/Tzuyu"
echo BASE_PATH = $BASE_PATH

export TRUNK="$BASE_PATH/trunk"
echo TRUNK = $TRUNK

export M2_HOME=$BASE_PATH/tools/apache-maven-3.2.2
echo M2_HOME = $M2_HOME

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_67.jdk/Contents/Home
echo JAVA_HOME = $JAVA_HOME

PATH=$PATH:$M2_HOME/bin
#PATH=$PATH:$JAVA_HOME/bin

echo PATH = $PATH
cd $TRUNK/app/tzuyu.parent
mvn clean install -DskipTests
