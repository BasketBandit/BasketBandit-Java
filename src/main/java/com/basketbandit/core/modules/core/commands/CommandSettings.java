package com.basketbandit.core.modules.core.commands;

import com.basketbandit.core.Configuration;
import com.basketbandit.core.SystemInformation;
import com.basketbandit.core.database.DatabaseConnection;
import com.basketbandit.core.database.DatabaseFunctions;
import com.basketbandit.core.modules.Command;
import com.basketbandit.core.modules.core.settings.SettingCommandPrefix;
import com.basketbandit.core.modules.core.settings.SettingDeleteExecuted;
import com.basketbandit.core.utils.Utils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;

public class CommandSettings extends Command {

    public CommandSettings() {
        super("settings", "com.basketbandit.core.modules.core.ModuleCore", new String[]{"-settings", "-settings [setting] [value]"}, Permission.MANAGE_SERVER);
    }

    public CommandSettings(MessageReceivedEvent e, String[] command) {
        executeCommand(e, command);
    }

    @Override
    protected void executeCommand(MessageReceivedEvent e, String[] command) {
        try {
            if(command.length > 1) {
                String[] commandParameters = command[1].split("\\s+", 2);

                // Check to make sure the command is a valid command.
                if(!SystemInformation.getSettingsList().contains(commandParameters[0].toLowerCase())) {
                    Utils.sendMessage(e, "Sorry, '" + commandParameters[0] + "' is not a setting.");
                    return;
                }

                if(commandParameters[0].equalsIgnoreCase("deleteExecuted")) {
                    if(!commandParameters[1].equalsIgnoreCase("true") && !commandParameters[1].equalsIgnoreCase("false")) {
                        Utils.sendMessage(e, "Sorry, '" + commandParameters[1].toUpperCase() + "' is not a valid value. (Valid: TRUE, FALSE, 1, 0)");
                        return;
                    }

                    new SettingDeleteExecuted(e, commandParameters[1]);
                }

                if(commandParameters[0].equalsIgnoreCase("commandPrefix")) {
                    new SettingCommandPrefix(e, commandParameters[1]);
                }

            } else {

                Connection connection = new DatabaseConnection().getConnection();
                try {
                    ResultSet resultSet = new DatabaseFunctions().getServerSettings(connection, e.getGuild().getId());
                    resultSet.next();

                    EmbedBuilder commandModules = new EmbedBuilder()
                        .setColor(Color.DARK_GRAY)
                        .setTitle("Settings for **" + e.getGuild().getName() + "**")
                        .setDescription("Settings can be changed by typing '<prefix>settings [setting] [value]' where [setting] is a value found below and [value] is a valid value, with special values like booleans being either TRUE or FALSE (case insensitive)")
                            .addField("commandPrefix", "[**" + resultSet.getString("commandPrefix") + "**] - The message prefix used to symbolise a command.", false)
                            .addField("deleteExecuted", "[**" + resultSet.getBoolean("deleteExecuted") + "**] - Deletes the users command string when it is executed.", false)
                        .setFooter(Configuration.VERSION, e.getGuild().getMemberById(Configuration.BOT_ID).getUser().getAvatarUrl());

                    Utils.sendMessage(e, commandModules.build());
                } catch(Exception ex) {
                    Utils.sendException(ex, e.getMessage().getContentRaw());
                } finally {
                    try {
                        connection.close();
                    } catch(Exception ex) {
                        Utils.sendException(ex, "Unable to close connection to database. [CommandSettings]");
                    }
                }
            }

        } catch(Exception ex) {
            Utils.sendException(ex, e.getMessage().getContentRaw());
        }

    }

}