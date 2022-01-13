// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3.Graph.Element
{
    using System;

    /// <summary>
    /// Storage container for a GremlinProperty.
    /// Supports deserialization from GraphSON format.
    /// </summary>
    public sealed class GremlinProperty : IEquatable<GremlinProperty>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="GremlinProperty"/> class.
        /// </summary>
        internal GremlinProperty()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="GremlinProperty"/> class.
        /// </summary>
        /// <param name="key">The key.</param>
        /// <param name="value">The value.</param>
        internal GremlinProperty(string key, object value)
        {
            this.Key = key;
            this.Value = value;
        }

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
        /// Indicates whether the current object is equal to another object of the same type.
        /// </summary>
        /// <param name="other">An object to compare with this object.</param>
        /// <returns>
        /// true if the current object is equal to the <paramref name="other" /> parameter; otherwise, false.
        /// </returns>
        public bool Equals(GremlinProperty other)
        {
            if (other == null)
            {
                return false;
            }

            if (string.Equals(this.Key, other.Key, StringComparison.OrdinalIgnoreCase) &&
                this.Value.GetType() == other.Value.GetType())
            {
                return this.Value.Equals(other.Value);
            }

            return false;
        }

        /// <summary>
        /// Validates this instance.
        /// </summary>
        /// <exception cref="System.ArgumentNullException">
        /// GremlinProperty must have a valid Key.
        /// or
        /// GremlinProperty must have a valid Value.
        /// </exception>
        public void Validate()
        {
            if (string.IsNullOrEmpty(this.Key))
            {
                throw new Exception($"Graph Element: GremlinProperty: Validate: {nameof(this.Key)} must have a valid {nameof(this.Key)}.");
            }

            if (this.Value == null)
            {
                throw new Exception($"Graph Element: GremlinProperty: Validate: {nameof(this.Value)} must have a valid {nameof(this.Value)}.");
            }
        }
    }
}