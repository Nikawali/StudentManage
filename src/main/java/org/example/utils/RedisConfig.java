package org.example.utils;


import java.io.InputStream;
import java.util.Properties;

public class RedisConfig {

    private static final Properties properties =
            new Properties();

    static {

        try (
                InputStream in =
                        RedisConfig.class
                                .getClassLoader()
                                .getResourceAsStream(
                                        "db.properties"
                                )
        ) {

            properties.load(in);

        } catch (Exception e) {

            throw new RuntimeException(
                    "读取Redis配置失败",
                    e
            );
        }
    }

    public static String getHost() {

        return properties.getProperty(
                "redis.host"
        );
    }

    public static int getPort() {

        return Integer.parseInt(
                properties.getProperty(
                        "redis.port"
                )
        );
    }

    public static String getPassword() {

        return properties.getProperty(
                "redis.password"
        );
    }

    public static int getDatabase() {

        return Integer.parseInt(
                properties.getProperty(
                        "redis.database"
                )
        );
    }

    public static int getTimeout() {

        return Integer.parseInt(
                properties.getProperty(
                        "redis.timeout"
                )
        );
    }
}