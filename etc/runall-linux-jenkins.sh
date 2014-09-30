#!/bin/bash

BASE_PATH=.
echo BASE_PATH = $BASE_PATH

export TRUNK=$BASE_PATH
echo TRUNK = $TRUNK

#export M2_HOME=$BASE_PATH/tools/apache-maven-3.2.2
#echo M2_HOME = $M2_HOME

export M2_BIN=$M2_HOME/bin

#export JAVA_HOME=$BASE_PATH/tools/jdk-7u67-linux-x64/jdk1.7.0_67
#echo JAVA_HOME = $JAVA_HOME

#PATH=$PATH:$M2_HOME/bin
#PATH=$PATH:$JAVA_HOME/bin

echo PATH = $PATH

sh etc/setup-linux.sh
sh etc/install-wala-linux.sh
sh etc/install-javaslicer-linux.sh
