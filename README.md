# Obama Compiler
Object oriented compiler for the Java Virtual Machine with Objective-C-like Syntax. Built by Bas Stottelaar and Mart Meijerink for the Compiler Design (Vertalerbouw) course.

## Quick Start
1. Import this project in Eclipse 3.7+
2. Make sure the libraries in the `lib/` folder are correctly referenced
2. Make sure ANTLR generates the correct files WITH debug options (the `-debug` option should be checked in the ANTLR preferences)
3. Compile it, put the Obama.jar in the `bin/` folder
4. Run the examples.

## Usage

### The application
The basic command is `java -jar 'app/Obama.jar' <options>`. Replace `<options>` with a filename and watch the output appear in the same foler. 

You can try to compile some basic examples in the `data/` folder.

### Directory structure
* `app/` &mdash; Contains the scripts and the main application binary (when compiled).
* `data/` &mdash; Contains the test files required for the jUnit tests
* `lib/` &mdash; The external libraries used in this project
* `src/` &mdash; Main application source
* `test/` &mdash; Test classes

## TODO
A lot, since it is far from finished ;) But it works and passes all the tests we have set up.

## License
See the LICENSE file for more information. All trademarks of external libraries included are copyright of their respective owners.