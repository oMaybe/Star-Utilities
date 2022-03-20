package me.karam.listener;

import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onGenericMessage(@NotNull GenericMessageEvent event) {
        if (!event.isFromGuild()){

        }
    }
}
