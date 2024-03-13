# RefactoringMinerTest

```
// Output single executable file with all dependencies
$ mvn clean compile assembly:single

// Run the executable
$ java -jar target/RefactoringMinerTest-1.0-SNAPSHOT-jar-with-dependencies.jar <repoUrl> <repoName> <startCommitSHA> <endCommitSHA>

// Example
$ java -jar .\RefactoringMinerTest-1.0-SNAPSHOT-jar-with-dependencies.jar https://github.com/apache/mina-sshd.git mina-sshd 1a3da27b8c5667c9b335a8352
ba6df5ca75167ba ea45ddc079a1dab67d590e800132088f
```