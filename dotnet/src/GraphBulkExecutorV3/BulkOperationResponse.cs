// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    using System;
    using System.Collections.Generic;
    using Newtonsoft.Json.Linq;

    /// <summary>
    /// The result of a Graph Bulk operation.
    /// </summary>
    public sealed class BulkOperationResponse
    {
        /// <summary>
        /// Total of time taken for the operation.
        /// </summary>
        public TimeSpan TotalTimeTaken { get; set; }

        /// <summary>
        /// Count of successful elements.
        /// </summary>
        public int SuccessfulDocuments { get; set; } = 0;

        /// <summary>
        /// Total request units consumed across all elements.
        /// </summary>
        public double TotalRequestUnitsConsumed { get; set; } = 0;

        /// <summary>
        /// List of failures associated with their elements.
        /// </summary>
        public IReadOnlyList<(JObject, Exception)> Failures { get; set; }
    }
}
