// Program: Yuuko (Discord Bot)
// Programmer: Joshua Mark Hunt

package com.yuuko.core;

import lavalink.client.io.Link;

public class Yuuko {
    public static void main(String[] args) {
        new Config().setup();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Config.LAVALINK.getLavalink().getLinks().forEach(Link::destroy);
            Config.SHARD_MANAGER.shutdown();
        }));
    }
}