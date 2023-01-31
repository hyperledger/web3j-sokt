# Sokt
[![Build Status](https://api.travis-ci.org/web3j/web3j-sokt.svg?branch=master)](https://travis-ci.org/web3j/web3j-sokt)


Sokt is a Kotlin wrapper for the Solidity compiler (solc). Given a solidity file, it can identify the ideal compiler version to use from the pragma statement at the top of the file. It can then download, install and invoke the compiler. Rather than using Dockerized versions of Solc, Sokt uses native builds and is compatible with Mac, Windows and Linux (x86/64 only). This means that the only dependency is a Java installation. Sokt also plays nicely with GraalVM, eliminating the Java dependency if necessary. 
 
Sokt is currently under active development. You can use it in one of your own projects by adding the following dependency to your `build.gradle`:

```groovy
dependencies {
    compile group: 'org.web3j', name: 'web3j-sokt', version: '0.2.1'
}
```
For other build systems, see [here](https://mvnrepository.com/artifact/org.web3j/web3j-sokt/0.2.1).

Example usage:
```kotlin
val fileName = filePath.substringAfterLast("/")
println("sokt Processing $fileName")
val solidityFile = SolidityFile(filePath)

println("Resolving compiler version for $fileName")
val compilerInstance = solidityFile.getCompilerInstance()

println("Resolved ${compilerInstance.solcRelease.version} for $fileName")

val result = compilerInstance.execute(
    SolcArguments.OUTPUT_DIR.param { "/tmp" },
    SolcArguments.BIN,
    SolcArguments.OVERWRITE
)

println("Solc exited with code: ${result.exitCode}")
println("Solc standard output:\n${result.stdOut}")
println("Solc standard error:\n${result.stdErr}")

```
Note: For running Sokt on Linux arm64 machine, make sure these dependencies are installed:

1. [CMake](https://cmake.org/download/)
2. [Boost](https://www.boost.org/)
3. [Git](https://git-scm.com/download)

On Linux arm64 machines, Sokt downloads the source file and builds the binary since arm64 solc builds are not supported officially.
Source: [Solc docs](https://docs.soliditylang.org/en/latest/installing-solidity.html#building-from-source)