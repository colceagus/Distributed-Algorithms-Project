Project Phase 1
The project is about studying different distributed algorithms for maintaining the consistency of the
shared state in a collaborative text editor.
The text editor takes two types of commands: Ins(C,P) which means the insertion of character C to
position P and Del(P) meaning the deletion of the character at position P.
For the first phase, the students are required to:
1. Implement such a text editor with two main modules: UI module and Communication module.
The two modules should be very clearly separated. Throughout the semester, the UI module will
stay pretty much the same, but the Communication module will change.
2. For Phase 1, four algorithms have to be implemented in the Communication module:
a. CBCAST
b. ABCAST
c. 3 Phase.
3. Also for Phase 1, a testing simulator has to be implemented to drive a text editor. The simulator
will be a different program that will read a file like:
Cmd1, cmd2, cmd3 and will run the commands with a random waiting interval between
100ms and 500ms. The tests have to be done with at least 3 nodes. The yesting file with
commands will be supplied on the site. Each site has to execute the commands in parallel.
4. A bonus of 2 points will be awarded if CBCAST is shown not to preserve the global consistency.
The deadline for Phase 1 is March 27 and have to presented in person by each student during the lab.
