package dev.iiahmed.mexmpl.command;

import dev.iiahmed.disguise.*;

import dev.iiahmed.disguise.util.DisguiseUtil;
import dev.iiahmed.mexmpl.ModernExample;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.command.CommandActor;

@Command("disguise")
@SuppressWarnings("unused")
public class DisguiseCommand {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    public DisguiseCommand() {
        DisguiseManager.setPlugin(ModernExample.getInstance());
        provider.allowOverrideChat(false);
    }

    @Subcommand("info")
    public void info(
            final CommandActor actor
    )
    {
        String foundColor = dev.iiahmed.disguise.util.DisguiseUtil.found < 56? "&c" : "&a"; // 56 is amount of entities detected on 1.8.8
        actor.reply(translate("Found enitities: " + foundColor + dev.iiahmed.disguise.util.DisguiseUtil.found));

        String livingColor = dev.iiahmed.disguise.util.DisguiseUtil.living < 33? "&c" : "&a"; // 33 is amount of entities living in 1.8.8
        actor.reply(translate("Extends LivingEntity: " + livingColor + dev.iiahmed.disguise.util.DisguiseUtil.living));

        String registeredColor = dev.iiahmed.disguise.util.DisguiseUtil.registered < 32? "&c" : "&a"; // 32 is amount of entities registered in 1.8.8
        actor.reply(translate("Has a Constructor (aka. Registered): " + registeredColor + DisguiseUtil.registered));
    }

    @Subcommand("player")
    public @NotNull String asPlayer(
            final Player player,
            @Flag("name") @Default("BillBobbyBob") String name,
            @Flag("skin") @Optional String skin,
            @Flag("api") @Default("MOJANG") SkinAPI api
    )
    {
        long time = System.currentTimeMillis();
        Disguise.Builder builder = Disguise.builder().setName(name);

        if (skin != null) {
            builder.setSkin(skin, api);
        }

        return provider.disguise(player, builder.build()) + " (done in " + (System.currentTimeMillis() - time) + "ms)";
    }

    @Subcommand("entity")
    public @NotNull String asEntity(
            final Player player,
            @Default("ZOMBIE") EntityType type
    )
    {
        long time = System.currentTimeMillis();
        Disguise disguise = Disguise.builder()
                .setEntityType(type)
                .build();
        return provider.disguise(player, disguise) + " (done in " + (System.currentTimeMillis() - time) + "ms)";
    }

    @Command("undisguise")
    public @NotNull String undisguise(
            final Player player
    )
    {
        long time = System.currentTimeMillis();
        return provider.undisguise(player) + " (done in " + (System.currentTimeMillis() - time) + "ms)";
    }

    private String translate(final String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
