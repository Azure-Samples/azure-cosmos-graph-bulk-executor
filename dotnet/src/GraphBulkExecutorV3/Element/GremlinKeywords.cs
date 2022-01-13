// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// ------------------------------------------------------------

namespace GraphBulkExecutorV3.Graph.Element
{
    internal static class GremlinKeywords
    {
        internal const string KW_DOC_ID = "id";
        internal const string KW_DOC_RID = "_rid";
        internal const string KW_DOC_SELF = "_self";
        internal const string KW_DOC_TS = "_ts";
        internal const string KW_DOC_ATTACHMENTS = "_attachments";
        internal const string KW_DOC_LABEL = "label";
        internal const string KW_DOC_PARTITION = "_partition";
        internal const string KW_DOC_ETAG = "_etag";
        internal const string KW_DOC_TTL = "ttl";

        internal const string KW_VERTEX_LABEL = "label";
        internal const string KW_VERTEX_EDGE = "_edge";
        internal const string KW_VERTEX_REV_EDGE = "_reverse_edge";
        internal const string KW_VERTEX_ID_KEY = "_vertexId";

        internal const string KW_PROPERTY_ID = "id";
        internal const string KW_PROPERTY_VALUE = "_value";
        internal const string KW_PROPERTY_META = "_meta";

        internal const string KW_EDGE_LABEL = "label";
        internal const string KW_EDGE_ID = "id";
        internal const string KW_EDGE_SRCV = "_src";
        internal const string KW_EDGE_SRCV_LABEL = "_srcLabel";
        internal const string KW_EDGE_SRCV_PARTITION = "_srcPartition";
        internal const string KW_EDGE_SINKV = "_sink";
        internal const string KW_EDGE_SINKV_LABEL = "_sinkLabel";
        internal const string KW_EDGE_SINKV_PARTITION = "_sinkPartition";
        internal const string KW_EDGE_SINKV_PARTITION_INDEXER = "[\"_sinkPartition\"]";

        internal const string KW_EDGEDOC_VERTEXID = "_vertexId";
        internal const string KW_EDGEDOC_VERTEXLABEL = "_vertexLabel";
        internal const string KW_EDGEDOC_ISREVERSE = "_isReverse";
        internal const string KW_EDGEDOC_EDGE = KW_VERTEX_EDGE;
        internal const string KW_EDGEDOC_IDENTIFIER = "_isEdge";
        internal const string KW_EDGEDOC_ISPKPROPERTY = "_isPkEdgeProperty";

        internal const string KW_GRAPH_METADATA_DOCUMENT_ID = "metadata";
        internal const string KW_GRAPH_METADATA_DOCUMENT_PARTITION_KEY = "metapartition";

        internal const string KW_TABLE_DEFAULT_COLUMN_NAME = "_value";
    }
}