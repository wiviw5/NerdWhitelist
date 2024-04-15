package me.wiviw.nerdwhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import me.wiviw.nerdwhitelist.events.JoinListener;
import me.wiviw.nerdwhitelist.util.redis.RedisClient;
import org.slf4j.Logger;
import redis.clients.jedis.exceptions.InvalidURIException;

@Plugin(id = "nerdwhitelist", name = "NerdWhitelist", version = "0.0.1", authors = {"WiViW"})
public class NerdWhitelist {


    private final ProxyServer server;
    @Getter
    public static Logger logger;
    public static RedisClient redisClient;

    @Inject
    public NerdWhitelist(ProxyServer server, Logger logger) {
        this.server = server;
        NerdWhitelist.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (System.getProperty("db.redis.uri") == null) {
            getLogger().error("db.redis.uri is null or not set, please set this so the whitelist will work.");
            server.shutdown();
            return;
        }
        try {
            redisClient = new RedisClient(System.getProperty("db.redis.uri"));
        } catch (InvalidURIException exception) {
            getLogger().error("db.redis.uri is either not set or is invalid, please set this so the whitelist will work.");
            server.shutdown();
            return;
        }



        /*
        redisClient.addToSet("members", "ecd2f42e-4879-449e-ac2f-74814ffb9f0b", "3fcdd9d3-8c60-4bda-b45b-406ab6b2ecd1");
        */

        System.out.println("Set Members: " + redisClient.getSetMembers("members"));


        server.getEventManager().register(this, new JoinListener());
    }
}
