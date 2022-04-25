package me.karam.modules.giveaway;

import me.karam.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;

public class GiveawayManager {

    private HashMap<UUID, Giveaway> openGiveaways;
    private HashMap<UUID, Giveaway> closedGiveaways;

    public GiveawayManager(){
        openGiveaways = new HashMap<>();
        closedGiveaways = new HashMap<>();
    }

    public void add(Giveaway giveaway){
        openGiveaways.put(giveaway.getId(), giveaway);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        //https://cdn.discordapp.com/emojis/826665413009735710.webp?size=96&quality=lossless
        embedBuilder.setAuthor(giveaway.getPrize(), null, giveaway.getChannel().getJDA().getSelfUser().getAvatarUrl());
        embedBuilder.addField("Giveaway Information", "⠀⠀◉ **Prize:** " + giveaway.getPrize() +
                "\n⠀⠀◉ **Host:** " + Main.jda.getUserById(giveaway.getHosterID()).getAsMention() +
                "\n⠀⠀◉ **Duration:** " + giveaway.getDuration(), false);

        if (giveaway.getRequirements() != null){
            embedBuilder.addField("Requirements", "⠀⠀◉ " + giveaway.getRequirements(), false);
        }

        embedBuilder.addField("Additional Info", "⠀⠀◉ **Allowed Winners**: " + giveaway.getAllowedWinners(), false);

        embedBuilder.setFooter("0 Entries");
        embedBuilder.setColor(new Color(new Random().nextInt(250), new Random().nextInt(250), new Random().nextInt(250)));
        giveaway.getChannel().sendMessage("<a:rainbowfire:967998598849650758> **Giveaway!** <a:rainbowfire:967998598849650758>").queue();
        giveaway.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRows(ActionRow.of(Button.success("ga_enter", "Enter"))).queue();
        giveaway.run();
    }

}
