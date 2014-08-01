echo on
call setenv.bat
set WALA_JARS_PATH=%TRUNK%\etc\wala
set JAVAML_JARS_PATH=%TRUNK%\etc\javaml
call mvn install:install-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.core-1.3.4-SNAPSHOT.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.core-1.3.4-SNAPSHOT-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.core -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar
call mvn install:install-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.shrike-1.3.4-SNAPSHOT.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.shrike-1.3.4-SNAPSHOT-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.shrike -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar
call mvn install:install-file -Dfile=%WALA_JARS_PATH%\com.ibm.wala.util-1.3.4-SNAPSHOT.jar -Dsources=%WALA_JARS_PATH%\com.ibm.wala.util-1.3.4-SNAPSHOT-src.zip -DgroupId=com.ibm.wala -DartifactId=com.ibm.wala.util -Dversion=1.3.4-SNAPSHOT -Dpackaging=jar
call mvn install:install-file -Dfile=%JAVAML_JARS_PATH%\javaml-0.1.7.jar -Dsources=%JAVAML_JARS_PATH%\javaml-0.1.7-src.zip -DgroupId=net.sourceforge -DartifactId=javaml -Dversion=0.1.7 -Dpackaging=jar