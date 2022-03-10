package com.microsoft.graph.bulk.sample.model;

import com.microsoft.graph.bulk.impl.annotations.GremlinId;
import com.microsoft.graph.bulk.impl.annotations.GremlinIgnore;
import com.microsoft.graph.bulk.impl.annotations.GremlinPartitionKey;
import com.microsoft.graph.bulk.impl.annotations.GremlinProperty;
import com.microsoft.graph.bulk.impl.annotations.GremlinVertex;
import lombok.Builder;
import lombok.Data;

//TODO: Create a sample vertex to demonstrate how a client specific domain class can be decorated to produce a
// GremlinVertex
@Data
@Builder
@GremlinVertex(label = "PERSON")
public class PersonVertex {
    @GremlinId
    public String id;
    public String firstName;
    public String lastName;
    @GremlinProperty(name = "ElectronicMail")
    public String email;
    @GremlinPartitionKey
    public String country;
    @GremlinIgnore
    public Boolean isSpecial;

}
