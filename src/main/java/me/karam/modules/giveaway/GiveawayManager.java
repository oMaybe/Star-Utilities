package me.karam.modules.giveaway;

import me.karam.Main;
import me.karam.config.Config;
import me.karam.modules.modmail.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

public class GiveawayManager {

    private ArrayList<Giveaway> openGiveaways;

    public GiveawayManager(){
        openGiveaways = new ArrayList<>();
        // TODO: add feature enable like hydra plugin?
    }

    public void add(Giveaway giveaway){
        openGiveaways.add(giveaway);
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

        embedBuilder.setFooter(giveaway.getEnteries().size() + " Entries");
        embedBuilder.setColor(new Color(new Random().nextInt(250), new Random().nextInt(250), new Random().nextInt(250)));
        //giveaway.getChannel().sendMessage("<a:rainbowfire:967998598849650758> **Giveaway!** <a:rainbowfire:967998598849650758>").queue();
        Message message = giveaway.getChannel().sendMessageEmbeds(embedBuilder.build())
                .addActionRow(Button.success("ga_enter", "Enter")).complete();
        giveaway.setId(message.getId());
        giveaway.setGiveawayMessage(message);
        giveaway.setEmbed(embedBuilder);
        giveaway.run();
    }

    public Giveaway getGiveawayByID(String id) {
        return openGiveaways.stream().filter(giveaway -> giveaway.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public int size(){
        return openGiveaways.size();
    }
}
