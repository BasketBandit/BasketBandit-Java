package com.yuuko.core.events.controllers;

import com.yuuko.core.database.DatabaseFunctions;
import com.yuuko.core.database.ModuleBindFunctions;
import net.dv8tion.jda.core.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;

public class GenericTextChannelController {

    public GenericTextChannelController(GenericTextChannelEvent e) {
        if(e instanceof TextChannelDeleteEvent) {
            textChannelDeleteEvent((TextChannelDeleteEvent)e);
        }
    }

    private void textChannelDeleteEvent(TextChannelDeleteEvent e) {
        ModuleBindFunctions.cleanupBinds(e.getChannel().getId());

        if(DatabaseFunctions.getGuildSetting("starboard", e.getGuild().getId()).equals(e.getChannel().getId())) {
            DatabaseFunctions.cleanupSettings(e.getGuild().getId());
        }
    }

}