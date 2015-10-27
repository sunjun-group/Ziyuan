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


Quick start
------------
Requirement:
- maven (apache-maven-3.0.4)
- jdk1.6
- eclipse which is already installed these plugins: m2e(maven plugin), git (optional)

Configuration:
- Update Maven settings: the example for maven settings is in /etc/maven/settings.xml,
un comment the setting for tools.jar, and modify the path to [jdk folder]/lib/tools.jar in your local machine.
        <!-- 			<properties> -->
				<!-- 				<toolsjar>[../jdk1.6.../lib/tools.jar</toolsjar> -->
				<!-- 				<toolsjar-version>1.6...</toolsjar-version> -->
				<!-- 			</properties> -->

- Import to Eclipse:
	- Make sure that m2e plugin (maven to eclipse plugin) is installed.
	- Update maven settings as above.
	- All necessary settings (.classpath, .project) are ready for eclipse. 

Check Ziyuan_Installation_Note.txt in /etc/doc for more detail about the installation.

Contacts
--------
lylypp6987@gmail.com
spencerxiao@sutd.edu.sg
sunjun@sutd.edu.sg
