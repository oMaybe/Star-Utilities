package me.karam.slash.commands.impl;

import me.karam.slash.commands.SlashCommand;
import me.karam.utils.Settings;
import me.karam.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.awt.*;
import java.util.Date;

public class SettingsCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel) {
        if (!m.hasPermission(Permission.MANAGE_CHANNEL)) {
            event.replyEmbeds(Utils.createEmbed("You do not have enough permission!", new Color(135, 0, 14))).setEphemeral(true).queue();
            return;
        }

        String key = event.getOptions().get(0).getAsString();
        String value = event.getOptions().get(1).getAsString();

        if (key.equalsIgnoreCase("ticket_channel")){
            if (m.getGuild().getTextChannelById(value) == null){
                event.replyEmbeds(Utils.createEmbed("That text channel could not be found! Please select a valid text channel and enter the id.", new Color(135, 0, 14))).queue();
                return;
            }
            Settings.config.insert("ticket_channel", value);
            event.replyEmbeds(Utils.createEmbed("Successfully set the ticket logs to " + m.getGuild().getTextChannelById(value).getAsMention(), new Color(0, 80, 0), new Date())).queue();
        }else if (key.equalsIgnoreCase("guild_id")){
            Settings.config.insert("guild_id", value);
            event.replyEmbeds(Utils.createEmbed("Successfully set the guild id to " + value, new Color(0, 80, 0), new Date())).queue();
        }else{
            event.replyEmbeds(Utils.createEmbed("I could not find that. Refer to /help", new Color(135, 0, 14))).queue();
        }
    }
}
