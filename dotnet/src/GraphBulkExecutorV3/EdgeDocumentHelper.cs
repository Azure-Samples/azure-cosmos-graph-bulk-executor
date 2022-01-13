// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Text;
    using Newtonsoft.Json;
    using GraphBulkExecutorV3.Graph.Element;
    using Newtonsoft.Json.Linq;

    internal sealed class EdgeDocumentHelper
    {
        private readonly bool isMultiValuedAndMetaPropertiesDisabled;
        private readonly string partitionKey;
        private readonly bool isPartitionedCollection;
        private readonly HashSet<string> edgeSystemProperties;

        public EdgeDocumentHelper(bool isMultiValuedAndMetaPropertiesDisabled, bool isPartitionedCollection, string partitionKey = null)
        {
            this.isMultiValuedAndMetaPropertiesDisabled = isMultiValuedAndMetaPropertiesDisabled;
            this.isPartitionedCollection = isPartitionedCollection;

            if (this.isPartitionedCollection)
            {
                if (string.IsNullOrEmpty(partitionKey))
                {
                    throw new ArgumentNullException(nameof(partitionKey));
                }
                this.partitionKey = partitionKey;
            }

            this.edgeSystemProperties = new HashSet<string>
            {
                partitionKey,
                GremlinKeywords.KW_EDGE_ID,
                GremlinKeywords.KW_EDGE_LABEL,
                GremlinKeywords.KW_EDGE_SINKV,
                GremlinKeywords.KW_EDGE_SINKV_LABEL,
                GremlinKeywords.KW_EDGE_SINKV_PARTITION,
                GremlinKeywords.KW_EDGE_SRCV,
                GremlinKeywords.KW_EDGE_SRCV_LABEL,
                GremlinKeywords.KW_EDGE_SRCV_PARTITION
            };
        }

        public JObject GetEdgeDocument(GremlinEdge edge)
        {
            edge.Validate();

            JObject jObject = new JObject();
            jObject.Add(GremlinKeywords.KW_EDGE_ID, edge.Id);
            jObject.Add(GremlinKeywords.KW_EDGE_LABEL, edge.Label);
            jObject.Add(GremlinKeywords.KW_EDGE_SINKV, JToken.FromObject(edge.InVertexId));
            jObject.Add(GremlinKeywords.KW_EDGEDOC_VERTEXID, JToken.FromObject(edge.OutVertexId));
            jObject.Add(GremlinKeywords.KW_EDGE_SINKV_LABEL, edge.InVertexLabel);
            jObject.Add(GremlinKeywords.KW_EDGEDOC_VERTEXLABEL, edge.OutVertexLabel);
            jObject.Add(GremlinKeywords.KW_EDGEDOC_IDENTIFIER, true);

            if (this.isPartitionedCollection)
            {
                if (edge.InVertexPartitionKey == null)
                {
                    throw new Exception("Graph Element: Edge: Validate: Edge must have a valid InVertexPartitionKey for a partitioned graph.");
                }

                if (edge.OutVertexPartitionKey == null)
                {
                    throw new Exception("Graph Element: Edge: Validate: Edge must have a valid OutVertexPartitionKey for a partitioned graph.");
                }

                // edge partition key = source partition key
                jObject.Add(this.partitionKey, JToken.FromObject(edge.OutVertexPartitionKey));

                // sink partition key
                jObject.Add(GremlinKeywords.KW_EDGE_SINKV_PARTITION, JToken.FromObject(edge.InVertexPartitionKey));
            }

            foreach (GremlinProperty ep in edge.GetProperties())
            {
                if (this.edgeSystemProperties.Contains(ep.Key))
                {
                    throw new Exception($"Property: {ep.Key} is not allowed as an edge property.");
                }

                jObject.Add(ep.Key, JToken.FromObject(ep.Value));
            }

            return jObject;
        }
    }
}