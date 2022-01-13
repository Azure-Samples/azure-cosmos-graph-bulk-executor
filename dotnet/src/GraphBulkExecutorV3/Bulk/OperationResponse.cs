// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    using System;

    /// <summary>
    /// The result of an invididual operation in the Bulk operation.
    /// </summary>
    internal class OperationResponse<T>
    {
        /// <summary>
        /// The item associated with the operation.
        /// </summary>
        public T Item { get; set; }

        /// <summary>
        /// The amount of request units consumed.
        /// </summary>
        public double RequestUnitsConsumed { get; set; } = 0;

        /// <summary>
        /// Whether or not the operation was successful.
        /// </summary>
        public bool IsSuccessful { get; set; }

        /// <summary>
        /// In case <see cref="IsSuccessful"/> is false, it will contain the associated exception.
        /// </summary>
        public Exception CosmosException { get; set; }
    }
}
