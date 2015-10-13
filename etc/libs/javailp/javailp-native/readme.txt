SET UP FOR JAVAILP
========================================
1. Copy files into system folder
	For Windows:
		copy lpsolve55.dll and liblpsolve55.so in folder /system to folder Windows/System32

	For Mac:
		copy liblpsolve55.dylib into /usr/local/lib
		copy liblpsolve55j.jnilib into /Library/Java/Extensions
		------------------------------------------------------------------------------------------------
		[These files are built following the guide in Read Guide to use lp_solve in Java on Mac OS X.txt]
		
2. Indicate native library path
	- Using program parameter:
		add -Djava.library.path=[.../[Ziyuan project folder]/etc/libs/javailp-native] into VM Arguments under Run Configuration.
	- Or set Native library location if running in eclipse
		+ go to [project] properties/ Java Build Path / Libraries
		+ uncollapse Maven Dependencies, select Native Library location, hit Edit button on the right and enter location path. 

