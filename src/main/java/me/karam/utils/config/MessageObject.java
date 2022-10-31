package me.karam.utils.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class MessageObject {

    private final Member member;
    private final Date time;
    private final Message message;
    public static String format(ArrayList<MessageObject> messageObjects) {
        return null;
    }
}
