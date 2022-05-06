package me.karam.slash.commands.impl;

import me.karam.Main;
import me.karam.modules.modmail.Ticket;
import me.karam.modules.modmail.TicketType;
import me.karam.slash.commands.SlashCommand;
import me.karam.utils.Severity;
import me.karam.utils.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.lang.reflect.AnnotatedType;
import java.util.Date;
import java.util.UUID;

public class EmbedCommand implements SlashCommand {

    public void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

        if (event.getOptions().get(0).getAsString().equalsIgnoreCase("support")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(MarkdownUtil.bold("⭐ Star Galaxy Support ⭐"));
            builder.setFooter("Please read all the information above before creating a support ticket");

            builder.setDescription(MarkdownUtil.bold("\uD83D\uDD28 Appealing a Ban\n") +
                    "⁃ When appealing a ban, please be sure to follow all of the instructions in the appeal\n" +
                    "⁃ Failure to follow the instructions properly will result in your appeal getting denied\n" +
                    "⁃ You won't receive a response from our staff unless more information is required\n" +
                    "⁃ To know the outcome of your appeal, join the server and look for the following:\n" +
                    "‣ If your appeal was accepted, your ban will be either no longer be permanent or you will be unbanned\n" +
                    "‣ If your appeal was denied, you will not be unbanned\n" +
                    "⁃ Please wait two weeks before re-appealing if your appeal was denied");

            builder.addField(MarkdownUtil.bold("\uD83C\uDF88 Apply for Media Rank\n"),
                    "⁃ Must have 500+ subscribers on Youtube\n" +
                            "⁃ Must have 500+ followers on tiktok, twitter or twitch", true);

            builder.addField(MarkdownUtil.bold("\uD83D\uDC8E Claim Nitro Booster Perks\n"),
                    "• Please boost our server then create a ticket to claim your reward\n", true);

            builder.addField(MarkdownUtil.bold("⚠️ Reporting a User\n"),
                    "• You can report a Minecraft or Discord user here\n" +
                            "• Please have evidence of your report ready before you create the ticket", false);

            builder.addField(MarkdownUtil.bold("❓Other Questions or Concerns\n"),
                    "• If you need any kind of support that isn't covered above, use the Other Support button\n" +
                            "• Please be sure to explain what kind of support you need in detail", true);

            event.deferReply(true).queue();
            event.getTextChannel().sendMessageEmbeds(builder.build()).setActionRows(ActionRow.of(
                    Button.link("https://forms.gle/SFsrCpM5YqmQRN5i9", "\uD83D\uDD28 Appeal Ban"),
                    Button.primary("apply", "\uD83C\uDF88 Apply for Media"),
                    Button.primary("nitro", "\uD83D\uDC8E Claim Nitro Perks"),
                    Button.danger("report", "⚠️ Report User"),
                    Button.success("question", "❓ Other Questions or Concerns")
                    )).queue();
        }else{
            event.reply("I'm sorry but the only choices you have is 'support'.").setEphemeral(true).queue();
        }
    }

    public void onButton(ButtonInteractionEvent event){
        String buttonID = event.getComponentId();
        Member member = event.getMember();
        if (buttonID.equalsIgnoreCase("apply") || buttonID.equalsIgnoreCase("nitro") ||
                buttonID.equalsIgnoreCase("report") || buttonID.equalsIgnoreCase("question") || buttonID.equalsIgnoreCase("Ban Appeal")) {
            if (Main.getInstance().getTicketManager().hasOpenTicket(member.getUser().getId())) {
                event.reply("You already have another ticket open!").setEphemeral(true).queue();
                return;
            }

            EmbedBuilder media = new EmbedBuilder();
            media.setDescription("Your ticket has been created. A staff member will be with you shortly.");
            media.setTimestamp(new Date().toInstant());
            media.setColor(new Color(0, 150, 0));

            if (buttonID.equals("apply")) {
                event.reply("Starting media application in dms..").setEphemeral(true).queue();

                Utils.sendPrivateMessage(event.getUser(), media.build());

                Ticket ticket = new Ticket(UUID.randomUUID(), member.getUser().getId(), member, TicketType.Media);
                Main.getInstance().getTicketManager().add(ticket);
            } else if (buttonID.equalsIgnoreCase("nitro")) {
                event.reply("Starting nitro perks ticket in dms..").setEphemeral(true).queue();

                Utils.sendPrivateMessage(event.getUser(), media.build());

                Ticket ticket = new Ticket(UUID.randomUUID(), member.getUser().getId(), member, TicketType.Nitro_Perks);
                Main.getInstance().getTicketManager().add(ticket);
            } else if (buttonID.equalsIgnoreCase("report")) {
                event.reply("Starting report user ticket in dms..").setEphemeral(true).queue();

                Utils.sendPrivateMessage(event.getUser(), media.build());

                Ticket ticket = new Ticket(UUID.randomUUID(), member.getUser().getId(), member, TicketType.Report_User);
                Main.getInstance().getTicketManager().add(ticket);
            } else if (buttonID.equalsIgnoreCase("question")) {
                event.reply("Starting other questions tickets in dms..").setEphemeral(true).queue();

                Utils.sendPrivateMessage(event.getUser(), media.build());

                Ticket ticket = new Ticket(UUID.randomUUID(), member.getUser().getId(), member, TicketType.Other);
                Main.getInstance().getTicketManager().add(ticket);
            } else if (event.getButton().getLabel().equalsIgnoreCase("\uD83D\uDD28 Appeal Ban")) {
                event.reply("Launched ban appeal form.").setEphemeral(true).queue();

                //Utils.sendPrivateMessage(event.getUser(), media.build());

                Ticket ticket = new Ticket(UUID.randomUUID(), member.getUser().getId(), member, TicketType.Ban_Appeal);
                Main.getInstance().getTicketManager().add(ticket);
            }
        }
    }
}
