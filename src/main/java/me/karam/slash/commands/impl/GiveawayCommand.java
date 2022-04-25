package me.karam.slash.commands.impl;

import me.karam.Main;
import me.karam.modules.giveaway.Giveaway;
import me.karam.slash.commands.SlashCommand;
import me.karam.utils.BotLogger;
import me.karam.utils.TimeUtil;
import me.karam.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.awt.*;
import java.util.AbstractMap;
import java.util.UUID;

public class GiveawayCommand implements SlashCommand {
    @Override
    public void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel) {
        if (!m.hasPermission(Permission.ADMINISTRATOR)) {
            event.replyEmbeds(Utils.createEmbed("You do not have enough permission!", new Color(135, 0, 14))).queue();
            return;
        }

        String subCommand = event.getOptions().get(0).getAsString();
        switch (subCommand){
            case "create":
                BotLogger.log(event.getOptions().get(2).getAsString());

                String prize = event.getOptions().get(1).getAsString();
                long startedAt = System.currentTimeMillis();
                long expiry = TimeUtil.parseTime(event.getOptions().get(2).getAsString());
                int winnerAmount = event.getOptions().get(3).getAsInt();

                Giveaway giveaway = new Giveaway(UUID.randomUUID(), prize, expiry, winnerAmount, m.getUser().getId());
                giveaway.setChannel(channel);
                giveaway.setTimeStarted(startedAt);
                giveaway.setHosterID(event.getUser().getId());
                giveaway.setExpiry(expiry);
                giveaway.setTimeStarted(startedAt);
                giveaway.setAllowedWinners(winnerAmount);
                BotLogger.log(event.getOptions());
                if (event.getOptions().size() >= 5){
                    giveaway.setRequirements(event.getOptions().get(5).getAsString());
                }

                Main.getInstance().getGiveawayManager().add(giveaway);
                event.reply("okay").setEphemeral(true).queue();
                break;
        }
    }

    public void onButton(ButtonInteractionEvent event){

    }
}
