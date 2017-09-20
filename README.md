# Java Fuseki client example
A basic example of how you can query an Apache Fuseki instance using Java. It reads queries from files, so they're easy to maintain, and shows how you can chain two queries by dyanmically creating the second query based on the result from the first.

It doesn't use any dependencies to keep things simple but a more robust solution would benefit for using existing libraries.

## How to use it
There's only one class `App.java` and you just have to run that as a Java program.

### Command line
You can do that from the command line with:

	cd java-fuseki-client-example
	mvn clean compile
	java -cp target/classes au.org.ecoinformatics.App

You should be output like:

	The subject 'http://www.aekos.org.au/ontology/1.0.0/aekos_common#SPECIESCONCEPT-T1504751208224' is of type 'http://www.aekos.org.au/ontology/1.0.0#SPECIESCONCEPT'

### Eclipse		
You can also run it in Eclipse. If you've just cloned this project, you'll need to generate the project files for Eclipse before you can import it into Eclipse:

	cd java-fuseki-client-example
	mvn eclipse:clean eclipse:eclipse
	
...then you can import the project to Eclipse. Finally, right click the App.java class and `Run as -> Java application`.
