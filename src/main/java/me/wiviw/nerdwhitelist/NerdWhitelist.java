package me.wiviw.nerdwhitelist;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.extern.log4j.Log4j2;
import me.wiviw.nerdwhitelist.events.JoinListener;
import me.wiviw.nerdwhitelist.util.redis.RedisClient;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Plugin(id = "nerdwhitelist", name = "NerdWhitelist", version = "0.0.1", authors = {"WiViW"})
@Log4j2
public class NerdWhitelist {


    private final ProxyServer server;
    public static RedisClient redisClient;

    @Inject
    public NerdWhitelist(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        if (System.getProperty("db.redis.uri") == null) {
            log.error("db.redis.uri is null or not set, please set this so the whitelist will work.");
            server.shutdown();
            return;
        }

        // This block is to catch if redis is actually alive, and to start the subscription thread along with sending a request to the bot.
        try {
            redisClient = new RedisClient(System.getProperty("db.redis.uri"));
        } catch (JedisConnectionException exception) {
            log.error("Failed to Connect: {}", exception.toString());
            server.shutdown();
            return;
        } catch (Exception exception) {
            log.error("db.redis.uri is either not set or is invalid, please set this so the whitelist will work. {}", exception.toString());
            server.shutdown();
            return;
        }

        server.getEventManager().register(this, new JoinListener());
    }
}
