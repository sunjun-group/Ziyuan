#!/bin/bash
BASE_PATH="/Users/lylytran/Projects"
# TRUNK is the folder where you check out the code, this must be the parent folder of "app" and "etc"
export TRUNK="$BASE_PATH/Ziyuan-branches/learntest-eclipse"

#export REPO_THIRD_PARTY_URL=http://202.94.70.100:8081/nexus/content/repositories/thirdparty
#export REPO_THIRD_PARTY_URL=http://localhost:8081/nexus/content/repositories/thirdparty/
#export REPO_THIRD_PARTY_URL=http://52.89.202.3:8081/nexus/content/repositories/thirdparty

echo JAVA_HOME = $JAVA_HOME
echo M2_HOME = $M2_HOME
echo PATH = $PATH
echo REPO_THIRD_PARTY_URL=$REPO_THIRD_PARTY_URL

sh ./mac-mvn-install-libs.sh
