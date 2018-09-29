package com.basketbandit.core.modules.utility.commands;

import com.basketbandit.core.modules.Command;
import com.basketbandit.core.utils.Utils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandChannel extends Command {

    public CommandChannel() {
        super("channel", "com.basketbandit.core.modules.utility.ModuleUtility", new String[]{"-channel [action] [type] [name]", "-channel [action] [type] [name] [nsfw]"}, Permission.MANAGE_CHANNEL);
    }

    public CommandChannel(MessageReceivedEvent e, String[] command) {
        executeCommand(e, command);
    }

    @Override
    protected void executeCommand(MessageReceivedEvent e, String[] command) {
        String[] commandParameters = command[1].split("\\s+", 3);
        String type = commandParameters[1].toLowerCase();

        if(commandParameters[0].equals("add")) {
            if(type.equals("text")) {
                e.getGuild().getController().createTextChannel(commandParameters[1]).setNSFW(commandParameters.length > 2).queue();
            } else if(type.equals("voice")) {
                e.getGuild().getController().createVoiceChannel(commandParameters[1]).queue();
            }

        } else if(commandParameters[0].equals("del")) {
            if(type.equals("text")) {
                if(e.getGuild().getTextChannelsByName(commandParameters[2], true).size() == 0) {
                    Utils.sendMessage(e, "Sorry, that text-channel could not be found.");
                    return;
                }
                e.getGuild().getTextChannelsByName(commandParameters[2], true).get(0).delete().queue();
            } else if(type.equals("voice") && commandParameters[2].length() == 18 && Long.parseLong(commandParameters[2]) > 0) {
                if(e.getGuild().getVoiceChannelsByName(commandParameters[2], true).size() == 0) {
                    Utils.sendMessage(e, "Sorry, that voice-channel could not be found.");
                    return;
                }
                e.getGuild().getVoiceChannelsByName(commandParameters[1], true).get(0).delete().queue();
            } else {
                e.getTextChannel().delete().queue();
            }

        } else {
            Utils.sendMessage(e, "Sorry, something went wrong when trying to perform that action.");
        }
    }
}