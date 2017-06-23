@echo on
set BASE_PATH=F:\project
rem TRUNK is the folder where you check out the code, this must be the parent folder of "app" and "etc"
set TRUNK=%BASE_PATH%\Tzuyu
set M2_HOME=%BASE_PATH%\tools\apache-maven-3.0.4
set JAVA_HOME=%BASE_PATH%\tools\jdk1.6.0_26-64b

set PATH=%M2_HOME%\bin;%JAVA_HOME%\bin;%PATH%
set REPO_THIRD_PARTY_URL=http://202.94.70.100:8081/nexus/content/repositories/thirdparty
rem set REPO_THIRD_PARTY_URL=http://localhost:8081/nexus/content/repositories/thirdparty/
rem set REPO_THIRD_PARTY_URL=http://52.89.202.3:8081/nexus/content/repositories/thirdparty

