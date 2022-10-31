package me.karam.modules.modmail;

import me.karam.Main;
import me.karam.config.Config;
import me.karam.utils.*;
import me.karam.utils.config.MessageObject;
import me.karam.utils.info.BotLogger;
import me.karam.utils.info.Severity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TicketManager {

    private Config config;
    private HashMap<String, Ticket> openTickets;
    private final HashMap<String, Ticket> closedTickets;
    public TicketManager(){
        this.config = new Config(new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "tickets.json"));
        openTickets = new HashMap<>();
        closedTickets = new HashMap<>();
        //loadExistingTickets();
    }
    public void loadExistingTickets(){
        //BotLogger.log(config.getRawData("openedTickets"));
        //BotLogger.log(config.getRawData("closedTickets"));
    }
    public void saveTickets(){
        //HashMap

        // Bucket
        //config.insert("openedTickets", SavableObject.toSavableTickets(openTickets));
        //config.insert("closedTickets", closedTickets);
        //config.save();
    }

    public void add(Ticket ticket){
        openTickets.put(ticket.getTicketID(), ticket);
        if (Settings.TICKET_LOG_CHANNEL == null){
            BotLogger.log(Severity.HIGH, "Failed to create ticket because ticket channel doesn't exist!");
            return;
        }
        /*TextChannel channel = ticket.getConsumer().getJDA().getGuildById(Settings.GUILD_ID).getTextChannelById(Settings.TICKET_LOG_CHANNEL);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(ticket.getConsumer().getUser().getAsTag(), null, ticket.getConsumer().getEffectiveAvatarUrl());
        builder.setTitle("New Ticket"); //
        //builder.setThumbnail(ticket.getConsumer().getEffectiveAvatarUrl());
        builder.addField(MarkdownUtil.bold("Ticket Creation."), ticket.getConsumer().getAsMention() + " created a " + ticket.getType().name().replace("_", " ") + " ticket.", false);
        builder.setColor(new Color(0, 40, 200));
        builder.setFooter("Ticket ID: " + ticket.getTicketID());
        builder.setTimestamp(new Date().toInstant());*/
    }

    public Ticket getTicket(User user){
        return openTickets.values().stream().filter(ticket -> ticket.getConsumer().getUser().getId().equals(user.getId())).findFirst().orElse(null);
    }

    public Ticket getTicketByID(String ticketID){
        return openTickets.values().stream().filter(ticket -> ticket.getTicketID().equals(ticketID)).findFirst().orElse(null);
    }

    public boolean hasOpenTicket(String id){
        return openTickets.values().stream().filter(ticket -> ticket.getId().equals(id)).findAny().isPresent();
    }

    public boolean isClosedTicket(Ticket ticket){
        return closedTickets.containsKey(ticket.getTicketID());
    }

    public void closeTicket(Ticket ticket) {
        openTickets.remove(ticket.getTicketID());
        closedTickets.put(ticket.getTicketID(), ticket);

        //TODO change this
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Ticket Closure", null, ticket.getConsumer().getUser().getAvatarUrl());
        embed.addField(new MessageEmbed.Field("Ticket Consumer", ticket.getConsumer().getAsMention(), true));
        if (ticket.getResponder() == null){
            embed.addField(new MessageEmbed.Field("Ticket Claimer", "null", true));
        }else{
            embed.addField(new MessageEmbed.Field("Ticket Claimer", Main.jda.getGuildById(Settings.GUILD_ID).getMemberById(ticket.getResponder()).getAsMention(), true));
        }

        embed.addField(new MessageEmbed.Field("Closed by", ticket.getCloser().getAsMention(), true));
        embed.addField(new MessageEmbed.Field("Ticket Type", ticket.getType().toString().toLowerCase(), true));
        embed.addField(new MessageEmbed.Field("Ticket ID", ticket.getTicketID(), true));
        embed.addField(new MessageEmbed.Field("Closing reason", ticket.getCloseReason().length() > 1 ? ticket.getCloseReason() : "no reason", true));
        embed.setFooter("Task Completed");
        embed.setTimestamp(new Date().toInstant());
        embed.setColor(new Color(125, 125, 255));
        // TODO:transcript

        //MessageHistory history = MessageHistory.getHistoryFromBeginning(ticket.getChannel()).complete();
        //List<Message> messageList = history.getRetrievedHistory();

        File file = new File(System.getProperty("user.dir") + "/transcripts/" + ticket.getTicketID() + ".txt");
        try {
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write("# Transcript for " + ticket.getConsumer().getUser().getAsTag() + " (" + ticket.getConsumer().getUser().getId() + ")" + "\n");
            writer.write("\n");
            writer.write("# Ticket ID: " + ticket.getTicketID() + "\n");
            writer.write("# Ticket Closed By: " +  ticket.getCloser().getUser().getAsTag()  + " (" + ticket.getCloser().getUser().getId() + ")" + "\n");
            writer.write("# Ticket Claimed By: " + (ticket.getResponder() != null ? Main.jda.getUserById(ticket.getResponder()).getAsTag()  + " (" + Main.jda.getUserById(ticket.getResponder()).getId() + ")" : "null") + "\n");
            writer.write("\n");
            writer.write("# Ticket Close Reason: " + (ticket.getCloseReason().length() > 1 ? "(no reason)" : ticket.getCloseReason()) + "\n");
            writer.write("# Ticket Type: " + ticket.getType() + "\n");
            writer.write("\n");

            if (1+1==2) {
                Message message = ticket.getChannel().retrieveMessageById(ticket.getFirstMessageID()).complete();
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = dt.format(Date.from(message.getTimeCreated().toInstant()));

                writer.write("# Ticket created at " +  formattedDate + "\n");
                writer.write("\n");
            }

            for (MessageObject messageObject : ticket.getContents()){
                Instant datedObject = messageObject.getMessage().getTimeCreated().toInstant();

                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dt.format(Date.from(datedObject));

                SimpleDateFormat tIme = new SimpleDateFormat("HH:mm:ss");
                String formattedTime = tIme.format(Date.from(datedObject));

                writer.append(messageObject.getMessage().getMember().getUser().getAsTag() + " on " + formattedDate + " at " + formattedTime + ": " + messageObject.getMessage().getContentRaw() + "\n");
            }

            Message message = ticket.getChannel().getHistory()
                    .retrievePast(2)
                    .map(messages -> messages.get(0)).complete();
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dt.format(Date.from(message.getTimeCreated().toInstant()));

            writer.write("# Ticket closed at " +  formattedDate + "\n");
            writer.write("\n");

            writer.flush();
            writer.close();

            ticket.setTranscript(file);

            Main.jda.getGuildById(Settings.GUILD_ID)
                    .getTextChannelById(Settings.TICKET_LOG_CHANNEL)
                    .sendMessageEmbeds(embed.build())
                    .setComponents(ActionRow.of(Button.primary("transcript", "📜 Transcript"))).queueAfter(3, TimeUnit.SECONDS);

            ticket.setTranscript(file);
            Cooldown.put(ticket.getConsumer().getId(), new Date());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

