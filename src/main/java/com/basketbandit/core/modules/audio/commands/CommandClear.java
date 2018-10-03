package com.basketbandit.core.modules.audio.commands;

import com.basketbandit.core.modules.Command;
import com.basketbandit.core.modules.audio.handlers.AudioManagerHandler;
import com.basketbandit.core.modules.audio.handlers.GuildAudioManager;
import com.basketbandit.core.utils.Utils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.LinkedList;
import java.util.Queue;

public class CommandClear extends Command {

    public CommandClear() {
        super("clear", "com.basketbandit.core.modules.audio.ModuleAudio", 0, new String[]{"-clear", "-clear 3"}, null);
    }

    @Override
    public void executeCommand(MessageReceivedEvent e, String[] command) {
        GuildAudioManager manager = AudioManagerHandler.getGuildAudioManager(e.getGuild().getId());

        try {
            if(command.length > 1) {
                int clearPos;

                try {
                    clearPos = Integer.parseInt(command[1]);
                } catch (Exception ex) {
                    return;
                }

                Queue<com.sedmelluq.discord.lavaplayer.track.AudioTrack> temp = new LinkedList<>();
                Queue<com.sedmelluq.discord.lavaplayer.track.AudioTrack> clone = new LinkedList<>();
                clone.addAll(manager.scheduler.queue);

                int i = 1;
                for(int x = 0; x < manager.scheduler.queue.size(); x++) {
                    if(i == clearPos) {
                        Utils.sendMessage(e, e.getAuthor().getAsMention() + " has removed **" + clone.remove().getInfo().title + "** from the queue.");
                        i++;
                    } else {
                        ((LinkedList<com.sedmelluq.discord.lavaplayer.track.AudioTrack>) temp).addLast(clone.remove());
                        i++;
                    }
                }
                manager.scheduler.queue.clear();
                manager.scheduler.queue.addAll(temp);

            } else {
                Utils.sendMessage(e, e.getAuthor().getAsMention() + " cleared the queue.");
                manager.scheduler.queue.clear();
            }
        } catch(Exception ex) {
            Utils.sendException(ex, e.getMessage().getContentRaw());
        }
    }

}
