#### Sokt
[![Build Status](https://travis-ci.org/josh-richardson/sokt.svg?branch=master)](https://travis-ci.org/josh-richardson/sokt)


Sokt is a Kotlin library and version manager for the Solidity compiler (solc). It is under active development.

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