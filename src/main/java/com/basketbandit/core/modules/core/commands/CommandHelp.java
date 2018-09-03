package com.basketbandit.core.modules.core.commands;

import com.basketbandit.core.Configuration;
import com.basketbandit.core.database.DatabaseConnection;
import com.basketbandit.core.database.DatabaseFunctions;
import com.basketbandit.core.modules.Command;
import com.basketbandit.core.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;

public class CommandHelp extends Command {

    public CommandHelp() {
        super("help", "com.basketbandit.core.modules.core.ModuleCore", new String[]{"-help", "-help [command]"}, null);
    }

    public CommandHelp(MessageReceivedEvent e, String[] command) {
        executeCommand(e, command);
    }

    @Override
    protected void executeCommand(MessageReceivedEvent e, String[] command) {
        // If command length is smaller than 2 give the regular help DM, else give the command usage embed.
        if(command.length < 2) {
            EmbedBuilder commandInfo = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Hey " + e.getAuthor().getName() + ",")
                    .setDescription(
                            "A full list of modules and features is available on the GitHub, which is located [here](https://github.com/BasketBandit/BasketBandit-Java)! \n" +
                            "If you have an issues or suggestions, please join the [support server](https://discord.gg/QcwghsA) and let me know! The only way the bot can improve is if I'm told when things go wrong!"
                    )
                    .addField("Want me on your server?", "Click [here](https://discordapp.com/api/oauth2/authorize?client_id=420682957007880223&permissions=8&scope=bot) to send me an invite! Also be sure to give me admin privileges if you wish to use the 'nuke' or any other admin commands.", false)
                    .setFooter("Version: " + Configuration.VERSION, e.getGuild().getMemberById(Configuration.BOT_ID).getUser().getAvatarUrl());

            Utils.sendMessage(e, "Check your private messages, " + e.getAuthor().getAsMention() + "! <:ShinobuOshino:420423622663077889>");
            e.getAuthor().openPrivateChannel().queue((privateChannel) -> privateChannel.sendMessage(commandInfo.build()).queue());

        } else {
            for(Command cmd: Utils.commandList) {
                if(cmd.getCommandName().equals(command[1])) {
                    String commandPermission;
                    if(cmd.getCommandPermission() == null) {
                        commandPermission = "None";
                    } else {
                        commandPermission = cmd.getCommandPermission().getName();
                    }

                    StringBuilder usages = new StringBuilder();
                    for(String usage: cmd.getCommandUsage()) {
                        usages.append(usage).append("\n");
                    }
                    usages = Utils.removeLastOccurrence(usages, "\n");

                    StringBuilder bindList = new StringBuilder();
                    StringBuilder excludeList = new StringBuilder();

                    try {
                        Connection connection = new DatabaseConnection().getConnection();
                        ResultSet rs = new DatabaseFunctions().getBindingsExclusionsChannel(connection, e.getGuild().getId(), Utils.extractModuleName(cmd.getCommandModule(), false, true));
                        while(rs.next()) {
                            if(rs.getBoolean(5)) {
                                excludeList.append(e.getGuild().getTextChannelCache().getElementById(rs.getString(2)).getName()).append("\n");
                            } else {
                                bindList.append(e.getGuild().getTextChannelCache().getElementById(rs.getString(2)).getName()).append("\n");
                            }
                        }
                        connection.close();

                        Utils.removeLastOccurrence(bindList, "\n");
                        Utils.removeLastOccurrence(excludeList, "\n");
                        if(bindList.length() == 0) {
                            bindList.append("None");
                        }
                        if(excludeList.length() == 0) {
                            excludeList.append("None");
                        }
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }

                    User bot = e.getGuild().getMemberById(420682957007880223L).getUser();

                    EmbedBuilder commandInfo = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setThumbnail(bot.getAvatarUrl())
                            .setTitle("Command information for: " + cmd.getCommandName())
                            .addField("Module", Utils.extractModuleName(cmd.getCommandModule(), true, false), true)
                            .addField("Required Permission", commandPermission, true)
                            .addField("Binds", bindList.toString(), true)
                            .addField("Exclusions", excludeList.toString(), true)
                            .addField("Usage", usages.toString(), false)
                            .setFooter("Version: " + Configuration.VERSION, e.getGuild().getMemberById(Configuration.BOT_ID).getUser().getAvatarUrl());

                    Utils.sendMessage(e, commandInfo.build());
                    return;
                }
            }
            Utils.sendMessage(e, "Sorry, I can't find a usage for command '" + command[1] + "'");
        }
    }

}