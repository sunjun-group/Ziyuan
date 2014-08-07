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