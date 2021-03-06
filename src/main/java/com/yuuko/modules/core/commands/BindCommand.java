package com.yuuko.modules.core.commands;

import com.yuuko.MessageDispatcher;
import com.yuuko.Yuuko;
import com.yuuko.database.connection.DatabaseConnection;
import com.yuuko.events.entity.MessageEvent;
import com.yuuko.modules.Command;
import com.yuuko.utilities.TextUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class BindCommand extends Command {

    public BindCommand() {
        super("bind", Arrays.asList("-bind <module>", "-bind <module> #channel"), Arrays.asList(Permission.MANAGE_SERVER));
    }

    @Override
    public void onCommand(MessageEvent context) {
        if(context.hasParameters()) {
            String[] params = context.getParameters().toLowerCase().split("\\s+", 2);

            if(!params[0].equals("*") && !Yuuko.MODULES.containsKey(params[0])) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(context.i18n( "invalid_input_title"))
                        .setDescription(context.i18n( "invalid_input_desc").formatted(params[0], context.getPrefix()));
                MessageDispatcher.reply(context, embed.build());
                return;
            }

            String module = params[0].equals("*") ? "*" : Yuuko.MODULES.get(params[0]).getName();
            List<TextChannel> channels = context.getMessage().getMentionedChannels();

            if(channels.size() != 0) {
                final int res = DatabaseInterface.toggleBind(context.getGuild().getId(), channels.get(0).getId(), module);
                if(res == 0) {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(context.i18n( "bind_channel").formatted(module, channels.get(0).getName()));
                    MessageDispatcher.reply(context, embed.build());
                } else if(res == 1) {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(context.i18n( "unbind_channel").formatted(module, channels.get(0).getName()));
                    MessageDispatcher.reply(context, embed.build());
                }
            } else {
                final int res = DatabaseInterface.toggleBind(context.getGuild().getId(), context.getChannel().getId(), module);
                if(res == 0) {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(context.i18n( "bind_channel").formatted(module, context.getChannel().getName()));
                    MessageDispatcher.reply(context, embed.build());
                } else if(res == 1) {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle(context.i18n( "unbind_channel").formatted(module, context.getChannel().getName()));
                    MessageDispatcher.reply(context, embed.build());
                }
            }
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(context.i18n( "bound"))
                .setDescription(DatabaseInterface.getGuildBinds(context.getGuild(), "\n"));
        MessageDispatcher.reply(context, embed.build());
    }

    public static class DatabaseInterface {
        /**
         * Binds a particular module to a channel.
         * @param guildId the idLong of the guild.
         * @param channel the idLong of the channel.
         * @param module the name of the module.
         * @return boolean
         */
        public static int toggleBind(String guildId, String channel, String module) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `guilds_module_binds` WHERE `guildId` = ? AND `channelId` = ? AND `moduleName` = ?");
                PreparedStatement stmt2 = conn.prepareStatement("INSERT INTO `guilds_module_binds`(`guildId`, `channelId`, `moduleName`) VALUES (?,?,?)")) {

                stmt.setString(1, guildId);
                stmt.setString(2, channel);
                stmt.setString(3, module);
                ResultSet resultSet = stmt.executeQuery();

                if(!resultSet.next()) {
                    stmt2.setString(1, guildId);
                    stmt2.setString(2, channel);
                    stmt2.setString(3, module);
                    if(!stmt2.execute()) {
                        return 0;
                    }
                }

                return (clearBind(guildId, channel, module)) ? 1 : -1;

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
                return -1;
            }
        }

        /**
         * Returns a formatted string of all of the selected guild's binds.
         * @param guild a guild object.
         * @param delimiter the delimiter used in the returned string.
         * @return String
         */
        public static String getGuildBinds(Guild guild, String delimiter) {
            try(Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `guilds_module_binds` WHERE `guildId` = ? ORDER BY `channelId`")) {

                stmt.setString(1, guild.getId());
                ResultSet rs = stmt.executeQuery();

                StringBuilder string = new StringBuilder();
                while(rs.next()) {
                    string.append(rs.getString(3)).append(" : ").append(guild.getTextChannelCache().getElementById(rs.getString(2)).getAsMention()).append(" (").append(guild.getTextChannelCache().getElementById(rs.getString(2)).getId()).append(")").append(delimiter);
                }

                if(string.length() > 0) {
                    TextUtilities.removeLast(string, delimiter);
                } else {
                    string.append("None");
                }

                return string.toString();

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
                return null;
            }
        }

        /**
         * Returns a formatted string of all of the guild's binds, which match a given module.
         * @param guild Guild
         * @param module String
         * @param delimiter String
         * @return String
         */
        public static String getBindsByModule(Guild guild, String module, String delimiter) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `guilds_module_binds` WHERE (`guildId` = ? AND `moduleName` = ?) OR (`guildId` = ? AND `moduleName` = '*')")) {

                stmt.setString(1, guild.getId());
                stmt.setString(2, module);
                stmt.setString(3, guild.getId());

                ResultSet rs = stmt.executeQuery();

                StringBuilder string = new StringBuilder();
                while(rs.next()) {
                    string.append(guild.getTextChannelCache().getElementById(rs.getString("channelId")).getAsMention()).append(delimiter);
                }

                if(string.length() > 0) {
                    TextUtilities.removeLast(string, delimiter);
                } else {
                    string.append("`None`");
                }

                return string.toString();

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
                return null;
            }
        }

        /**
         * Checks if a command's execution is to be blocked by a binding.
         * @param guildId String
         * @param channelId String
         * @param moduleName String
         * @return boolean returns false if there are no binds PREVENTING execution, else true.
         */
        public static boolean isBound(String guildId, String channelId, String moduleName) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `guilds_module_binds` WHERE (`guildId` = ? AND `moduleName` = ?) OR (`guildId` = ? AND `moduleName` = '*')")) {

                stmt.setString(1, guildId);
                stmt.setString(2, moduleName);
                stmt.setString(3, guildId);

                ResultSet rs = stmt.executeQuery();
                int count = 0;
                while(rs.next()) {
                    if(rs.getString("channelId").equals(channelId)) {
                        return false;
                    }
                    count++;
                }

                return count != 0;

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
                return false;
            }
        }

        /**
         * Verifies all binds, removing any that are no longer valid.
         * @param guild {@link Guild} object
         */
        public static void verifyBind(Guild guild) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `guilds_module_binds` WHERE `guildId` = ?")) {

                stmt.setString(1, guild.getId());
                ResultSet rs = stmt.executeQuery();

                while(rs.next()) {
                    String channelId = rs.getString("channelId");
                    if(guild.getTextChannelCache().getElementById(channelId) == null) {
                        clearBindByChannel(channelId);
                    }
                }

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
            }
        }

        /**
         * Recursively verifies all binds, removing any that are no longer valid.
         * @param guildCache {@link SnowflakeCacheView<Guild>} object
         */
        public static void verifyBinds(SnowflakeCacheView<Guild> guildCache) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `guilds_module_binds` WHERE `guildId` = ?")) {

                guildCache.forEach(guild -> {
                    try {
                        stmt.setString(1, guild.getId());
                        ResultSet rs = stmt.executeQuery();

                        while (rs.next()) {
                            String channelId = rs.getString("channelId");
                            if (guild.getTextChannelCache().getElementById(channelId) == null) {
                                clearBindByChannel(channelId);
                            }
                        }
                    } catch(Exception e) {
                        log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
                    }
                });

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
            }
        }

        /**
         * Clears a module bind from the given channel.
         * @param guild String
         * @param channel String
         * @param module String
         * @return boolean
         */
        private static boolean clearBind(String guild, String channel, String module) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM `guilds_module_binds` WHERE `guildId` = ? AND `channelId` = ? AND `moduleName` = ?")) {

                stmt.setString(1, guild);
                stmt.setString(2, channel);
                stmt.setString(3, module);

                if(!stmt.execute()) {
                    stmt.close();
                    conn.close();
                    return true;
                }

                return false;

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
                return false;
            }
        }

        /**
         * Clears all binds that are connected to a specific channel.
         * @param channelId channelId string.
         */
        private static void clearBindByChannel(String channelId) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM `guilds_module_binds` WHERE `channelId` = ?")) {

                stmt.setString(1, channelId);
                stmt.execute();

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
            }
        }

        /**
         * Removes all references of channels that are deleted.
         * @param channel String id of the channel referenced
         */
        public static void cleanupReferences(String channel) {
            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM `guilds_module_binds` WHERE `channelId` = ?");
                PreparedStatement stmt2 = conn.prepareStatement("UPDATE `guilds_settings` SET `starboard` = null WHERE 'starboard' = ?");
                PreparedStatement stmt3 = conn.prepareStatement("UPDATE `guilds_settings` SET `commandlog` = null WHERE 'commandlog' = ?");
                PreparedStatement stmt4 = conn.prepareStatement("UPDATE `guilds_settings` SET `moderationlog` = null WHERE 'moderationlog' = ?");
                PreparedStatement stmt5 = conn.prepareStatement("UPDATE `guilds_settings` SET `eventchannel` = null WHERE 'eventchannel' = ?")) {

                stmt.setString(1, channel);
                stmt.execute();
                stmt2.setString(1, channel);
                stmt2.execute();
                stmt3.setString(1, channel);
                stmt3.execute();
                stmt4.setString(1, channel);
                stmt4.execute();
                stmt5.setString(1, channel);
                stmt5.execute();

            } catch(Exception e) {
                log.error("An error occurred while running the {} class, message: {}", BindCommand.DatabaseInterface.class.getSimpleName(), e.getMessage(), e);
            }
        }
    }

}
