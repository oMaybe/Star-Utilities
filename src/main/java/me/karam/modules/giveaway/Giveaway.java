package me.karam.modules.giveaway;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Giveaway {

    private UUID id;
    private String prize;
    private long timeStarted;
    private long expiry;
    private String requirements;
    private String hosterID;

    private int allowedWinners;
    private List<String> enteries;
    private List<String> winners;

    private TextChannel channel;

    public Giveaway(UUID id, String prize, long expiry, int allowedWinners, String hosterID) {
        this.id = id;
        this.prize = prize;
        this.expiry = expiry;
        this.allowedWinners = allowedWinners;
        this.hosterID = hosterID;
    }
}
