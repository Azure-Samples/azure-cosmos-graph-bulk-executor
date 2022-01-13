// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    /// <summary>
    /// Base container for Gremlin data.
    /// </summary>
    public interface IGremlinElement
    {
        /// <summary>
        /// Gets the identifier.
        /// </summary>
        /// <value>
        /// The identifier.
        /// </value>
        string Id { get; }

        /// <summary>
        /// Gets the label.
        /// </summary>
        /// <value>
        /// The label.
        /// </value>
        string Label { get; }

        /// <summary>
        /// Validates this instance.
        /// </summary>
        void Validate();
    }
}
