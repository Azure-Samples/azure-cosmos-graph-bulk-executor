// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.graph.bulk.sample;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseSettings {
    private DatabaseSettings() {
        throw new IllegalStateException("Utility class, should not be constructed");
    }

    private static Properties properties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties properties = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream("application.properties")) {
            properties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static final String MASTER_KEY =
            System.getProperty("ACCOUNT_KEY",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("ACCOUNT_KEY")),
                            properties().getProperty("sample.sql.key")));

    public static final String HOST =
            System.getProperty("ACCOUNT_HOST",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("ACCOUNT_HOST")),
                            properties().getProperty("sample.sql.host")));

    public static final String DATABASE_NAME =
            System.getProperty("DATABASE_NAME",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("DATABASE_NAME")),
                            properties().getProperty("sample.sql.database.name")));

    public static final String CONTAINER_NAME =
            System.getProperty("CONTAINER_NAME",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("CONTAINER_NAME")),
                            properties().getProperty("sample.sql.container.name")));

    public static final String PARTITION_KEY_PATH =
            System.getProperty("PARTITION_KEY_PATH",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("PARTITION_KEY_PATH")),
                            properties().getProperty("sample.sql.partition.path")));

    public static final int THROUGHPUT = Integer.parseInt(properties().getProperty("sample.sql.allow.throughput"));

    public static final String CONTACT_POINT =
            System.getProperty("CONTACT_POINT",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("CONTACT_POINT")),
                            properties().getProperty("sample.gremlin.contactPoint")));

    public static final int PORT =
            Integer.parseInt(System.getProperty("PORT",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("PORT")),
                            properties().getProperty("sample.gremlin.port"))));

    public static final boolean SSL_ENABLED =
            Boolean.parseBoolean(System.getProperty("SSL_ENABLED",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("SSL_ENABLED")),
                            properties().getProperty("sample.gremlin.sslEnabled"))));

    public static final int MAX_CONNECTION_POOL_SIZE =
            Integer.parseInt(System.getProperty("MAX_CONNECTION_POOL_SIZE",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("MAX_CONNECTION_POOL_SIZE")),
                            properties().getProperty("sample.gremlin.maxConnectionPoolSize"))));

    public static final int MAX_WAIT_FOR_CONNECTION =
            Integer.parseInt(System.getProperty("MAX_WAIT_FOR_CONNECTION",
                    StringUtils.defaultString(StringUtils.trimToNull(
                                    System.getenv().get("MAX_WAIT_FOR_CONNECTION")),
                            properties().getProperty("sample.gremlin.maxWaitForConnection"))));
}
