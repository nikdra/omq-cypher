# Ontology-Mediated Querying for Property Graphs

## About the Project

This project implements a rewriting algorithm for a subset of C2RPQs and DL-Lite ontologies.
It takes as input a path to an OWL file and a query defined by our [ANTLR grammar](/src/main/antlr4/at/ac/tuwien/informatics/generated/Q.g4).
The OWL file can be in any format accepted by [OWL API version 5](http://owlcs.github.io/owlapi/).
You can find some example OWL files in our [test resources](/src/test/resources).
For an ontology and a query, the tool creates an equivalent Neo4j Cypher query which can be evaluated over the plain data.
The query is copied to your clipboard, so you can copy-paste it into a Cypher shell or the Neo4j Desktop application.

## Built With

* Java 11
* Apache Maven
* ANTLR 4
* OWL API

## Getting Started

The rewriting prototype can be executed from the command line.
It is interactive, so you will be asked to provide your arguments after executing the .jar file.

### Prerequisites

Building and running the application requires Java 11.

### Installation

Clone the repository and run
```cmd
mvn clean compile
```

Then, you can run the main class [Cli](src/main/java/at/ac/tuwien/informatics/client/Cli.java) from your IDE.
Alternatively, you can build a fat .jar file
```cmd
mvn package
```
The .jar files will be generated in the `target` directory of your local repository.
Look for the .jar which contains `with-dependencies` in its name.

### Usage

To perform the rewriting, execute either [Cli](src/main/java/at/ac/tuwien/informatics/client/Cli.java) (with all dependencies) installed, or
```cmd
java -jar <pathToJar><jarname>.jar
```
The prototype writes instructions to the console output.
For example, to run the rewriting with an ontology from the [test resources](/src/test/resources):
```cmd
Please enter a path to an ontology file: 
src/test/resources/paths2.owl
```
Then, we can provide a query:
```cmd
q(x):-A(x),r*(x,y),B(y)
```
And finally, the tool tells us that rewriting has been completed:
```cmd
Your query has been rewritten. It has been copied to your system clipboard.
```

## Ontology

For a given ontology, we test that it is in OWL2 QL.
The following ontology axioms from OWL2 QL are accepted (essentially DL-Lite):

* subclasses
* domain
* ranges
* sub properties
* inverse properties
* some values from (top concept)

## Query Language

Queries consist of a **head** and a **body**.
The head contains the answer variables of the query, whereas the body contains the comma-separated atoms.
Atoms can be of the following form:
* A(t) for a basic concept name A
* (s_1|...|s_n)(t,t') for role names (properties) and their inverses s_i
* (r_1|...|r_n)*(t,t') for role names (properties) r_i
* Concatenations of binary atoms - paths. Note that you can only use role names in paths.

## Query Answers

Ontologies use homomorphism-based semantics.
Cypher has isomorphism-based no-repeated-edges semantics.
Note that the answers rewriting is only correct if the roles in the paths are acyclic i.e., there is no cycle in your data that can be formed with the roles from the paths.
If you do not plan to use paths, then you need not worry about this.
Without paths and arbitrary length atoms, the rewriting algorithm returns the correct answers under homomorphism-based and Cypher semantics.

## Contributing
Any contributions are welcome.
If you have a suggestion, or notice a bug, please open an issue.
You can also fork the project, create a new branch and open a pull request.