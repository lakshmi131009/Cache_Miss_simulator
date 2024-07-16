sample compilation and running

gcc read_traces.c -o t
$ ./t sphinx3.log_l1misstrace 2

cache simulation code is there in Analysis.java file

pass the file which you want to test by changing the file name in the Analysis.java file
 File myObj = new File("bzip.txt");
change above line in the Analysis.java file to
 File myObj = new File("gcc.txt"); if you want

-----------------------------------------------------------------------------------------------
part A:
for running Analysis.java file

javac Analysis.java
java Analysis
