package me.karam.slash.commands.impl;

import me.karam.Main;
import me.karam.slash.commands.SlashCommand;
import me.karam.utils.BotLogger;
import me.karam.utils.Settings;
import me.karam.utils.Severity;
import me.karam.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.concurrent.TimeUnit;

public class DMCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandInteractionEvent event, Member member, TextChannel channel) {
        String targetID = event.getOptions().get(0).getAsString();
        //BotLogger.log(Severity.INFO, targetID);
        String message = event.getOptions().get(1).getAsString();

        if (!member.hasPermission(Permission.MESSAGE_MANAGE)){
            event.reply("You cannot perform this command!").setEphemeral(true).queue();
            return;
        }

        if (Main.jda.getGuildById(Settings.GUILD_ID).getMemberById(targetID) == null){
            event.reply("You cannot mention a role or a non existent member!").setEphemeral(true).queue();
            return;
        }

        Member targetMember = Main.jda.getGuildById(Settings.GUILD_ID).getMemberById(targetID);

        if (message == null || message.length() == 0){
            event.reply("You cannot send an empty message!").setEphemeral(true).queue();
            return;
        }

        event.reply("Successfully sent the message");
        Utils.sendPrivateMessage(targetMember.getUser(), message);
    }

}
