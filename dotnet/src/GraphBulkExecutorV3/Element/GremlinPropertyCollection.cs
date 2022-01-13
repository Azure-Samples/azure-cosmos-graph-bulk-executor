// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3.Graph.Element
{
    using System;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;

    /// <summary>
    /// An indexed collection of <see cref="Property" />.
    /// </summary>
    /// <seealso cref="System.Collections.ObjectModel.KeyedCollection{T, V}" />
    internal sealed class GremlinPropertyCollection : KeyedCollection<string, GremlinProperty>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="GremlinPropertyCollection"/> class.
        /// </summary>
        internal GremlinPropertyCollection()
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="GremlinPropertyCollection"/> class with the specified items.
        /// </summary>
        /// <param name="items">An <see cref="IEnumerable{T}"/> to initialize the collection with.</param>
        internal GremlinPropertyCollection(IEnumerable<GremlinProperty> items)
        {
            if (items != null)
            {
                this.AddRange(items);
            }
        }

        /// <summary>
        /// Tries to get the <see cref="Property"/> from the collection.
        /// </summary>
        /// <param name="key">The key.</param>
        /// <param name="property">The query.</param>
        /// <returns>True if successful.</returns>
        public bool TryGetProperty(string key, out GremlinProperty property)
        {
            if (this.Dictionary != null)
            {
                return this.Dictionary.TryGetValue(key, out property);
            }

            property = null;
            return false;
        }

        /// <summary>
        /// Adds the elements of the specified collection to the end of the <see cref="GremlinPropertyCollection" />.
        /// </summary>
        /// <param name="items">The collection whose elements should be added to the end of the <see cref="GremlinPropertyCollection" />. The collection itself cannot be null and it cannot contain elements that are null.</param>
        /// <exception cref="System.ArgumentNullException">The items cannot be null.</exception>
        internal void AddRange(IEnumerable<GremlinProperty> items)
        {
            if (items == null)
            {
                throw new Exception($"Graph Element: Property Collection: Add Range: null {nameof(items)}");
            }

            // Loop through the items and add them to the list.
            foreach (GremlinProperty property in items)
            {
                this.Add(property);
            }
        }

        /// <summary>
        /// Gets the key for the item.
        /// </summary>
        /// <param name="item">The item to get the key for.</param>
        /// <returns>The key of the item.</returns>
        protected override string GetKeyForItem(GremlinProperty item)
        {
            return item.Key;
        }
    }
}