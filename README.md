Ziyuan
=====
Introduction
------------
Ziyuan (namely after Confucius's smartest student) is designed to be a self-contained package for analyzing Java programs.

Documentation
-------------


Licensing
---------
Ziyuan is free and open source for acedemic research.    
  
Requirements
------------


Set up
------------
1.	Check out source code from Github: https://github.com/sunjun-group/Ziyuan.
	- Project folder includes 2 main sub folders, /app (store code), and /etc (store project settings, libs, ..etc).
2.	Update Maven settings:
	- goto /etc/maven, copy settings.xml --> local-settings.xml
	- open local-settings.xml: 
		+ modify this line and set it to your local maven repository folder [upon your choice].<br/>
		&lt;localRepository&gt;D:/_1_Projects/Tzuyu/maven-repository&lt;/localRepository&gt; <br/> 
					
3.	Make sure Ziyuan java dependencies will be available in local repository:
	- For the setting in maven pom.xml of tzuyu.parent, some special dependency are deployed onto nexus server which url is declared in
	settings.xml (or your local-settings.xml), and will be downloaded automatically during build process.	
	- Check if the nexus server is available or not:
		Open the url defined in your local-settings.xml to your web browser, if it renders the nexus page then the server is available.
	- If server is available, you can jump to step 5. 
	- If not available, 
		+ open local-settings.xml, comment this line:  
			&lt;activeProfile&gt;nexus-sever&lt;/activeProfile&gt;
		+ Go to step 4.
4. 	Install jars:	
	- Download maven (binary package) at https://maven.apache.org/download.cgi
	- Unzip maven to folder [mvn folder]
	- copy and override /etc/maven/local-settings.xml --> [mvn folder]/conf/settings.xml
	- Go to /etc
	For Window:
		+ copy win-setenv-example.bat --> setenv.bat
		+ modify setenv.bat, set absolute path to the folders on your local machine.
		+ run win-mvn-install-libs.bat 
	For Linux:
		+ copy linux-runall-example.sh --> linux-runall.sh
		+ modify linux-runall.sh, set absolute path to the folders on your local machine.
		+ run linux-runall.sh
5.	Set up Eclipse, and import project:
	- Open Eclipse.
	- Make sure that m2e plugin (maven to eclipse plugin) is installed.
		+ Go to Help/About Eclipse/Installation Details. If it was installed, you must see "m2e-Maven Integration For Eclipse" on the list.
		+ To install m2e plugin:
				<br/>Go to Help/Install New Software, parse "http://download.eclipse.org/technology/m2e/releases" to text field "Work with".
				<br/>In displayed table, select "Maven Integration For Eclipse", click "Next" --> ... --> "Finish"
	- Update maven settings:
		+ Go to Window/Preferences/Maven/User Settings
		+ Browse User Settings to /etc/maven/local-settings.xml -> apply, ok
	- All necessary settings (.classpath, .project) are ready for eclipse. 
		+ Click on File/Import...
		+ Select General/Existing Projects into workspace 
		+ Browse to Ziyuan project folder, select these modules:[sav.commons, slicer.wala, slicer.javaslicer, codecoverage.jacoco, cfgcoverage.jacoco, svmlib, faultLocalization, mutation, gentest, icsetlv, tzuyu.core, tzuyu, assertion, invariant.templates], 
				then click "finish".
		+ Wait until the building proccess finishes (this may take a while).	
6. Configuration for ilpsolver:
    Following the instruction in /etc/libs/javailp/javailp-native/readme.txt.
7.	Run the application:
	- run tzuyu.core.main.TzuyuCoreDemoTest.testStudentEvaluate2()
	- if everything is setup properly, we should get learning result in the console view.

Contacts
--------
lylypp6987@gmail.com
sunjun@sutd.edu.sg
