// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3
{
    using System;
    using System.Threading.Tasks;
    using Microsoft.Azure.Cosmos;

    internal static class TaskExtensions
    {
        /// <summary>
        /// Captures the result of a Cosmos item operation into an <see cref="OperationResponse{T}"/>
        /// </summary>
        public static async Task<OperationResponse<T>> CaptureOperationResponse<T>(this Task<ItemResponse<T>> task, T item)

        {
            try
            {
                ItemResponse<T> response = await task;
                return new OperationResponse<T>()
                {
                    Item = item,
                    IsSuccessful = true,
                    RequestUnitsConsumed = task.Result.RequestCharge
                };
            }
            catch (Exception ex)
            {
                if (ex is CosmosException cosmosException)
                {
                    return new OperationResponse<T>()
                    {
                        Item = item,
                        RequestUnitsConsumed = cosmosException.RequestCharge,
                        IsSuccessful = false,
                        CosmosException = cosmosException
                    };
                }

                return new OperationResponse<T>()
                {
                    Item = item,
                    IsSuccessful = false,
                    CosmosException = ex
                };
            }
        }
    }
}
