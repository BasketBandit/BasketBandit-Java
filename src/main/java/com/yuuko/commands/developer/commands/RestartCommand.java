package com.yuuko.commands.developer.commands;

import com.yuuko.MessageDispatcher;
import com.yuuko.commands.Command;
import com.yuuko.database.function.ShardFunctions;
import com.yuuko.events.entity.MessageEvent;
import com.yuuko.utilities.Sanitiser;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.regex.Pattern;

public class RestartCommand extends Command {
    private final Pattern range = Pattern.compile("[0-9]+\\s*-\\s*[0-9]+");
    private final Pattern list = Pattern.compile("([0-9]+\\s*,\\s*)+[0-9]+");

    public RestartCommand() {
        super("restart", 0, -1L, Arrays.asList("-restart <shard_id>"), false, null);
    }

    @Override
    public void onCommand(MessageEvent e) throws Exception {
        if(!e.hasParameters()) {
            ShardFunctions.getShardStatistics().forEach(shard -> {
                ShardFunctions.triggerRestartSignal(shard.getId());
            });
            return;
        }

        if(Sanitiser.isNumeric(e.getParameters())) {
            ShardFunctions.triggerRestartSignal(Integer.parseInt(e.getParameters()));
            EmbedBuilder embed = new EmbedBuilder().setTitle("Restart").setDescription("Attempted to set restart trigger for shard: " + e.getParameters());
            MessageDispatcher.reply(e, embed.build());
            return;
        }

        if(range.matcher(e.getParameters()).matches()) {
            String[] shards = e.getParameters().split("-");
            for(int i = Integer.parseInt(shards[0]); i <= Integer.parseInt(shards[1]); i++) {
                ShardFunctions.triggerShutdownSignal(i);
            }
            EmbedBuilder embed = new EmbedBuilder().setTitle("Restart").setDescription("Attempted to set restart trigger for shards: " + e.getParameters());
            MessageDispatcher.reply(e, embed.build());
            return;
        }

        if(list.matcher(e.getParameters()).matches()) {
            String[] shards = e.getParameters().split(",");
            for(String shard: shards) {
                ShardFunctions.triggerShutdownSignal(Integer.parseInt(shard));
            }
            EmbedBuilder embed = new EmbedBuilder().setTitle("Restart").setDescription("Attempted to set restart trigger for shards: " + e.getParameters());
            MessageDispatcher.reply(e, embed.build());
        }
    }

}