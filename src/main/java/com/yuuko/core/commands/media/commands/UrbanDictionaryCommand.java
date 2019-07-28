package com.yuuko.core.commands.media.commands;

import com.google.gson.JsonObject;
import com.yuuko.core.Configuration;
import com.yuuko.core.MessageHandler;
import com.yuuko.core.commands.Command;
import com.yuuko.core.events.entity.MessageEvent;
import com.yuuko.core.io.RequestHandler;
import com.yuuko.core.utilities.Sanitiser;
import net.dv8tion.jda.api.EmbedBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class UrbanDictionaryCommand extends Command {

    private static final String BASE_URL = "https://api.urbandictionary.com/v0/define?term=";

    public UrbanDictionaryCommand() {
        super("urban", Configuration.MODULES.get("media"), 1, Arrays.asList("-urban <term>"), true, null);
    }

    @Override
    public void onCommand(MessageEvent e) {
        final String url = BASE_URL + Sanitiser.scrubString(e.getParameters(), true);
        final JsonObject json = new RequestHandler(url).getJsonObject();

        if(json == null) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("No Results").setDescription("Something went wrong when trying to execute that request, please try again.");
            MessageHandler.sendMessage(e, embed.build());
            return;
        }

        if(json.get("list").getAsJsonArray().size() < 1) {
            EmbedBuilder embed = new EmbedBuilder().setTitle("No Results").setDescription("Search for `" + e.getParameters() + "` produced no results.");
            MessageHandler.sendMessage(e, embed.build());
            return;
        }

        JsonObject data = json.get("list").getAsJsonArray().get(0).getAsJsonObject();

        double thumbsUp = data.get("thumbs_up").getAsDouble();
        double thumbsDown = data.get("thumbs_down").getAsDouble();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(data.get("word").getAsString(), data.get("permalink").getAsString())
                .setDescription(data.get("definition").getAsString().replace("[", "").replace("]", ""))
                .addField("Example", data.get("example").getAsString().replace("[", "").replace("]", ""), false)
                .setFooter("\uD83D\uDC4D " + data.get("thumbs_up").getAsString() + " \uD83D\uDC4E " + data.get("thumbs_down").getAsString() + " \uD83D\uDCCC " + new BigDecimal((thumbsUp/(thumbsUp+thumbsDown))*100).setScale(2, RoundingMode.HALF_UP) + "%", null);
        MessageHandler.sendMessage(e, embed.build());
    }

}