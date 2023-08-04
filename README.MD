
# Native Transformer for qProtect

Video tutorial: https://www.youtube.com/watch?v=_QxPAAzNcdA

## Requirements
- [qProtect (Paid)](https://mdma.dev) v1.10.0 or newer
- [CMake](https://cmake.org/)
- Java Development Kit (Version 8)
- Windows only:
    - [Visual Studio with C++ Package](https://visualstudio.microsoft.com/)
    - or [MinGW](https://www.mingw-w64.org/)
- Linux only:
    - [build-essential](https://packages.debian.org/de/source/sid/build-essential)
- JAVA_HOME variable needs to be set otherwise won't work

## Instructions
- Download the Transformer and put the .jar into AppData/qProtect/custom/.
- Launch qProtect.
- Select, input and output file.
- Go to the "Transformers" tab and you will see a new tab called "Custom".
- Enable your desired transformers, click "Process" and wait for it to finish.
- qProtect created a new folder called "cpp/" in the same directory as the output jar.

### For Windows: &nbsp;
 (Visual Studio):
- Open CMake-GUI select the "cpp/" directory and click "Configure" and then "Generate".
- Open the project with Visual Studio and compile it.
- Put the .dll into your output jar using 7-zip (or anything similar). \

 (MinGW):  &nbsp;
 - Open the terminal and go to the directory where the output is.
 - Type "cmake -G "MinGW Makefiles cpp/"" and then "make" to start compiling the project.
 - Put the .dll into your output jar using "zip output.jar compiled.so" .

### For Linux:
- Open the terminal and go to the directory where the output is.
- Type "cmake cpp/" and then "make" to start compiling the project.
- Put the .so into your output jar using "zip output.jar compiled.so" .

## Credits
- radioegor146 ([native-obfuscator](https://github.com/radioegor146/native-obfuscator))
- vaz, Mystery, brownie ([qProtect](https://mdma.dev/))
