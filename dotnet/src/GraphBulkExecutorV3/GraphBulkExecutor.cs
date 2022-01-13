// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Reflection;
    using System.Threading;
    using System.Threading.Tasks;
    using GraphBulkExecutorV3.Graph.Element;
    using Microsoft.Azure.Cosmos;
    using Microsoft.Azure.Cosmos.Fluent;
    using Newtonsoft.Json.Linq;

    /// <summary>
    /// Implementation class of IgraphBulkImport interface
    /// </summary>
    public class GraphBulkExecutor : IDisposable
    {
        private readonly SemaphoreSlim initializationSyncLock = new SemaphoreSlim(1, 1);
        private readonly bool isMultiValuedAndMetaPropertiesDisabled;
        private readonly Container container;

        private VertexDocumentHelper vertexDocumentHelper;
        private EdgeDocumentHelper edgeDocumentHelper;
        private CosmosClient client;
        private string VertexPartitionProperty;
        private bool isDisposed = false;
        private bool isSuccessfullyInitialized = false;

        /// <summary>
        /// Initializes a new instance of the <see cref="GraphBulkExecutor"/> class.
        /// </summary>
        /// <param name="connectionString">Connection string for the Cosmos DB account.</param>
        /// <param name="databaseName">Name of the database to use.</param>
        /// <param name="containerName">Name of the container to use.</param>
        /// <param name="isMultiValuedAndMetaPropertiesDisabled">Boolean flag indicating whether the support for multi-valued and meta properties
        /// are disabled. By default the support is always enabled. The support currently can only be disabled via a request to Azure Cosmos DB team.
        /// When the above mentioned support is disabled, the underlying storage format changes. Mixing document import via two different mode can
        /// cause data corruption.</param>
        public GraphBulkExecutor(
            string connectionString,
            string databaseName,
            string containerName,
            bool isMultiValuedAndMetaPropertiesDisabled = false)
            : this (databaseName,
                  containerName,
                  isMultiValuedAndMetaPropertiesDisabled)
        {
            if (string.IsNullOrEmpty(connectionString))
            {
                throw new ArgumentNullException(nameof(connectionString));
            }

            this.client = new CosmosClient(connectionString,
                new CosmosClientOptions()
                {
                    AllowBulkExecution = true,
                    ApplicationName = GraphBulkExecutor.GetApplicationName()
                });

            this.container = this.client.GetContainer(databaseName, containerName);
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="GraphBulkExecutor"/> class.
        /// </summary>
        /// <param name="cosmosClientBuilder">Customized <see cref="CosmosClientBuilder"/> to create the <see cref="CosmosClient"/> instance with.</param>
        /// <param name="databaseName">Name of the database to use.</param>
        /// <param name="containerName">Name of the container to use.</param>
        /// <param name="isMultiValuedAndMetaPropertiesDisabled">Boolean flag indicating whether the support for multi-valued and meta properties
        /// are disabled. By default the support is always enabled. The support currently can only be disabled via a request to Azure Cosmos DB team.
        /// When the above mentioned support is disabled, the underlying storage format changes. Mixing document import via two different mode can
        /// cause data corruption.</param>
        public GraphBulkExecutor(
            CosmosClientBuilder cosmosClientBuilder,
            string databaseName,
            string containerName,
            bool isMultiValuedAndMetaPropertiesDisabled = false)
            : this (databaseName, 
                  containerName, 
                  isMultiValuedAndMetaPropertiesDisabled)
        {
            if (cosmosClientBuilder == null)
            {
                throw new ArgumentNullException(nameof(cosmosClientBuilder));
            }

            this.client = cosmosClientBuilder
                .WithBulkExecution(true)
                .WithApplicationName(GraphBulkExecutor.GetApplicationName())
                .Build();

            this.container = this.client.GetContainer(databaseName, containerName);
        }

        private GraphBulkExecutor(
            string databaseName,
            string containerName,
            bool isMultiValuedAndMetaPropertiesDisabled)
        {
            if (string.IsNullOrEmpty(databaseName))
            {
                throw new ArgumentNullException(nameof(databaseName));
            }

            if (string.IsNullOrEmpty(containerName))
            {
                throw new ArgumentNullException(nameof(containerName));
            }

            this.isMultiValuedAndMetaPropertiesDisabled = isMultiValuedAndMetaPropertiesDisabled;
        }

        /// <summary>
        /// Executes a bulk import in the Azure Cosmos DB database service.
        /// </summary>
        /// <param name="gremlinElements">The collection of gremlin elements to be bulk imported.</param>
        /// <param name="enableUpsert">(Optional) A flag to enable upsert of the elements.</param>
        /// <param name="cancellationToken">The cancellation token to gracefully exit bulk import.</param>
        /// <returns>The task object representing the service response for the bulk import operation.</returns>
        /// <exception cref="CosmosException">Failed to complete the operation.</exception>
        public async Task<BulkOperationResponse> BulkImportAsync(
            IEnumerable<IGremlinElement> gremlinElements,
            bool enableUpsert = false,
            CancellationToken cancellationToken = default)
        {
            this.ThrowIfDisposed();
            await this.InitializeAsync(cancellationToken);

            List<JObject> elements = gremlinElements.Select(element => this.GetGraphElementDocument(element)).ToList();
            BulkExecutor bulkExecutor = new BulkExecutor(elements.Count);
            foreach (JObject element in elements)
            {
                if (enableUpsert)
                {
                    bulkExecutor.Tasks.Add(this.container.UpsertItemAsync(element, cancellationToken: cancellationToken).CaptureOperationResponse(element));
                }
                else
                {
                    bulkExecutor.Tasks.Add(this.container.CreateItemAsync(element, cancellationToken: cancellationToken).CaptureOperationResponse(element));
                }
            }

            return await bulkExecutor.ExecuteAsync();
        }

        /// <inheritdoc/>
        public void Dispose()
        {
            if (this.isDisposed)
            {
                return;
            }

            CosmosClient clientSnapshot = this.client;
            if (clientSnapshot != null)
            {
                clientSnapshot.Dispose();
                this.client = null;
            }

            this.isDisposed = true;
        }

        private async Task InitializeAsync(CancellationToken cancellationToken)
        {
            if (this.isSuccessfullyInitialized)
            {
                return;
            }

            await this.initializationSyncLock.WaitAsync(cancellationToken);
            try
            {
                if (this.isSuccessfullyInitialized)
                {
                    return;
                }

                await this.VerifyCollectionPropertiesAsync(cancellationToken);

                bool isPartitionedCollection = (!string.IsNullOrEmpty(this.VertexPartitionProperty));

                // Parameters of the constructors below would come from GraphConnection
                this.vertexDocumentHelper = new VertexDocumentHelper(isPartitionedCollection: isPartitionedCollection, isMultiValuedAndMetaPropertiesDisabled: this.isMultiValuedAndMetaPropertiesDisabled, partitionKey: this.VertexPartitionProperty);
                this.edgeDocumentHelper = new EdgeDocumentHelper(isPartitionedCollection: isPartitionedCollection, isMultiValuedAndMetaPropertiesDisabled: this.isMultiValuedAndMetaPropertiesDisabled, partitionKey: this.VertexPartitionProperty);
            }
            finally
            {
                this.initializationSyncLock.Release();
            }
        }

        private async Task VerifyCollectionPropertiesAsync(CancellationToken cancellationToken)
        {
            ContainerProperties properties = await this.container.ReadContainerAsync(cancellationToken: cancellationToken);
            string partitionFullPath = properties.PartitionKeyPath;

            string partitionTopLevelPath =
                    partitionFullPath.Split(new[] { '/' }, StringSplitOptions.RemoveEmptyEntries)[0];

            if (partitionFullPath == $"/{GremlinKeywords.KW_DOC_ID}")
            {
                throw new Exception("A container with /id as partition key cannot be used for graph operations.");
            }
            else if (partitionFullPath == $"/{GremlinKeywords.KW_DOC_LABEL}")
            {
                throw new Exception("A container with /label as partition key cannot be used for graph operations.");
            }
            else if (partitionFullPath == $"/{GremlinKeywords.KW_DOC_PARTITION}")
            {
                // When the document collection is physically partitioned by "/_partition",
                // The value of this property mirrors the value of the property VertexPartitionProperty.
                // VertexPartitionProperty is "id" by default, unless it's set to another vertex property.
                this.VertexPartitionProperty = this.VertexPartitionProperty ?? GremlinKeywords.KW_DOC_ID;
            }
            else
            {
                this.VertexPartitionProperty = partitionTopLevelPath;
            }
        }

        private static string GetApplicationName()
        {
            return string.Format("GraphBulkImportSDK-{0}-{1}",
                typeof(GraphBulkExecutor).GetTypeInfo().Assembly.GetName().Version,
                typeof(GraphBulkExecutor).GetTypeInfo().Assembly.ImageRuntimeVersion);
        }

        private JObject GetGraphElementDocument(IGremlinElement element)
        {
            if (element is GremlinVertex vertex)
            {
                return this.vertexDocumentHelper.GetVertexDocument(vertex);
            }

            if (element is GremlinEdge edge)
            {
                return this.edgeDocumentHelper.GetEdgeDocument(edge);
            }

            throw new Exception($"Only {typeof(GremlinVertex)} and {typeof(GremlinEdge)} is supported by Graph Bulk Executor tool.");
        }

        private void ThrowIfDisposed()
        {
            if (this.isDisposed)
            {
                throw new ObjectDisposedException("GraphBulkExecutor");
            }
        }
    }
}