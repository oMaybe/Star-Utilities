package me.karam.listener;

import me.karam.slash.commands.DMCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        // TODO: add checks here
        String commandName = event.getName().toLowerCase();

        switch (commandName){
            case "dm":
                DMCommand.performSlashCommand(event);
                break;
        }
    }
}
