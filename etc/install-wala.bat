echo on
call setenv.bat
set WALA_JARS_PATH=%TRUNK%\etc\wala
set JAVAML_JARS_PATH=%TRUNK%\etc\javaml
call mvn install:install-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.core-1.3.4.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.core-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar
call mvn install:install-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.shrike-1.3.4.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.shrike-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar
call mvn install:install-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.util-1.3.4.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.util-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar

rem THIS PART IS FOR DEPLOYING TO NEXUS SERVER
rem call mvn deploy:deploy-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.core-1.3.4.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.core-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=tzuyu-thirdparty -Durl=http://202.94.70.100:8081/nexus/content/repositories/thirdparty
rem call mvn deploy:deploy-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.shrike-1.3.4.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.shrike-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=tzuyu-thirdparty -Durl=http://202.94.70.100:8081/nexus/content/repositories/thirdparty
rem call mvn deploy:deploy-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.util-1.3.4.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.util-1.3.4-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4 -Dpackaging=jar -DrepositoryId=tzuyu-thirdparty -Durl=http://202.94.70.100:8081/nexus/content/repositories/thirdparty