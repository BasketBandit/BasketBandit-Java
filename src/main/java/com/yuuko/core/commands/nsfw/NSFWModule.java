package com.yuuko.core.commands.nsfw;

import com.yuuko.core.CommandExecutor;
import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.Module;
import com.yuuko.core.commands.nsfw.commands.EfuktCommand;
import com.yuuko.core.commands.nsfw.commands.NekoCommand;
import com.yuuko.core.commands.nsfw.commands.UrbanDictionaryCommand;
import com.yuuko.core.events.extensions.MessageEvent;

public class NSFWModule extends Module {
    private static final Command[] commands = new Command[]{
            new EfuktCommand(),
            new NekoCommand(),
            new UrbanDictionaryCommand()
    };

    public NSFWModule(MessageEvent e) {
        super("nsfw", true, commands);
        new CommandExecutor(e, this);
    }

}
