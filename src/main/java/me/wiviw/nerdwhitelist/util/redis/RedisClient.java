package me.wiviw.nerdwhitelist.util.redis;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;

@Log4j2
public class RedisClient {

    public static Jedis subscribeJedis;

    @Getter
    @Setter
    private static Map<String, JsonElement> memberList;

    private static class RunnableMemberList implements Runnable {

        public void run() {
            subscribeJedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    memberList = JsonParser.parseString(message).getAsJsonObject().asMap();

                    log.info("Updated Member list!\n{}", memberList.toString());
                }
            }, "MemberList");
        }
    }

    public RedisClient(String uri) {
        subscribeJedis = new Jedis(uri);

        // Starting the Subscribe Jedis Client.
        Thread t1 = new Thread(new RunnableMemberList());
        t1.start();

        // Sending the request for the first set of Member list.
        Jedis publishJedis = new Jedis(uri);
        publishJedis.publish("Requests", "Please Grant new names!");
    }
}
