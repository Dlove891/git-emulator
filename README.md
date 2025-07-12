# Gitlet

Gitlet is a simplified version-control system inspired by Git. It enables tracking file versions, branching, merging, and logging—all from the command line, without relying on any existing version-control libraries.

## Features

- Initialize a new repository
- Add and remove files from the staging area
- Commit changes with messages
- View commit history (`log`, `global-log`)
- Create and switch between branches
- Merge changes from other branches
- Reset to previous commits
- Status display of current branch, staged/unstaged files


## File Overview

- `Main.java`  
  Entry point of the application. Parses command-line arguments and delegates functionality to the `Repository` class.

- `Repository.java`  
  Core logic of Gitlet. Implements all repository-level operations like init, add, commit, merge, etc.

- `Commit.java`  
  Represents a single commit object, containing metadata (timestamp, message, parent hashes) and a snapshot of tracked files.

- `Blob.java`  
  Represents individual file contents as blobs. Blobs are stored by their SHA-1 hash to ensure deduplication.

- `Stage.java`  
  Handles the staging area for additions and removals prior to committing.

- `Branch.java`  
  Manages branches and their heads (pointers to commits).

- `Utils.java`  
  Helper functions for file operations and SHA-1 hashing, reused across multiple classes.

