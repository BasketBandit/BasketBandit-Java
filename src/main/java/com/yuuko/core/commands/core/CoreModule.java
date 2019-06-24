package com.yuuko.core.commands.core;

import com.yuuko.core.CommandExecutor;
import com.yuuko.core.commands.Command;
import com.yuuko.core.commands.Module;
import com.yuuko.core.commands.core.commands.*;
import com.yuuko.core.events.entity.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class CoreModule extends Module {
	private static final List<Command> commands = Arrays.asList(
			new AboutCommand(),
			new SettingsCommand(),
			new ModuleCommand(),
			new HelpCommand(),
			new ShardsCommand(),
			new VoteCommand(),
			new CommandCommand(),
			new AdvertiseCommand()
	);

	public CoreModule(MessageEvent e) {
		super("core", false, commands);
		new CommandExecutor(e, this);
	}

}
