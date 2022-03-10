package com.microsoft.graph.bulk.sample;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseSettings {


    private static Properties Properties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        var properties = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String MASTER_KEY =
            System.getProperty("ACCOUNT_KEY",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("ACCOUNT_KEY")),
                            Properties().getProperty("sample.sql.key")));

    public static String HOST =
            System.getProperty("ACCOUNT_HOST",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("ACCOUNT_HOST")),
                            Properties().getProperty("sample.sql.host")));

    public static String DATABASE_NAME =
            System.getProperty("DATABASE_NAME",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("DATABASE_NAME")),
                            Properties().getProperty("sample.sql.database.name")));

    public static String CONTAINER_NAME =
            System.getProperty("CONTAINER_NAME",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("CONTAINER_NAME")),
                            Properties().getProperty("sample.sql.container.name")));

    public static String PARTITION_KEY_PATH =
            System.getProperty("PARTITION_KEY_PATH",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("PARTITION_KEY_PATH")),
                            Properties().getProperty("sample.sql.partition.path")));

    public static final int THROUGHPUT = Integer.parseInt(Properties().getProperty("sample.sql.allow.throughput"));

    public static Boolean ALLOW_UPSERT =
            Boolean.parseBoolean(Properties().getProperty("sample.sql.allow.upserts"));

    public static String CONTACT_POINT =
        System.getProperty("CONTACT_POINT",
            StringUtils.defaultString(StringUtils.trimToNull(
                System.getenv().get("CONTACT_POINT")),
                Properties().getProperty("sample.gremlin.contactPoint")));

    public static int PORT =
        Integer.parseInt(System.getProperty("PORT",
            StringUtils.defaultString(StringUtils.trimToNull(
                System.getenv().get("PORT")),
                Properties().getProperty("sample.gremlin.port"))));

    public static boolean SSL_ENABLED =
        Boolean.parseBoolean(System.getProperty("SSL_ENABLED",
            StringUtils.defaultString(StringUtils.trimToNull(
                System.getenv().get("SSL_ENABLED")),
                Properties().getProperty("sample.gremlin.sslEnabled"))));

    public static int MAX_CONNECTION_POOL_SIZE =
        Integer.parseInt(System.getProperty("MAX_CONNECTION_POOL_SIZE",
            StringUtils.defaultString(StringUtils.trimToNull(
                System.getenv().get("MAX_CONNECTION_POOL_SIZE")),
                Properties().getProperty("sample.gremlin.maxConnectionPoolSize"))));

    public static int MAX_WAIT_FOR_CONNECTION =
        Integer.parseInt(System.getProperty("MAX_WAIT_FOR_CONNECTION",
            StringUtils.defaultString(StringUtils.trimToNull(
                System.getenv().get("MAX_WAIT_FOR_CONNECTION")),
                Properties().getProperty("sample.gremlin.maxWaitForConnection"))));
}
