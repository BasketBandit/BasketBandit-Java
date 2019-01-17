package com.yuuko.core.commands.audio.commands;

import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.audio.AudioModule;
import com.yuuko.core.commands.audio.handlers.AudioManagerManager;
import com.yuuko.core.commands.audio.handlers.GuildAudioManager;
import com.yuuko.core.utilities.MessageHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static net.dv8tion.jda.core.audio.hooks.ConnectionStatus.NOT_CONNECTED;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", AudioModule.class, 0, new String[]{"-stop"}, null);
    }

    @Override
    public void executeCommand(MessageReceivedEvent e, String[] command) {
        if(e.getGuild().getAudioManager().getConnectionStatus() != NOT_CONNECTED) {
            GuildAudioManager manager = AudioManagerManager.getGuildAudioManager(e.getGuild().getId());

            manager.scheduler.queue.clear();
            manager.scheduler.setBackground(null);
            manager.player.stopTrack();
            manager.player.setPaused(false);
            e.getGuild().getAudioManager().setSendingHandler(null);
            e.getGuild().getAudioManager().closeAudioConnection();

            if(command != null) {
                EmbedBuilder embed = new EmbedBuilder().setTitle("Stopping").setDescription("Audio connection closed.");
                MessageHandler.sendMessage(e, embed.build());
            }
        }
    }

    /**
     * Executes command when everyone leaves the channel the bot is in.
     * @param e; GenericGuildEvent
     */
    public void executeCommand(GenericGuildEvent e) {
        if(e.getGuild().getAudioManager().getConnectionStatus() != NOT_CONNECTED) {
            GuildAudioManager manager = AudioManagerManager.getGuildAudioManager(e.getGuild().getId());

            manager.scheduler.queue.clear();
            manager.scheduler.setBackground(null);
            manager.player.stopTrack();
            manager.player.setPaused(false);
            e.getGuild().getAudioManager().setSendingHandler(null);
            e.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}