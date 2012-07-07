package net.wiskr.ScheduledAnnouncer;

import org.bukkit.ChatColor;

public class ChatColorHelper
{

    public ChatColorHelper()
    {
    }

    public static String replaceColorCodes(String message)
    {
        ChatColor arr$[] = ChatColor.values();
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; i$++)
        {
            ChatColor color = arr$[i$];
            message = message.replaceAll(String.format("&%c", new Object[] {
                Character.valueOf(color.getChar())
            }), color.toString());
        }

        return message;
    }
}
