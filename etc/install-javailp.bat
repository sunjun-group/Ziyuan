echo on
call setenv.bat
set JAR_PATH=%TRUNK%\etc\libs

call mvn install:install-file -Dfile=%JAR_PATH%\javailp1.2a.jar -DgroupId=net.sf.javailp -DartifactId=javailp -Dversion=1.2a -Dpackaging=jar
