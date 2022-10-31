package me.karam.modules.modmail;

public enum TicketType {

    GENERAL,
    USER_REPORT,
    GIVEAWAYS,
    PARTNERSHIPS,
    APPEAL,
    ADMINISTRATOR,
    NULL;
    public static String from(String basic){
        return basic.equalsIgnoreCase("question") ? "General Question" :
                basic.equalsIgnoreCase("user_report") ? "User/Staff Report" :
                basic.equalsIgnoreCase("giveaways") ? "Giveaways" :
                basic.equalsIgnoreCase("partner") ? "Partnership" :
                basic.equalsIgnoreCase("appeal") ? "Punishment Appeal" :
                basic.equalsIgnoreCase("admin") ? "Admin Inquiry" :
                        "null";
    }

    public static TicketType to(String basic){
        return basic.equalsIgnoreCase("question") ? TicketType.GENERAL :
                basic.equalsIgnoreCase("user_report") ? TicketType.USER_REPORT :
                basic.equalsIgnoreCase("giveaways") ? TicketType.GIVEAWAYS :
                basic.equalsIgnoreCase("partner") ? TicketType.PARTNERSHIPS :
                basic.equalsIgnoreCase("appeal") ? TicketType.APPEAL :
                basic.equalsIgnoreCase("admin") ? TicketType.ADMINISTRATOR :
                      TicketType.NULL;
    }
}
