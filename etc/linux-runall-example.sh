#!/bin/bash
BASE_PATH="/home/lylytran/projects/Tzuyu"
# TRUNK is the folder where you check out the code, this must be the parent folder of "app" and "etc"
export TRUNK="$BASE_PATH/workspace/trunk"
export M2_HOME=$BASE_PATH/tools/apache-maven-3.2.2
export M2_BIN=$M2_HOME/bin
export JAVA_HOME=$BASE_PATH/tools/jdk-7u67-linux-x64/jdk1.7.0_67
PATH=$PATH:$M2_HOME/bin
PATH=$PATH:$JAVA_HOME/bin

#export REPO_THIRD_PARTY_URL=http://202.94.70.100:8081/nexus/content/repositories/thirdparty
#export REPO_THIRD_PARTY_URL=http://localhost:8081/nexus/content/repositories/thirdparty/
export REPO_THIRD_PARTY_URL=http://52.89.202.3:8081/nexus/content/repositories/thirdparty

echo JAVA_HOME = $JAVA_HOME
echo M2_HOME = $M2_HOME
echo PATH = $PATH
echo REPO_THIRD_PARTY_URL=$REPO_THIRD_PARTY_URL

sh ./linux-mvn-install-libs.sh
#sh ./linux-mvn-deploy-libs.sh
#sh ./linux-mvn-deploy-Ziyuan.sh