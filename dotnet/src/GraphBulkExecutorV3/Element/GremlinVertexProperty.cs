// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3.Graph.Element
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using Newtonsoft.Json;

    /// <summary>
    /// Storage container for vertex property data.
    /// Supports deserialization from GraphSON format.
    /// </summary>
    public sealed class GremlinVertexProperty
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="GremlinVertexProperty" /> class.
        /// </summary>
        /// <param name="key">The key.</param>
        /// <param name="value">The value.</param>
        public GremlinVertexProperty(string key, object value)
        {
            this.Key = key;
            this.Value = value;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="VertexProperty" /> class.
        /// </summary>
        /// <param name="id">The identifier.</param>
        /// <param name="key">The key.</param>
        /// <param name="value">The value.</param>
        internal GremlinVertexProperty(object id, string key, object value)
        {
            this.Id = id;
            this.Key = key;
            this.Value = value;
        }

        /// <summary>
        /// Gets the identifier.
        /// </summary>
        /// <value>
        /// The identifier.
        /// </value>
        public object Id { get; internal set; }

        /// <summary>
        /// Gets the key.
        /// </summary>
        /// <value>
        /// The key.
        /// </value>
        public string Key { get; internal set; }

        /// <summary>
        /// Gets the value.
        /// </summary>
        /// <value>
        /// The value.
        /// </value>
        public object Value { get; internal set; }

        /// <summary>
        /// Gets or sets the properties.
        /// </summary>
        /// <value>
        /// The properties.
        /// </value>
        internal GremlinPropertyCollection Properties { get; set; }

        /// <summary>
        /// Gets a property given the property key.
        /// </summary>
        /// <param name="key">The property key.</param>
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
        /// Gets all the meta properties of this <see cref="VertexProperty"/>.
        /// </summary>
        /// <returns>Enumerable of meta properties on the <see cref="VertexProperty"/>.</returns>
        public IEnumerable<GremlinProperty> GetProperties()
        {
            return this.Properties != null ? this.Properties : Enumerable.Empty<GremlinProperty>();
        }

        /// <summary>
        /// Validates this instance.
        /// </summary>
        /// <exception cref="System.ArgumentNullException">
        /// VertexProperty must have a valid Key.
        /// or
        /// VertexProperty must not have a null Value.
        /// </exception>
        public void Validate()
        {
            if (string.IsNullOrEmpty(this.Key))
            {
                throw new Exception($"Graph Element: VertexProperty: Must have a valid Key.");
            }

            if (this.Value == null)
            {
                throw new Exception($"Graph Element: VertexProperty: Must not have a null Value.");
            }
        }

        /// <summary>
        /// Adds the property.
        /// </summary>
        /// <param name="key">The key.</param>
        /// <param name="value">The value.</param>
        /// <returns>This <see cref="VertexProperty"/> for method chaining.</returns>
        public GremlinVertexProperty AddProperty(string key, object value)
        {
            if (this.Properties == null)
            {
                this.Properties = new GremlinPropertyCollection();
            }

            this.Properties.Add(new GremlinProperty(key, value));
            return this;
        }
    }
}