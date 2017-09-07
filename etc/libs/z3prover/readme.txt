SET UP FOR Z3prover (using in jdart)
========================================
1. Copy files into system folder
	For Windows: goto folder ./windows
		copy libz3.dll and libz3java.dll in folder ./system to folder Windows/System32
	For Linux: 
		copy libz3.so and libz3java.so to a global library folder or to one that is contained in your java.library.path property (the LD_LIBRARY_PATH config, by default, it is: “/usr/local/lib”).
	For Mac: goto folder ./mac
		copy libz3.dylib and libz3java.dylib into /usr/local/lib
		copy libz3.dylib and libz3java.dylib into /Library/Java/Extensions (DYLD_LIBRARY_PATH)

2. Make sure libz3.dylib and libz3java.dylib are located in resources folder. (It’s already set up in learntest project)

