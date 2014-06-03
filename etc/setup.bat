echo on
call setenv.bat
cd %TRUNK%\app\tzuyuEclipsePlugin
mklink  /D tzuyuCore %TRUNK%\app\tzuyu\target\classes
mklink  /D tzuyuLib %TRUNK%\app\tzuyu\lib
cd %TRUNK%\app\tzuyu.parent
call mvn clean verify
call mvn eclipse:eclipse