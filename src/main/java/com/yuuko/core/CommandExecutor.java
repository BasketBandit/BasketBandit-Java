package com.yuuko.core;

import com.yuuko.core.modules.Command;
import com.yuuko.core.modules.Module;
import com.yuuko.core.utils.MessageHandler;
import com.yuuko.core.utils.Sanitise;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandExecutor {

    public CommandExecutor(MessageReceivedEvent e, String[] command, Module module) {
        for(Command cmd : module.getModuleCommands()) {
            if(cmd.getCommandName().equalsIgnoreCase(command[0])) {
                if(cmd.getCommandPermission() != null) {
                    if(!e.getMember().hasPermission(cmd.getCommandPermission()) && !e.getMember().hasPermission(e.getTextChannel(), cmd.getCommandPermission())) {
                        MessageHandler.sendMessage(e, "Sorry " + e.getAuthor().getAsMention() + ", you lack the required permissions to use that command.");
                        return;
                    }
                }
                if(Sanitise.checkParameters(e, command, cmd.getExpectedParameters())) {
                    cmd.executeCommand(e, command);
                }
                return;
            }
        }
    }

}
