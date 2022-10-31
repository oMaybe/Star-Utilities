package me.karam.commands.impl;

import me.karam.Main;
import me.karam.commands.SlashCommand;
import me.karam.modules.giveaway.Giveaway;
import me.karam.profile.Profile;
import me.karam.utils.events.ButtonsEvent;
import me.karam.utils.events.SelectionMenu;
import me.karam.utils.gear.TimeUtil;
import me.karam.utils.gear.Utils;
import me.karam.utils.info.BotLogger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;

public class GiveawayCommand implements SlashCommand {

    @Override
    public void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel) {
        if (!event.getName().equalsIgnoreCase("giveaway")) return;
        String subCommand = event.getSubcommandName().toLowerCase();

        switch (subCommand){
            case "create":
                if (Main.getInstance().getGiveawayManager().size() > 20){
                    event.replyEmbeds(Utils.createEmbed("Sorry! To prevent a flood in the amount of giveaways there are, you cannot create more than 20 giveaways simultaneously. If you believe this is an error, contact the developer.", new Color(135, 0, 14))).setEphemeral(true).queue();
                    return;
                }

                String prize = event.getOptions().get(0).getAsString();
                long startedAt = System.currentTimeMillis();
                long expiry = System.currentTimeMillis() + TimeUtil.parseTime(event.getOptions().get(1).getAsString());
                int winnerAmount = event.getOptions().get(2).getAsInt();

                Giveaway giveaway = new Giveaway("", prize, expiry, winnerAmount, m.getUser().getId());
                giveaway.setChannel(channel);
                giveaway.setTimeStarted(startedAt);
                giveaway.setHosterID(event.getUser().getId());
                giveaway.setExpiry(expiry);
                giveaway.setTimeStarted(startedAt);
                giveaway.setAllowedWinners(winnerAmount);
                //BotLogger.info(event.getOptions());
                if (event.getOptions().size() >= 4){
                    giveaway.setRequirements(event.getOptions().get(3).getAsString());
                }

                Main.getInstance().getGiveawayManager().add(giveaway);
                event.reply("Created a giveaway!").setEphemeral(true).queue();
                break;
        }
    }

    @ButtonsEvent
    public void onButton(ButtonInteractionEvent event){
        String buttonID = event.getComponentId();
        Member member = event.getMember();
        if (event.getMessage().getEmbeds().get(0) == null){
            return;
        }

        if (buttonID.equalsIgnoreCase("ga_enter")){
            Giveaway giveaway = Main.getInstance().getGiveawayManager().getGiveawayByID(event.getMessage().getId());
            if (giveaway == null || giveaway.ended()){
                event.replyEmbeds(Utils.NULL_EMBED).setEphemeral(true).queue();
                return;
            }

            if (giveaway.getEnteries().contains(member.getId())){
                event.replyEmbeds(Utils.createEmbed("You are already in that giveaway!", new Color(135, 0, 14))).setEphemeral(true).queue();
                return;
            }

            giveaway.add(member.getId());
            event.replyEmbeds(Utils.createEmbed("You are now entered to that giveaway!", new Color(0, 205, 150))).setEphemeral(true).queue();
        }
    }

    @SelectionMenu
    public void onSelect(SelectMenuInteractionEvent selectionMenu){
        String id = selectionMenu.getComponentId();
        String value = selectionMenu.getValues().get(0);
        if (!id.equalsIgnoreCase("edit_giveaway")) return;
        if (value.equalsIgnoreCase("g_reroll")){
            Profile profile = Main.getInstance().getProfileManager().getByID(selectionMenu.getMember().getId());
            Giveaway giveaway = Main.getInstance().getGiveawayManager().getGiveawayByID(profile.getEditing());

            if (giveaway == null){
                selectionMenu.replyEmbeds(Utils.NULL_EMBED).setEphemeral(true).queue();
                return;
            }

            if (!giveaway.ended()){
                selectionMenu.replyEmbeds(Utils.createEmbed("The giveaway hasn't ended yet. You cannot reroll while there is nothing to reroll.", new Color(100, 0, 0))).setEphemeral(true).queue();
                return;
            }

            giveaway.getWinMessage().editMessage(MarkdownUtil.bold("🎈 REROLL! Congratulations to " + giveaway.roll() + " for winning the contest!")).queue();
            giveaway.getGiveawayMessage().reply("Re-rolling a new winner!").queue();
        }else {
            Profile profile = Main.getInstance().getProfileManager().getByID(selectionMenu.getMember().getId());
            Giveaway giveaway = Main.getInstance().getGiveawayManager().getGiveawayByID(profile.getEditing());
            Modal.Builder modal = Modal.create("give_modal", "🎉 Giveaway Edit");
            if (giveaway == null || giveaway.ended()){
                selectionMenu.replyEmbeds(Utils.NULL_EMBED).setEphemeral(true).queue();
                return;
            }


            TextInput.Builder input = null;
            switch (selectionMenu.getValues().get(0)) {
                case "g_time" -> {
                    input = TextInput.create("g_edit_v", "Time of Giveaway", TextInputStyle.SHORT);
                    input.setRequired(true);
                    input.setMinLength(2);
                    input.setMaxLength(50);
                    input.setPlaceholder("Enter the duration of the giveaway.");

                    modal.setId("give_modal_time");
                }
            }

            modal.addActionRow(input.build());
            selectionMenu.replyModal(modal.build()).queue();
        }
    }

    public void onModal(ModalInteractionEvent event){
        Profile profile = Main.getInstance().getProfileManager().getByID(event.getMember().getId());
        if (profile.getEditing() != null) {
            switch (event.getModalId()) {
                case "give_modal_time" -> {
                    long time = TimeUtil.parseTime(event.getValues().get(0).getAsString());
                    Giveaway giveaway = Main.getInstance().getGiveawayManager().getGiveawayByID(profile.getEditing());
                    giveaway.setExpiry(System.currentTimeMillis() + time);

                    event.replyEmbeds(Utils.createEmbed("Successfully changed that giveaway's time to " + TimeUtil.formatTimeMillis(time), new Color(20, 100, 0))).setEphemeral(true).queue();
                }
            }
        }
    }
}
