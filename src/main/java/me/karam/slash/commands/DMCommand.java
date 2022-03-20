package me.karam.slash.commands;

import me.karam.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.concurrent.TimeUnit;

public class DMCommand {

    public static void performSlashCommand(SlashCommandEvent event) {
        Member member = event.getMember();
        Member target = event.getOptions().get(0).getAsMember();
        String message = event.getOptions().get(1).getAsString();

        if (!member.hasPermission(Permission.MESSAGE_MANAGE)){
            event.reply("You cannot perform this command!").setEphemeral(true).queue();
            return;
        }

        if (target == null){
            event.reply("You cannot mention a role or a non existent member!").setEphemeral(true).queue();
            return;
        }

        if (message == null || message.length() == 0){
            event.reply("You cannot send an empty message!").setEphemeral(true).queue();
            return;
        }

        //event.reply("Successfully sent the message ")
        sendPrivateMessage(member.getUser(), message);
    }

    private static void sendPrivateMessage(User user, String content) {
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessage(content).queue();
        });
    }

    private static void sendPrivateMessage(User user, MessageEmbed content) {
        user.openPrivateChannel().queue((channel) ->
        {
            channel.sendMessageEmbeds(content).queue();
        });
    }
}
