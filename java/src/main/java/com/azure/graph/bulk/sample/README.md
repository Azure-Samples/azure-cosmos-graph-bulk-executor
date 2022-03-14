# Sample usage

The sample application is provided to illustrate how to use the GraphBulkExecutor package. Samples are available for using either the Domain object annotations or using the POJO objects directly. It is encouraged to try both approaches to determine which better meets your implementation and performance demands.

## Running the sample

### Prerequisites

To run this sample you will need to have the following:

* OpenJDK 11
* Maven
* An Azure Cosmos Db Account configured to use the Gremlin API

### Configuration

The /resources/application.properties file defines the data required to configure the Cosmos Db the required values are:

* **sample.sql.host**: This is the value provided by the Azure Cosmos Db. Ensure you use the ".NET SDK URI" which can be located on the Overview blade of the Cosmos Db Account.
* **sample.sql.key**: You can get this from the Keys blade of the Cosmos Db Account.
* **sample.sql.database.name**: The name of the database within the Cosmos Db account to run the sample against. If the database isn't found, the sample code will create it.
* **sample.sql.container.name**: The name of the container within the database to run the sample against. If the container isn't found, the sample code will create it.
* **sample.sql.partition.path**: If the container needs to be created, this value will be used to define the partitionKey path.
* **sample.sql.allow.upserts**: Tells the sample whether it will use the UpsertItemOperations or, if false, the CreateItemOperations. Explore the differences in performance between the two and determine which options best suits your use case.
* **sample.sql.allow.throughput**: The container will be updated to use the throughput value defined here. If you are exploring different throughput options to meet your performance demands, make sure to reset the throughput on the container when done with your exploration. There are costs associated with leaving the container provisioned with a higher throughput.

### Execution

Once the configuration is complete make sure you run:

```bash
mvn clean package 
```

For added safety, you can also run the integration tests by changing the "skipIntegrationTests" value in the pom.xml to false.

Assuming the Unit tests were run successfully. You can run the following from the command line to execute the sample code:

```bash
java -jar target/GraphBulkExecutor-1.0-jar-with-dependencies.jar -v 1000 -e 10 -d
```

This will run the sample with a fairly small batch (1k Vertices and roughly 5k Edges). Use the following command lines arguments to tweak the volumes run and which sample version to run.

### Command line Arguments

When running the sample there are several command line arguments that can be provided they are as follows:

* **--vertexCount** (-v): Tells the application how many person vertices to generate.
* **--edgeMax** (-e): Tells the application what the maximum number of edges to generate for each Vertex. The generator will randomly select a number between 1 and the value provided here.
* **--domainSample** (-d): Tells the application to run the sample using the Person and Relationship domain structures instead of the GraphBulkExecutors GremlinVertex and GremlinEdge POJOs.

## The Sample Domain

### Person Vertex

The Person class is a fairly simple domain object that has been decorated with several annotations to assist in the transformation into the GremlinVertex class. They are as follows:

* **GremlinVertex**: Notice how we're using the optional "label" parameter to define all Vertices created using this class.
* **GremlinId**: Being used to define which field will be used as the id value. While the field name on the Person class is id, it is not required.
* **GremlinProperty**: Is being used on the email field to change the name of the property when stored in the database.
* **GremlinPartitionKey**: Is being used to define which field on the class contains the partition key. The field name provided here should match the value defined by the partition path on the container.
* **GremlinIgnore**: Is being used to exclude the isSpecial field from the property being written to the database.

### Relationship Edge

The RelationshipEdge is a fairly versatile domain object. Using the field level label annotation allows for a dynamic collection of edge types to be created. The following annotations are represented in this sample domain edge:

* **GremlinEdge**: The GremlinEdge decoration on the class, defines the name of the field for that for the partition key. The value assigned, when the edge document is created, will come from the source vertex information.
* **GremlinEdgeVertex**: Notice there are two of these defined. One for each side of the Edge (Source and Destination). Our sample has the field's data type as GremlinEdgeVertexInfo. The information provided by this class is required for the edge to be created correctly in the database. Another option would be to have the data type of the vertices be a class that has been decorated with the GremlinVertex annotations.
* **GremlinLabel**: This Edge sample is using a field to define what the label value is. This allows different labels to be defined while still using the same base domain class.

## Output Explained

The console will finish its run with a json string describing the run times of the sample. The json string contain the following information.

* **startTime**: The System.nanoTime() when the process started.
* **endtime**: The System.nanoTime() when the process completed.
* **durationInNanoSeconds**: The difference between the endTime and the startTime.
* **durationInMinutes**: The durationInNanoSeconds converted into minutes. Important to note that this is represented as a float number, not a time value. e.g. a value 2.5 would be 2 minutes and 30 seconds.
* **vertexCount**: The volume of vertices generated. This should match the value passed into the command line execution.
* **edgeCount**: The volume of edges generated. This value is not static. It is built with an element of randomness in it.
* **exception**: Only populated when there was an exception thrown when attempting to make the run.

### States Array

The states array gives insight into how long each step within the execution takes. The steps that occur are:

* **Build sample vertices**: The time it takes to fabricate the requested volume of Person objects.
* **Build sample edges**: The time it takes to fabricate the Relationship objects.
* **Configure Database**: The amount of time it took to get the database configured based on the values provided in the application.properties.
* **Write Documents**: The total time it took to write the documents to the database.

Each state will contain the following values:

* **stateName**: The name of the state being reported.
* **startTime**: The System.nanoTime() when the state started.
* **endtime**: The System.nanoTime() when the state completed.
* **durationInNanoSeconds**: The difference between the endTime and the startTime.
* **durationInMinutes**: The durationInNanoSeconds converted into minutes. Important to note that this is represented as a float number, not a time value. e.g. a value 2.5 would be 2 minutes and 30 seconds.
