# Azure Cosmos sample for Java for Bulk ingestion and capabilities on Gremlin API accounts

This sample uses the Java SDK v4.x.x for Azure Cosmos DB to execute high-throughput bulk operations against a Gremlin API graph.
that has been configured to use the Gremlin API.

There are two primary components of this sample:

[GraphBulkExecutor](src/main/java/com/azure/graph/bulk/impl/): Contains the source code of the core bulk execution
functionality for Gremlin API.

[Sample](src/main/java/com/azure/graph/bulk/sample/): Contains an example implementation using the above logic. Note
that, the core GraphBulkExecutor libraries takes a set of input parameter which may need to be tuned for optimal
performance for a given scenario.
