echo on
rem set environment
call setenv.bat

cd %TRUNK%/app/tzuyu.parent
call mvn deploy -DskipTests