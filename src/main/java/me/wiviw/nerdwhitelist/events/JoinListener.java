package me.wiviw.nerdwhitelist.events;


import com.google.gson.JsonElement;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import lombok.extern.log4j.Log4j2;
import me.wiviw.nerdwhitelist.util.redis.RedisClient;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Map;

@Log4j2
public class JoinListener {

    private final Component whitelistMessage = Component.text("You are not whitelisted!").color(TextColor.fromHexString("#FF3B06"))
            .appendNewline()
            .append(Component.text("If you think this is a mistake, please check that you are verified!").color(TextColor.fromHexString("#FFFFFF")))
            .asComponent();


    @Subscribe
    public void onLogin(LoginEvent event) {
        String playerUUID = event.getPlayer().getUniqueId().toString();

        Map<String, JsonElement> memberList = RedisClient.getMemberList();

        if (memberList == null) {
            event.setResult(ResultedEvent.ComponentResult.denied(whitelistMessage));
            return;
        }


        for (String uuid : memberList.keySet()) {
            if (uuid.equals(playerUUID)) {
                event.setResult(ResultedEvent.ComponentResult.allowed());
                log.info("User: {} Rank: {} UUID: {}", event.getPlayer().getUsername(), memberList.get(playerUUID).getAsString(), playerUUID);
                return;
            }
        }
        event.setResult(ResultedEvent.ComponentResult.denied(whitelistMessage));
    }
}
