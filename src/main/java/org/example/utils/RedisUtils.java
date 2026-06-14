package org.example.utils;

import redis.clients.jedis.Jedis;

public class RedisUtils {

    public static Jedis getJedis() {

        Jedis jedis =
                new Jedis(
                        RedisConfig.getHost(),
                        RedisConfig.getPort()
                );

        String password =
                RedisConfig.getPassword();

        if (password != null &&
                !password.isEmpty()) {

            jedis.auth(password);
        }

        jedis.select(
                RedisConfig.getDatabase()
        );

        return jedis;
    }
}