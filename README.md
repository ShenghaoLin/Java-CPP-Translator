# Translator Java to C++

This is a work in progress.

The given task to translate Java programs to C++ was divided in 5 phases. 
You may run each separately or simply run the entire translator. The commands follow bellow.

Before running, initiate a SBT shell, compile and run Boot.java

### To run phase x on SBT, type:

`runxtc -printPhasex <source file>`

The output for phases 1, 2 and 4 are prompted in the shell. 
The output for phases 3 and 4 are in the output directory.

### To run the tests for phase x, type:

`test-only *Phasex*` 

### To run translator, type:

`runxtc -translate [path/to/input]`

### To generate the documentation for phase x, open a terminal shell and type:

`javadoc Phasex`

