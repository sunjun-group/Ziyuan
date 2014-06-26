echo on
call setenv.bat
cd %TRUNK%\app\tzuyuEclipsePlugin
mklink  /D tzuyuCore %TRUNK%\app\tzuyu\target\classes
mklink  /D tzuyuLib %TRUNK%\app\tzuyu\lib
cd %TRUNK%\app\tzuyu.parent
rem call mvn clean install
rem call mvn eclipse:eclipse