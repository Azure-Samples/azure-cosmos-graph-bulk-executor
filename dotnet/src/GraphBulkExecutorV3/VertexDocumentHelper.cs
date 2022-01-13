// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    using System;
    using System.IO;
    using System.Linq;
    using System.Text;
    using Newtonsoft.Json;
    using GraphBulkExecutorV3.Graph.Element;
    using System.Globalization;
    using Newtonsoft.Json.Linq;

    internal sealed class VertexDocumentHelper
    {
        private readonly bool isMultiValuedAndMetaPropertiesDisabled;
        private readonly string partitionKey;
        private readonly bool isPartitionedCollection;

        public VertexDocumentHelper(bool isMultiValuedAndMetaPropertiesDisabled, bool isPartitionedCollection, string partitionKey = null)
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
        }

        public JObject GetVertexDocument(GremlinVertex vertex)
        {
            vertex.Validate();

            bool isPartitionKeyProvided = false;
            JObject jObject = new JObject();
            jObject.Add(GremlinKeywords.KW_DOC_ID, vertex.Id);
            jObject.Add(GremlinKeywords.KW_DOC_LABEL, vertex.Label);

            GremlinVertexProperty vp;
            foreach (string key in vertex.GetPropertyKeys())
            {
                if (key.Equals(this.partitionKey))
                {
                    vp = vertex.GetVertexProperties(key).FirstOrDefault();

                    if (vp == null)
                    {
                        throw new ArgumentException("Partition key property can't be null");
                    }

                    jObject.Add(vp.Key, JToken.FromObject(vp.Value));

                    isPartitionKeyProvided = true;
                }
                else
                {
                    if (this.isMultiValuedAndMetaPropertiesDisabled || key.ToLower(CultureInfo.InvariantCulture).Equals(GremlinKeywords.KW_DOC_TTL))
                    {
                        vp = vertex.GetVertexProperties(key).FirstOrDefault();
                        if (vp == null)
                        {
                            throw new ArgumentException("Vertex property can't be null");
                        }

                        jObject.Add(vp.Key, JToken.FromObject(vp.Value));
                    }
                    else
                    {
                        JArray jArray = new JArray();
                        
                        foreach (GremlinVertexProperty gremlinVertexProperty in vertex.GetVertexProperties(key))
                        {
                            JObject nestedGremlinVertex = new JObject();
                            nestedGremlinVertex.Add(GremlinKeywords.KW_PROPERTY_VALUE, JToken.FromObject(gremlinVertexProperty.Value));
                            nestedGremlinVertex.Add(GremlinKeywords.KW_PROPERTY_ID, Guid.NewGuid().ToString());

                            if (gremlinVertexProperty.Properties != null)
                            {
                                JObject nestedGremlinVertexMeta = new JObject();

                                foreach (GremlinProperty gp in gremlinVertexProperty.Properties)
                                {
                                    nestedGremlinVertexMeta.Add(gp.Key, JToken.FromObject(gp.Value));
                                }

                                nestedGremlinVertex.Add(GremlinKeywords.KW_PROPERTY_META, nestedGremlinVertexMeta);
                            }

                            jArray.Add(nestedGremlinVertex);
                        }

                        jObject.Add(key, jArray);
                    }
                }
            }

            if (this.isPartitionedCollection && !isPartitionKeyProvided)
            {
                throw new ArgumentException("PartitionKey property must be specified while adding a vertex to a partitioned graph.");
            }

            return jObject;
        }
    }
}