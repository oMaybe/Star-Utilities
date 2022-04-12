package me.karam.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.Date;

public class Utils {

    public static void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(content).queue();
        });
    }

    public static void sendPrivateMessage(User user, MessageEmbed content) {
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessageEmbeds(content).queue();
        });
    }

    public static MessageEmbed createEmbed(String description, Color color){
        return new EmbedBuilder().setDescription(description).setColor(color).build();
    }

    public static MessageEmbed createEmbed(String description, Color color, Date date){
        return new EmbedBuilder().setDescription(description).setColor(color).setFooter("Task Completed").setTimestamp(date.toInstant()).build();
    }
}
