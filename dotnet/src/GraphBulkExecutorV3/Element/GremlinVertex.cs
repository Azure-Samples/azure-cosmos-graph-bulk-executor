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
    /// Storage container for vertex data.
    /// Supports deserialization from GraphSON format.
    /// </summary>
    public class GremlinVertex : IGremlinElement
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="GremlinVertex"/> class.
        /// </summary>
        /// <param name="id">The identifier.</param>
        /// <param name="label">The label.</param>
        public GremlinVertex(string id, string label)
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


        internal Dictionary<string, List<GremlinVertexProperty>> Properties { get; set; }

        /// <summary>
        /// Adds the <see cref="VertexProperty"/> to this <see cref="GremlinVertex"/>.
        /// </summary>
        /// <param name="vertexProperty">The vertex property data.</param>
        /// <returns>The <see cref="VertexProperty"/> that was passed in for method chaining.</returns>
        public void AddProperty(string key, object value)
        {
            if (string.IsNullOrEmpty(key))
            {
                throw new Exception($"Graph Element: VertexProperty: Must have a valid Key.");
            }

            if (value == null)
            {
                throw new Exception($"Graph Element: VertexProperty: Must not have a null Value.");
            }

            if (this.Properties == null)
            {
                this.Properties = new Dictionary<string, List<GremlinVertexProperty>>();
            }

            List<GremlinVertexProperty> propertiesForKey = null;
            if (!this.Properties.TryGetValue(key, out propertiesForKey))
            {
                propertiesForKey = new List<GremlinVertexProperty>();
                this.Properties.Add(key, propertiesForKey);
            }

            propertiesForKey.Add(new GremlinVertexProperty(key, value));
        }

        /// <summary>
        /// Adds the <see cref="VertexProperty"/> to this <see cref="GremlinVertex"/>.
        /// </summary>
        /// <param name="vertexProperty">The vertex property data.</param>
        /// <returns>The <see cref="VertexProperty"/> that was passed in for method chaining.</returns>
        public GremlinVertexProperty AddProperty(GremlinVertexProperty vertexProperty)
        {
            vertexProperty.Validate();

            if (this.Properties == null)
            {
                this.Properties = new Dictionary<string, List<GremlinVertexProperty>>();
            }

            List<GremlinVertexProperty> propertiesForKey = null;
            if (!this.Properties.TryGetValue(vertexProperty.Key, out propertiesForKey))
            {
                propertiesForKey = new List<GremlinVertexProperty>();
                this.Properties.Add(vertexProperty.Key, propertiesForKey);
            }

            propertiesForKey.Add(vertexProperty);
            return vertexProperty;
        }


        /// <summary>
        /// Gets the keys of all the properties
        /// </summary>
        /// <returns>Enumerable of property keys.</returns>
        public IEnumerable<string> GetPropertyKeys()
        {
            return this.Properties != null ? this.Properties.Keys : Enumerable.Empty<string>();
        }

        /// <summary>
        /// Gets the vertex properties that correspond to a given property key.
        /// </summary>
        /// <param name="key">The property key to query.</param>
        /// <returns>Enumerable containing vertex properties matching the <paramref name="key"/>.</returns>
        public IEnumerable<GremlinVertexProperty> GetVertexProperties(string key)
        {
            if (key == null)
            {
                throw new Exception($"Graph Element: Vertex: Get Property {nameof(key)}");
            }

            return this.Properties != null ? this.GetNestedEnumerable(this.Properties, key) : Enumerable.Empty<GremlinVertexProperty>();
        }

        /// <summary>
        /// Gets all the vertex properties on the vertex.
        /// </summary>
        /// <returns>Enumerable containing all vertex properties.</returns>
        public IEnumerable<GremlinVertexProperty> GetVertexProperties()
        {
            return this.Properties != null ? this.GetNestedEnumerable(this.Properties) : Enumerable.Empty<GremlinVertexProperty>();
        }

        /// <summary>
        /// Validates this instance.
        /// </summary>
        /// <exception cref="System.ArgumentNullException">
        /// Vertex must have a valid Id.
        /// or
        /// Vertex must have a valid Label.
        /// </exception>
        public void Validate()
        {
            if (this.Id == null)
            {
                throw new Exception($"Graph Element: Vertex: Validate: Vertex must have a valid Id.");
            }

            if (string.IsNullOrEmpty(this.Label))
            {
                throw new Exception($"Graph Element: Vertex: Validate: Vertex must have a valid Label.");
            }
        }


        /// <summary>
        /// Gets an enumerable which represents a flat set of elements from a dictionary.
        /// </summary>
        /// <typeparam name="T">Type of the element.</typeparam>
        /// <param name="dictionary">Original dictionary which contains a nested collection.</param>
        /// <returns>Enumerable of each nested element.</returns>
        internal IEnumerable<T> GetNestedEnumerable<T>(Dictionary<string, List<T>> dictionary)
        {
            return dictionary.Keys.SelectMany(k => this.GetNestedEnumerable<T>(dictionary, k));
        }

        /// <summary>
        /// Gets an enumerable which represents a flat set of elements from a dictionary.
        /// </summary>
        /// <typeparam name="T">Type of the element.</typeparam>
        /// <param name="dictionary">Original dictionary which contains a nested collection.</param>
        /// <param name="key">Key to use when extracting the nested elements from the dictionary.</param>
        /// <returns>Enumerable of each nested element.</returns>
        internal IEnumerable<T> GetNestedEnumerable<T>(Dictionary<string, List<T>> dictionary, string key)
        {
            if (key == null)
            {
                throw new ArgumentNullException(nameof(key));
            }

            List<T> valueList = dictionary[key];
            foreach (T value in valueList)
            {
                yield return value;
            }
        }
    }
}