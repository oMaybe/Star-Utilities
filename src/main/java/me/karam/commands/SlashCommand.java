package me.karam.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface SlashCommand {

    void performCommand(SlashCommandInteractionEvent event, Member m, TextChannel channel);
    //void performSelectMenu(ButtonInteractionEvent event);
}
