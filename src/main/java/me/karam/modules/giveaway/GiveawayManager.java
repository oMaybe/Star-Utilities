package me.karam.modules.giveaway;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

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
        embedBuilder.setAuthor(giveaway.getPrize());
        embedBuilder.addField("Giveaway Information", "b", false);
        embedBuilder.setColor(new Color(new Random().nextInt(250), new Random().nextInt(250), new Random().nextInt(250)));
        giveaway.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

}
