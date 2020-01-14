# Sokt
[![Build Status](https://travis-ci.org/josh-richardson/sokt.svg?branch=master)](https://travis-ci.org/josh-richardson/sokt)


Sokt is a Kotlin wrapper for the Solidity compiler (solc). Given a solidity file, it can identify the ideal compiler version to use from the pragma statement at the top of the file. It can then download, install and invoke the compiler. Rather than using Dockerized versions of Solc, Sokt uses native builds and is compatible with Mac, Windows and Linux. This means that the only dependency is a Java installation. 
 
Sokt is currently under active development and should be available as a published artefact soon.

Example usage:
```kotlin
val solidityFile = SolidityFile("/a/solidity/file/name.sol")

println("Resolving compiler version for $fileName")
val compilerInstance = solidityFile.getCompilerInstance()

println("Resolved ${compilerInstance.solcRelease.version} for $fileName")

val result = compilerInstance.execute(
    SolcArguments.OUTPUT_DIR.param { "/output/directory" },
    SolcArguments.AST,
    SolcArguments.BIN,
    SolcArguments.OVERWRITE
)

println("Solc exited with code: $result")
```

