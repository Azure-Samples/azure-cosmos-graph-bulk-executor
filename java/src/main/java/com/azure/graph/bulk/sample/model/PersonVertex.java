// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample.model;

import com.azure.graph.bulk.impl.annotations.GremlinId;
import com.azure.graph.bulk.impl.annotations.GremlinIgnore;
import com.azure.graph.bulk.impl.annotations.GremlinPartitionKey;
import com.azure.graph.bulk.impl.annotations.GremlinProperty;
import com.azure.graph.bulk.impl.annotations.GremlinVertex;

// GremlinVertex
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

    PersonVertex(PersonVertex.PersonVertexBuilder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.country = builder.country;
        this.isSpecial = builder.isSpecial;
    }

    public static PersonVertex.PersonVertexBuilder builder() {
        return new PersonVertex.PersonVertexBuilder();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PersonVertex)) return false;
        PersonVertex other = (PersonVertex) o;

        if (isNotEqual(isSpecial, other.isSpecial)) return false;
        if (isNotEqual(id, other.id)) return false;
        if (isNotEqual(firstName, other.firstName)) return false;
        if (isNotEqual(lastName, other.lastName)) return false;
        if (isNotEqual(email, other.email)) return false;
        //noinspection RedundantIfStatement
        if (isNotEqual(country, other.country)) return false;

        return true;
    }

    private boolean isNotEqual(Object source, Object other) {
        if (source == null && other == null) return false;
        if (source == null) return true;
        return !source.equals(other);
    }

    public int hashCode() {
        int result = 59 + (isSpecial == null ? 43 : isSpecial.hashCode());
        result = result * 59 + (id == null ? 43 : id.hashCode());
        result = result * 59 + (firstName == null ? 43 : firstName.hashCode());
        result = result * 59 + (lastName == null ? 43 : lastName.hashCode());
        result = result * 59 + (email == null ? 43 : email.hashCode());
        result = result * 59 + (country == null ? 43 : country.hashCode());
        return result;
    }

    public static class PersonVertexBuilder {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String country;
        private Boolean isSpecial;

        PersonVertexBuilder() {
        }

        public PersonVertex.PersonVertexBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PersonVertex.PersonVertexBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public PersonVertex.PersonVertexBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public PersonVertex.PersonVertexBuilder email(String email) {
            this.email = email;
            return this;
        }

        public PersonVertex.PersonVertexBuilder country(String country) {
            this.country = country;
            return this;
        }

        public PersonVertex.PersonVertexBuilder isSpecial(Boolean isSpecial) {
            this.isSpecial = isSpecial;
            return this;
        }

        public PersonVertex build() {
            return new PersonVertex(this);
        }
    }
}
