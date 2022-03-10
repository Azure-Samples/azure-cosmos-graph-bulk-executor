# GraphBulkExecutor Java Sample

This sample uses the Java SDK 4.0 for Cosmos Db to execute bulk operations against a Cosmos Database that has been configured to use the Gremlin API. It is a port of the .Net  [GraphBulkExecutorV3](https://github.com/ealsur/GraphBulkExecutorV3).

This sample is useful when hitting performance issues when uploading Vertices and Edges in bulk.

There are two primary components of this sample:

[GraphBulkExecutor](./src/main/java/com/microsoft/graph/bulk/impl/): Contains the source code for the reusable code that can be harnessed within your specific domain.

[Sample](./src/main/java/com/microsoft/graph/bulk/sample/): Contains an example implementation using the above logic. It has some flexibility in how it is run to allow for experimentation to determine how best to use the GraphBulkExecutor.