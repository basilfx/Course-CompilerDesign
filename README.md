# Obama Compiler
Object oriented compiler for the Java Virtual Machine with Objective-C-like
Syntax. Built by Bas Stottelaar and Mart Meijerink for the Compiler Design
(Vertalerbouw) course.

[![Build Status](https://github.com/basilfx/Course-CompilerDesign/workflows/Course-CompilerDesign/badge.svg)](https://github.com/basilfx/Course-CompilerDesign/actions)

## Quick Start
1. Make sure Java 11 and Maven are installed.
2. Run `mvn package`. The `*.jar` file is placed in `target/`.
3. Run the examples.

## Usage

### The application
The basic command is `java -jar 'target/<JAR File>.jar' <options>`.

To compile a file, replace `<options>` with a `-f <filename>`. The output
appears in the same foler. You can try to compile some basic examples in the
`examples/` folder.

### Directory structure
* `src/` — Application and test sources
* `examples/` — Code examples

## TODO
A lot, since it is far from finished. But it works and passes all the tests
designed so far.

## License
See the [LICENSE](LICENSE) file for more information. All trademarks of
external libraries included are copyright of their respective owners.
