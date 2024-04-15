package me.wiviw.nerdwhitelist.events;


import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import me.wiviw.nerdwhitelist.NerdWhitelist;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Set;

import static me.wiviw.nerdwhitelist.NerdWhitelist.redisClient;

public class JoinListener {

    private final Component whitelistMessage = Component.text("You are not whitelisted!").color(TextColor.fromHexString("#FF3B06"))
            .appendNewline()
            .append(Component.text("If you think this is a mistake, please check that you are verified!").color(TextColor.fromHexString("#FFFFFF")))
            .asComponent();


    @Subscribe
    public void onLogin(LoginEvent event) {
        NerdWhitelist.getLogger().info("Player: " + event.getPlayer().getUsername() + " UUID: " + event.getPlayer().getUniqueId());

        String playerUUID = event.getPlayer().getUniqueId().toString();

        Set<String> test = redisClient.getSetMembers("members");
        for (String member : test) {
            if (member.equals(playerUUID)) {
                NerdWhitelist.getLogger().info("Success!");
                return;
            }
        }
        event.setResult(ResultedEvent.ComponentResult.denied(whitelistMessage));
    }
}
