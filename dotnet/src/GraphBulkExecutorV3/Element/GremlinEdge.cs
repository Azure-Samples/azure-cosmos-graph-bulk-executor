// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using GraphBulkExecutorV3.Graph.Element;

    /// <summary>
    /// Storage container for edge data.
    /// Supports deserialization from GraphSON format.
    /// </summary>
    public class GremlinEdge : IGremlinElement
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="GremlinEdge"/> class. Folling Tinkerpop convention outVertex is the source vertex and inVertex is the sink/destination
        /// vertex (http://tinkerpop.apache.org/javadocs/3.1.3/core/org/apache/tinkerpop/gremlin/structure/Edge.html)
        /// Diagrammatically:
        ///     outVertex ---label---> inVertex.
        /// </summary>
        /// <param name="edgeId">Id of the edge</param>
        /// <param name="edgeLabel">Label of the edge</param>
        /// <param name="outVertexId">Id of the source vertex</param>
        /// <param name="inVertexId">Id of the sink vertex</param>
        /// <param name="outVertexLabel">Label of the source vertex</param>
        /// <param name="inVertexLabel">Label of the sink vertex</param>
        /// <param name="outVertexPartitionKey">PartitionKey of the source vetex (mandatory for partitioned collection)</param>
        /// <param name="inVertexPartitionKey">PartitionKey of the sink vertex (mandatory for partitioned collection)</param>
        public GremlinEdge(
            string edgeId,
            string edgeLabel,
            string outVertexId,
            string inVertexId,
            string outVertexLabel,
            string inVertexLabel,
            object outVertexPartitionKey = null,
            object inVertexPartitionKey = null)
        {
            this.Id = edgeId;
            this.Label = edgeLabel;
            this.InVertexId = inVertexId;
            this.InVertexLabel = inVertexLabel;
            this.InVertexPartitionKey = inVertexPartitionKey;
            this.OutVertexId = outVertexId;
            this.OutVertexLabel = outVertexLabel;
            this.OutVertexPartitionKey = outVertexPartitionKey;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="Edge"/> class.
        /// </summary>
        /// <param name="edge">The edge data.</param>
        internal GremlinEdge(GremlinEdge edge)
        {
            this.Id = edge.Id;
            this.Label = edge.Label;
            this.InVertexId = edge.InVertexId;
            this.OutVertexId = edge.OutVertexId;
            this.InVertexLabel = edge.InVertexLabel;
            this.OutVertexLabel = edge.OutVertexLabel;
            this.Properties = edge.Properties;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="Edge"/> class.
        /// </summary>
        /// <param name="id">The identifier.</param>
        /// <param name="label">The label.</param>
        internal GremlinEdge(string id, string label)
        {
            this.Id = id;
            this.Label = label;
        }

        /// <summary>
        /// Gets the identifier.
        /// </summary>
        /// <value>
        /// The identifier.
        /// </value>
        public string Id { get; internal set; }

        /// <summary>
        /// Gets the label.
        /// </summary>
        /// <value>
        /// The label.
        /// </value>
        public string Label { get; internal set; }

        /// <summary>
        /// Gets the in vertex identifier.
        /// </summary>
        /// <value>
        /// The in vertex identifier.
        /// </value>
        public object InVertexId { get; internal set; }

        /// <summary>
        /// Gets the out vertex identifier.
        /// </summary>
        /// <value>
        /// The out vertex identifier.
        /// </value>
        public object OutVertexId { get; internal set; }

        /// <summary>
        /// Gets the in vertex label.
        /// </summary>
        /// <value>
        /// The in vertex label.
        /// </value>
        public string InVertexLabel { get; internal set; }

        /// <summary>
        /// Gets the out vertex label.
        /// </summary>
        /// <value>
        /// The out vertex label.
        /// </value>
        public string OutVertexLabel { get; internal set; }

        /// <summary>
        /// Gets the in vertex partition key.
        /// </summary>
        /// <value>
        /// The in vertex partition key.
        /// </value>
        public object InVertexPartitionKey { get; internal set; }

        /// <summary>
        /// Gets the out vertex partition key.
        /// </summary>
        /// <value>
        /// The out vertex partition key.
        /// </value>
        public object OutVertexPartitionKey { get; internal set; }

        /// <summary>
        /// Gets or sets the properties.
        /// </summary>
        /// <value>
        /// The properties.
        /// </value>
        internal GremlinPropertyCollection Properties { get; set; }

        /// <summary>
        /// Gets a property on the edge, given the property key.
        /// </summary>
        /// <param name="key">The key of the property.</param>
        /// <returns>Property that matches to <paramref name="key"/>.</returns>
        public GremlinProperty GetProperty(string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException(nameof(key));
            }

            GremlinProperty property;
            if (!this.Properties.TryGetProperty(key, out property))
            {
                return null;
            }

            return property;
        }

        /// <summary>
        /// Gets all the propeties on the edge.
        /// </summary>
        /// <returns>Enumerable of the edges properties.</returns>
        public IEnumerable<GremlinProperty> GetProperties()
        {
            return this.Properties != null ? this.Properties : Enumerable.Empty<GremlinProperty>();
        }

        /// <summary>
        /// Adds the property.
        /// </summary>
        /// <param name="key">The key.</param>
        /// <param name="value">The value.</param>
        /// <returns>This <see cref="Edge"/> for method chaining.</returns>
        public GremlinEdge AddProperty(string key, object value)
        {
            if (this.Properties == null)
            {
                this.Properties = new GremlinPropertyCollection();
            }

            this.Properties.Add(new GremlinProperty(key, value));
            return this;
        }

        /// <summary>
        /// Validates this instance.
        /// </summary>
        /// <exception cref="System.ArgumentNullException">
        /// Edge must have a valid Id.
        /// or
        /// Edge must have a valid Label.
        /// </exception>
        /// <exception cref="System.ArgumentException">
        /// Edge must specify InVertexId.
        /// or
        /// Edge must specify OutVertexId.
        /// or
        /// Edge must specify InVertexLabel.
        /// or
        /// Edge must specify OutVertexLabel.
        /// </exception>
        public virtual void Validate()
        {
            if (this.Id == null)
            {
                throw new Exception("Graph Element: Edge: Validate: Edge must have a valid Id.");
            }

            if (string.IsNullOrEmpty(this.Label))
            {
                throw new Exception("Graph Element: Edge: Validate: Edge must have a valid Label.");
            }

            if (this.InVertexId == null)
            {
                throw new Exception("Graph Element: Edge: Validate: Edge must have a valid InVertexId.");
            }

            if (this.OutVertexId == null)
            {
                throw new Exception("Graph Element: Edge: Validate: Edge must have a valid OutVertexId.");
            }

            if (string.IsNullOrEmpty(this.InVertexLabel))
            {
                throw new Exception("Graph Element: Edge: Validate: Edge must have a valid InVertexLabel.");
            }

            if (string.IsNullOrEmpty(this.OutVertexLabel))
            {
                throw new Exception("Graph Element: Edge: Validate: Edge must specify OutVertexLabel.");
            }
        }
    }
}
