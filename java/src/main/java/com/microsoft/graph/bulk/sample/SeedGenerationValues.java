package com.microsoft.graph.bulk.sample;

public class SeedGenerationValues {
    private SeedGenerationValues() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }
    
    public static String[] firstNames = new String[]{
            "John", "Shawn", "Sean", "Shawna", "Jane", "Alexis", "Allan", "Sara", "Sarah", "Janet", "Selah",
            "Anastasia", "Juanita", "Jesus"
    };

    public static String[] lastNames = new String[]{
            "Doe", "Smith", "Nagarajan", "Jones", "Jackson", "Diaz", "Williams", "Brown", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez"
    };

    public static String[] countries = new String[]{
            "AFG", "USA", "ALB", "BHS", "BRA", "CHN", "CZE", "EGY", "GUM", "GIN", "HND", "HUN", "MDG", "MLI"
    };

    public static String[] emailProviders = new String[]{
            "gmail", "yahoo", "me", "outlook", "aol", "yandex", "proton", "zoho", "tutanota"
    };

    public static String[] RelationshipTypes = new String[]{
            "spouse", "child", "friend", "enemy", "co-worker", "guardian", "parent", "grand parent",
            "cousin", "partner", "ally"
    };
}
