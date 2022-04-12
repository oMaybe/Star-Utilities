package me.karam.modules.modmail;

import lombok.Getter;
import lombok.Setter;
import me.karam.Main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class Ticket {

    private UUID ticketID;
    private String id;
    private Member consumer;
    private TicketType type;
    private List<Message> embedsSent;
    private String responder;

    private boolean responded;
    private Message m;

    public Ticket(UUID ticketID, String id, Member consumer, TicketType type) {
        this.ticketID = ticketID;
        this.id = id;
        this.consumer = consumer;
        this.type = type;
    }

    public static boolean isClosed(Ticket ticket){
        return Main.getInstance().getTicketManager().isClosedTicket(ticket);
    }
}
