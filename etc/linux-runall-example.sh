#!/bin/bash
BASE_PATH="/home/lylytran/projects/Tzuyu"

export TRUNK="$BASE_PATH/workspace/trunk"
export M2_HOME=$BASE_PATH/tools/apache-maven-3.2.2
export M2_BIN=$M2_HOME/bin
export JAVA_HOME=$BASE_PATH/tools/jdk-7u67-linux-x64/jdk1.7.0_67
PATH=$PATH:$M2_HOME/bin
PATH=$PATH:$JAVA_HOME/bin

echo JAVA_HOME = $JAVA_HOME
echo M2_HOME = $M2_HOME
echo PATH = $PATH

#sh ./linux-eclipse-plugin-setup.sh
sh ./linux-mvn-install-libs.sh

