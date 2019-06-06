package com.yuuko.core.commands.utility.commands;

import com.yuuko.core.Configuration;
import com.yuuko.core.MessageHandler;
import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.utility.UtilityModule;
import com.yuuko.core.events.extensions.MessageEvent;
import com.yuuko.core.metrics.MetricsManager;
import net.dv8tion.jda.core.EmbedBuilder;

import java.time.Instant;
import java.util.Arrays;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping", UtilityModule.class, 0, Arrays.asList("-ping"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Pong!")
                .setDescription("Current ping to Discord is " + MetricsManager.getDiscordMetrics().PING + "ms.")
                .setTimestamp(Instant.now())
                .setFooter(Configuration.STANDARD_STRINGS.get(1) + e.getMember().getEffectiveName(), e.getAuthor().getEffectiveAvatarUrl());
        MessageHandler.sendMessage(e, embed.build());
    }
}
