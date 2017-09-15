Learntest-eclipse
=====
Introduction
------------

Documentation
-------------


Licensing
---------
Learntest-eclipse is free and open source for acedemic research.    
  
Requirements
------------


Set up
------------
1.	Check out source code from Github: https://github.com/sunjun-group/Ziyuan; branch learntest-eclipse_v1.2.0
	- Project folder includes 2 main sub folders, /app (store code), and /etc (store project settings, libs, ..etc).
2.	Set up for dependencies:	
    - JavaIlp: read instructions in /etc/libs/javailp-native/readme.txt.
    - z3prover: read /etc/libs/z3prover/readme.txt.
3.	Set up Eclipse, and import project:
	- Open Eclipse.
    - Set up Plug-in API baseline: 
        + Go to Windows/Preferences/Plug-in Development/API Baselines, (You can find the instruction here: https://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Ftasks%2Fapi_tooling_baseline.htm). For easy, on New API Baseline wizard, you can select the option "An existing Eclipse installtion directory". Recomended eclipse version is: [TBD]
	- The project already included all necessary settings (.classpath, .project) which are ready to import into eclipse. 
		+ Click on File/Import...
		+ Select General/Existing Projects into workspace 
		+ Browse to Learntest-eclipse.v1.2.0 project folder, and select these following modules:[cfgextractor, gentest, icsetlv, jdart, learntest, sav.commons, svmlib], 
				then click "finish".
		+ Wait until the building proccess finishes (this may take a while).		
4.	Run the application:
	- In Run/Run Configurations, select learntest-eclipse under Eclipse Application option to run or debug.

Contacts
--------
llmhyy@gmail.com,
lylypp6987@gmail.com,
sunjun@sutd.edu.sg
