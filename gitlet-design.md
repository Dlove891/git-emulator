# Gitlet Design Document
author: Dylan Love 

## Design Document Guidelines

Please use the following format for your Gitlet design document. Your design
document should be written in markdown, a language that allows you to nicely 
format and style a text file. Organize your design document in a way that 
will make it easy for you or a course-staff member to read.  

## 1. Classes and Data Structures

Include here any class definitions. For each class list the instance
variables and static variables (if any). Include a ***brief description***
of each variable and its purpose in the class. Your explanations in
this section should be as concise as possible. Leave the full
explanation to the following sections. You may cut this section short
if you find your document is too wordy.

### Main.java
This class is the starting point of the program. It implements various methods to set up persistance.

#### Fields

1. static final File CWD: A pointer to the current working directory of the program.
2. static final File GITLET_FOLDER: A pointer to the `GITLET` directory in the current working directory

### Repo.java
This class is what is accessed from Main.java when a  Directory is turned into a version controlable directory accessible by git commands. Stores any methods that access and save and edit files.  

#### Fields

1. static final File REPO_FOLDER: A pointer to the directory persisting repo instances.
2. File _master: a pointer to the latest commit.
3. ArrayList _history: holds all commit objects in an ArrayList. 
4. ArrayList  _stagingarea: Hold all the added files that have not yet been committed.


### Commit.java
This class is will create an object that stores data pertaining to commits. 

##### Fields


1. String _message: Passed in from Main args. Basically a label for the commit. 
2. String _timestamp: using java packages, find the real world timestamp of when the file was committed.
3. String _selfKey: Serialization of commit object that represents the commit that was just done. 
4. String _parentKey: Serialization of commit object that represents the commit of the parent of the commit that was just done.


## 2. Algorithms

This is where you tell us how your code works. For each class, include
a high-level description of the methods in that class. That is, do not
include a line-by-line breakdown of your code, but something you would
write in a javadoc comment above a method, ***including any edge cases
you are accounting for***. We have read the project spec too, so make
sure you do not repeat or rephrase what is stated there.  This should
be a description of how your code accomplishes what is stated in the
spec.


The length of this section depends on the complexity of the task and
the complexity of your design. However, simple explanations are
preferred. Here are some formatting tips:

* For complex tasks, like determining merge conflicts, we recommend
  that you split the task into parts. Describe your algorithm for each
  part in a separate section. Start with the simplest component and
  build up your design, one piece at a time. For example, your
  algorithms section for Merge Conflicts could have sections for:

   * Checking if a merge is necessary.
   * Determining which files (if any) have a conflict.
   * Representing the conflict in the file.
  
* Try to clearly mark titles or names of classes with white space or
  some other symbols.

### Main.java

1. main(String[] args): This is the entry point of the program. It first checks to make sure that the input array is not empty. Then, it calls `setupPersistence` to create the `/.gitlet` and `/.gitlet/repo` for persistance. Lastly, depending on the input argument, different functions are called to perform the operation.
2. setupPersistence(): If the directory for persisting repo instances does not exist yet, then make the directory.
3. validateNumArgs(String[] args, int n):  It checks the number of arguments versus the expected number and throws a RuntimeException if they do not match.

### Repo.java

1. repo(): Initializes a repo object and class variables
2. init(): Start a new version control system in CWD. 
3. add(String args): Adds a file to the staging Arraylist. 
4. commit(String message): Saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time, creating a new commit.
5. rm(Stirng fileName): Unstage the file if it is currently staged for addition. If the file is tracked in the current commit, stage it for removal and remove the file from the working directory if the user has not already done so.
6. log(): Displays all the information about the previous commits. Including their key. Ignores second parents.
7. global-log(): Same as log but does not ignore second parents. Shows every commit ever made. 
8. find(String Message): finds and prints out all the keys that match the given commit message.  
9. status(): Displays all the current branches and marks the current branch with an '*'. displays what files have been staged for addition or removal.
10. checkout(String... args): Can overwrite the head of the current branch with the passed in file key, take the version of the file as it exists in the commit with the given id, and puts it in the working directory, or Takes all files in the commit at the head of the given branch, and puts them in the working directory, overwriting the versions of the files that are already there if they exist.
11. branch(String branchName):  Creates a new branch with the given name, and points it at the current head node.
12. rm-branch(String branchName):
13. reset(String commitID): Checks out all the files tracked by the given commit. Removes tracked files that are not present in that commit.
14. merge(String branchName): Merges files from the given branch into the current branch.

### Commit.java

1. commit(String _message, String _selfKey, String _parentKey, String _Contents): Makes a new commit object with the given parameters.
2. printLog(): Formats and prints to the terminal the commit.
3. getMessage(): returns _message
4. getselfKey(): returns _selfKey
5. getparentKey(): returns _parentKey

## 3. Persistence

Describe your strategy for ensuring that you don’t lose the state of your program
across multiple runs. Here are some tips for writing this section:

* This section should be structured as a list of all the times you
  will need to record the state of the program or files. For each
  case, you must prove that your design ensures correct behavior. For
  example, explain how you intend to make sure that after we call
       `java gitlet.Main add wug.txt`,
  on the next execution of
       `java gitlet.Main commit -m “modify wug.txt”`, 
  the correct commit will be made.
  
* A good strategy for reasoning about persistence is to identify which
  pieces of data are needed across multiple calls to Gitlet. Then,
  prove that the data remains consistent for all future calls.
  
* This section should also include a description of your .gitlet
  directory and any files or subdirectories you intend on including
  there.

### add [file]
- When a new file is added to the staging area to be possibly commited, we put the file into the staging area directory so it is ready to be committed.  

### commit [file]
- when a file is committed, we pull the existing files from the staging area and add them to our tree of commit objects that can be referenced later on.  
## 4. Design Diagram

Attach a picture of your design diagram illustrating the structure of your
classes and data structures. The design diagram should make it easy to 
visualize the structure and workflow of your program.

