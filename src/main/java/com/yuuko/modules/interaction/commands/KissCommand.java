package com.yuuko.modules.interaction.commands;

import com.yuuko.MessageDispatcher;
import com.yuuko.events.entity.MessageEvent;
import com.yuuko.modules.Command;
import com.yuuko.utilities.MessageUtilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.List;

public class KissCommand extends Command {
    private static final List<String> interactionImage = Arrays.asList(
            "https://i.imgur.com/sGVgr74.gif",
            "https://i.imgur.com/TItLfqh.gif",
            "https://i.imgur.com/YbNv10F.gif",
            "https://i.imgur.com/wQjUdnZ.gif",
            "https://i.imgur.com/lmY5soG.gif"
    );

    public KissCommand() {
        super("kiss", Arrays.asList("-kiss @user"), 1);
    }

    @Override
    public void onCommand(MessageEvent context) throws Exception {
        Member target = MessageUtilities.getMentionedMember(context, true);
        if(target != null) {
            EmbedBuilder embed = new EmbedBuilder().setDescription(context.i18n( "target").formatted(context.getMember().getEffectiveName(), target.getEffectiveName())).setImage(interactionImage.get(getRandom(interactionImage.size())));
            MessageDispatcher.sendMessage(context, embed.build());
        }
    }
}
