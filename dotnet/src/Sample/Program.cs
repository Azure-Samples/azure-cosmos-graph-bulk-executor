// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3.Sample
{
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos;

    class Program
    {
        private static string connectionString = "<connection-string>";
        private static string databaseName = "TestDb";
        private static string containerName = "TestColl";
        private static long documentsToInsert = 2000;
        private static string partitionKey = "pk";

        static async Task Main(string[] args)
        {
            CosmosClient cosmosClient = new CosmosClient(connectionString);

            Database database = await cosmosClient.CreateDatabaseIfNotExistsAsync(Program.databaseName);

            await database.CreateContainerIfNotExistsAsync(Program.containerName, $"/{Program.partitionKey}", 10000);

            GraphBulkExecutor graphBulkExecutor = new GraphBulkExecutor(connectionString, Program.databaseName, Program.containerName);

            List<IGremlinElement> gremlinElements = new List<IGremlinElement>();
            gremlinElements.AddRange(Program.GenerateVertices(Program.documentsToInsert));
            gremlinElements.AddRange(Program.GenerateEdges(Program.documentsToInsert));
            BulkOperationResponse bulkOperationResponse = await graphBulkExecutor.BulkImportAsync(
                gremlinElements: gremlinElements,
                enableUpsert: true);

            Console.WriteLine($"TotalTimeTaken: {bulkOperationResponse.TotalTimeTaken}");
            Console.WriteLine($"TotalRequestUnitsConsumed: {bulkOperationResponse.TotalRequestUnitsConsumed}");
            Console.WriteLine($"SuccessfulDocuments: {bulkOperationResponse.SuccessfulDocuments}");
        }

        private static IEnumerable<GremlinEdge> GenerateEdges(long count)
        {
            for (long i = 0; i < count - 1; i++)
            {
                GremlinEdge e = new GremlinEdge(
                    "e" + i,
                    "knows",
                    i.ToString(),
                    (i + 1).ToString(),
                    "vertex",
                    "vertex",
                    i,
                    i + 1);

                e.AddProperty("duration", i);

                yield return e;
            }
        }

        private static IEnumerable<GremlinVertex> GenerateVertices(long count)
        {
            GremlinVertex vBad = new GremlinVertex(Guid.NewGuid().ToString(), "vertex");
            vBad.AddProperty(Program.partitionKey, 0);
            yield return vBad;

            for (long i = 0; i < count; i++)
            {
                GremlinVertex v = new GremlinVertex(i.ToString(), "vertex");
                v.AddProperty(Program.partitionKey, i);
                v.AddProperty("name1", "name" + i);
                v.AddProperty("name2", i * 2);
                v.AddProperty("name3", i * 3);
                v.AddProperty("name4", i + 100);

                yield return v;
            }
        }
    }
}
