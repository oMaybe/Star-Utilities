package me.karam.slash.commands.impl;

import me.karam.Main;
import me.karam.slash.commands.SlashCommand;
import me.karam.utils.Severity;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.concurrent.TimeUnit;

public class DMCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandEvent event, Member member, TextChannel channel) {
        String targetID = event.getOptions().get(0).getAsString();
        Main.getInstance().log(Severity.INFO, targetID);
        String message = event.getOptions().get(1).getAsString();

        if (!member.hasPermission(Permission.MESSAGE_MANAGE)){
            event.reply("You cannot perform this command!").setEphemeral(true).queue();
            return;
        }

        if (Main.jda.getGuildById("954271232067530782").getMemberById(targetID) == null){
            event.reply("You cannot mention a role or a non existent member!").setEphemeral(true).queue();
            return;
        }

        Member targetMember = Main.jda.getGuildById("954271232067530782").getMemberById(targetID);

        if (message == null || message.length() == 0){
            event.reply("You cannot send an empty message!").setEphemeral(true).queue();
            return;
        }

        //event.reply("Successfully sent the message ")
        sendPrivateMessage(targetMember.getUser(), message);
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
