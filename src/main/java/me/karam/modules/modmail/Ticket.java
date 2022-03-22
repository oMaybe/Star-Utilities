package me.karam.modules.modmail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@RequiredArgsConstructor
public class Ticket {

    private UUID id;
    private SupportType type;
}
