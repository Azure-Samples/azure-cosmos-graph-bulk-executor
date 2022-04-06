// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

public class SeedGenerationValues {
    private SeedGenerationValues() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    protected static final String[] firstNames = new String[]{
            "John", "Shawn", "Sean", "Shawna", "Jane", "Alexis", "Allan", "Sara", "Sarah", "Janet", "Selah",
            "Anastasia", "Juanita", "Jesus"
    };

    protected static final String[] lastNames = new String[]{
            "Doe", "Smith", "Nagarajan", "Jones", "Jackson", "Diaz", "Williams", "Brown", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez"
    };

    protected static final String[] countries = new String[]{
            "AFG", "USA", "ALB", "BHS", "BRA", "CHN", "CZE", "EGY", "GUM", "GIN", "HND", "HUN", "MDG", "MLI"
    };

    protected static final String[] emailProviders = new String[]{
            "gmail", "yahoo", "me", "outlook", "aol", "yandex", "proton", "zoho", "tutanota"
    };

    protected static final String[] RelationshipTypes = new String[]{
            "spouse", "child", "friend", "enemy", "co-worker", "guardian", "parent", "grand parent",
            "cousin", "partner", "ally"
    };
}
