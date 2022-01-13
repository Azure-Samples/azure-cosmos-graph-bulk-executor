# Azure Cosmos sample for .NET for Bulk ingestion and capabilities on Graph API accounts

This project is a sample of how the [Cosmos DB V3 SQL SDK](https://github.com/Azure/azure-cosmos-dotnet-v3) can be used to process Gremlin/Graph data leveraging the [Bulk mode](https://docs.microsoft.com/azure/cosmos-db/sql/tutorial-sql-api-dotnet-bulk-import).

It can be used for migration scenarios when moving from  [Microsoft.Azure.CosmosDB.BulkExecutor.Graph.GraphBulkExecutor](https://docs.microsoft.com/dotnet/api/microsoft.azure.cosmosdb.bulkexecutor.graph.graphbulkexecutor?view=azure-dotnet).

## Sample usage

Full sample code available: https://github.com/Azure-Samples/azure-cosmos-graph-bulk-executor/blob/main/dotnet/src/Sample

```csharp
GraphBulkExecutor graphBulkExecutor = new GraphBulkExecutor("MyConnectionString", "myDatabase", "myContainer");

List<IGremlinElement> gremlinElements = new List<IGremlinElement>();
gremlinElements.AddRange(Program.GenerateVertices(Program.documentsToInsert));
gremlinElements.AddRange(Program.GenerateEdges(Program.documentsToInsert));
BulkOperationResponse bulkOperationResponse = await graphBulkExecutor.BulkImportAsync(
    gremlinElements: gremlinElements,
    enableUpsert: true);
```
