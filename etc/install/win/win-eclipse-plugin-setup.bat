echo on
call setenv.bat
cd %TRUNK%\app\tzuyuEclipsePlugin

rem set up links for tzuyu reference.
mklink  /D tzuyuCore %TRUNK%\app\tzuyu\target\classes

rem set up links for icsetlv reference.
mklink  /D icsetlvCore %TRUNK%\app\icsetlv\target\classes

rem set up links for sav.commons reference
mklink  /D savCommons %TRUNK%\app\sav.commons\target\classes
