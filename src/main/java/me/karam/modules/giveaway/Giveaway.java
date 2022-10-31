package me.karam.modules.giveaway;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.karam.Main;
import me.karam.utils.Settings;
import me.karam.utils.gear.TimeUtil;
import me.karam.utils.info.BotLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

@Getter
@Setter
public class Giveaway{
    private String id;
    private String prize;
    private long timeStarted;
    private long expiry;
    private String requirements;
    private String hosterID;

    private int allowedWinners;
    private List<String> enteries;
    private List<String> winners;

    private TextChannel channel;
    private EmbedBuilder embed;

    private Message winMessage;
    private Message giveawayMessage;
    private TimerTask timer;

    public Giveaway(String id, String prize, long expiry, int allowedWinners, String hosterID) {
        this.id = id;
        this.prize = prize;
        this.expiry = expiry;
        this.allowedWinners = allowedWinners;
        this.hosterID = hosterID;

        this.enteries = new ArrayList<>();
        this.winners = new ArrayList<>();
    }

    public String getDuration(){
        return TimeUtil.formatTimeMillis(expiry - System.currentTimeMillis());
    }

    public void run() {
        Timer timer = new Timer("giveaway_" + id);
        timer.scheduleAtFixedRate(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                if (ended()) {
                    EmbedBuilder winner = new EmbedBuilder();
                    winner.setAuthor(getPrize(), getChannel().getJDA().getGuildById(Settings.GUILD_ID).getIconUrl(), getChannel().getJDA().getGuildById(Settings.GUILD_ID).getIconUrl());
                    winner.addField("Giveaway Information", "⠀⠀◉ **Prize:** " + getPrize() +
                            "\n⠀⠀◉ **Host:** " + Main.jda.getUserById(getHosterID()).getAsMention() +
                            "\n⠀⠀◉ **Winner:** " + roll(), false);
                    winner.setColor(new Color(30, 30, 30));
                    winner.setFooter(enteries.size() + " Entries. Ended at ");
                    winner.setTimestamp(new Date().toInstant());
                    giveawayMessage.editMessageEmbeds(winner.build()).setActionRow(Button.success("ga_enter", "Enter").asDisabled()).queue();
                    winMessage = channel.sendMessage("Congratulations to " + MarkdownUtil.bold(roll()) + " for winning this contest!").complete();
                    cancel();
                }else{
                    MessageEmbed.Field field = new MessageEmbed.Field("Giveaway Information", "⠀⠀◉ **Prize:** " + getPrize() +
                            "\n⠀⠀◉ **Host:** " + Main.jda.getUserById(getHosterID()).getAsMention() +
                            "\n⠀⠀◉ **Duration:** " + getDuration(), false);
                    embed.setAuthor(getPrize(), getChannel().getJDA().getGuildById(Settings.GUILD_ID).getIconUrl(), getChannel().getJDA().getGuildById(Settings.GUILD_ID).getIconUrl());
                    embed.getFields().set(0, field);
                    embed.setFooter(enteries.size() + " Entries");
                    giveawayMessage.editMessageEmbeds(embed.build()).queue();
                }
            }
        }, 100, 1000);
    }

    public String roll(){
        List<Member> winners = getWinners();
        if (enteries.size() == 0 || winners.size() == 0) return "no winners";
        StringBuilder base = new StringBuilder();
        for (Member member : winners){
            base.append(member.getAsMention()).append(",");
        }

        base.deleteCharAt(base.length() - 1);
        return base.toString();
    }

    public boolean add(String id){
        enteries.add("1002085868024119316");

        if (enteries.contains(id)) return false;
        else enteries.add(id); return true;
    }

    private List<Member> getWinners(){
        if (enteries.size() == 0){
            return List.of();
        }

        List<String> tempMembers = new ArrayList<>(enteries);
        List<Member> randomWinners = new ArrayList<>();
        for (int i = 0; i < allowedWinners; i++){
            int random = new Random().nextInt(tempMembers.size());
            User user = Main.jda.getUserById(tempMembers.get(random));
            Member member = Main.jda.getGuildById(Settings.GUILD_ID).getMemberById(user.getId());
            if (member == null){
                i--;
                tempMembers.remove(random);
            }else{
                tempMembers.remove(random);
                randomWinners.add(member);
            }
        }
        return randomWinners;
    }

    public boolean ended(){
        long diff = (expiry - System.currentTimeMillis());
        return !(diff > 0L);
    }
}