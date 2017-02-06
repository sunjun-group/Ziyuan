 #!/bin/bash

 # set path to eclipse folder. If the same folder as this script, use the default; otherwise, use /path/to/eclipse/
 eclipsehome=/home/projects/Tzuyu/tools/eclipse-standard-luna-R-linux-gtk-x86_64/eclipse;

 # get path to equinox jar inside $eclipsehome folder
 cp=$(find $eclipsehome -name "org.eclipse.equinox.launcher_*.jar" | sort | tail -1);

 # start Eclipse w/ java
/home/projects/Tzuyu/tools/java-7-openjdk-amd64/bin/java -cp $cp org.eclipse.equinox.launcher.Main ...

