// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AnnouncerCommandExecutor.java

package net.wiskr.ScheduledAnnouncer;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.PluginDescriptionFile;

// Referenced classes of package net.wiskr.ScheduledAnnouncer:
//            AnnouncerPlugin, ChatColorHelper

class AnnouncerCommandExecutor
    implements CommandExecutor
{

    AnnouncerCommandExecutor(AnnouncerPlugin plugin)
    {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String args[])
    {
        boolean success;
        if(args.length == 0 || args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("info"))
            success = onVersionCommand(sender, command, label, args);
        else
        if("help".equalsIgnoreCase(args[0]))
            success = onHelpCommand(sender, command, label, args);
        else
        if("add".equalsIgnoreCase(args[0]))
            success = onAddCommand(sender, command, label, args);
        else
        if("broadcast".equalsIgnoreCase(args[0]) || "now".equalsIgnoreCase(args[0]))
            success = onBroadcastCommand(sender, command, label, args);
        else
        if("list".equalsIgnoreCase(args[0]))
            success = onListCommand(sender, command, label, args);
        else
        if("delete".equalsIgnoreCase(args[0]))
            success = onDeleteCommand(sender, command, label, args);
        else
        if("interval".equalsIgnoreCase(args[0]))
            success = onIntervalCommand(sender, command, label, args);
        else
        if("prefix".equalsIgnoreCase(args[0]))
            success = onPrefixCommand(sender, command, label, args);
        else
        if("random".equalsIgnoreCase(args[0]))
            success = onRandomCommand(sender, command, label, args);
        else
        if("enable".equalsIgnoreCase(args[0]))
            success = onEnableCommand(sender, command, label, args);
        else
        if("reload".equalsIgnoreCase(args[0]))
            success = onReloadCommand(sender, command, label, args);
        else
            success = false;
        if(!success)
            sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Invalid arguments! ").append("Use '/announce help' to get a list of valid commands.").toString());
        return true;
    }

    boolean onVersionCommand(CommandSender sender, Command command, String label, String args[])
    {
        sender.sendMessage(String.format("%s === %s [Version %s] === ", new Object[] {
            ChatColor.LIGHT_PURPLE, plugin.getDescription().getName(), plugin.getDescription().getVersion()
        }));
        sender.sendMessage(String.format("Author: %s", new Object[] {
            plugin.getDescription().getAuthors().get(0)
        }));
        sender.sendMessage(String.format("Website: %s", new Object[] {
            plugin.getDescription().getWebsite()
        }));
        sender.sendMessage(String.format("Version: %s", new Object[] {
            plugin.getDescription().getVersion()
        }));
        sender.sendMessage("Features:");
        sender.sendMessage("- InGame Configuration");
        sender.sendMessage("- Permissions Support");
        sender.sendMessage("");
        sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("Use '/announce help' to get a list of valid commands.").toString());
        return true;
    }

    boolean onHelpCommand(CommandSender sender, Command command, String label, String args[])
    {
        sender.sendMessage(String.format("%s === %s [Version %s] === ", new Object[] {
            ChatColor.LIGHT_PURPLE, plugin.getDescription().getName(), plugin.getDescription().getVersion()
        }));
        if(sender.hasPermission("announcer.add"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce add <message>").append(ChatColor.WHITE).append(" - Adds a new announcement").toString());
        if(sender.hasPermission("announcer.broadcast"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce broadcast [<index>]").append(ChatColor.WHITE).append(" - Broadcast an announcement NOW").toString());
        if(sender.hasPermission("announcer.delete"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce delete <index>").append(ChatColor.WHITE).append(" - Removes the announcement with the passed index").toString());
        if(sender.hasPermission("announcer.moderate"))
        {
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce enable [true|false]").append(ChatColor.WHITE).append(" - Enables or disables the announcer.").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce interval <seconds>").append(ChatColor.WHITE).append(" - Sets the seconds between the announcements.").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce prefix <message>").append(ChatColor.WHITE).append(" - Sets the prefix for all announcements.").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce list").append(ChatColor.WHITE).append(" - Lists all announcements").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce random [true|false]").append(ChatColor.WHITE).append(" - Enables or disables the random announcing mode.").toString());
        }
        if(sender.hasPermission("announcer.admin"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce reload").append(ChatColor.WHITE).append(" - Reloads the config.yml").toString());
        return true;
    }

    boolean onAddCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.add"))
        {
            if(args.length > 1)
            {
                StringBuilder messageToAnnounce = new StringBuilder();
                for(int index = 1; index < args.length; index++)
                {
                    messageToAnnounce.append(args[index]);
                    messageToAnnounce.append(" ");
                }

                plugin.addAnnouncement(messageToAnnounce.toString());
                sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Added announcement successfully!").toString());
                if(args.length > 100)
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("This message is too long!").toString());
            } else
            {
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("You need to pass a message to announce!").toString());
            }
            return true;
        } else
        {
            return false;
        }
    }

    boolean onBroadcastCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.broadcast"))
        {
            if(args.length == 2)
                try
                {
                    int index = Integer.parseInt(args[1]);
                    if(index > 0 && index <= plugin.numberOfAnnouncements())
                        plugin.announce(index);
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("There isn't any announcement with the passed index!").toString());
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Use '/announce list' to view all available announcements.").toString());
                }
                catch(NumberFormatException e)
                {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Index must be a integer!").toString());
                }
            else
            if(args.length == 1)
                plugin.announce();
            else
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Invalid number of arguments! Use /announce help to view the help!").toString());
            return true;
        } else
        {
            return false;
        }
    }

    boolean onListCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.moderate"))
        {
            if(args.length == 1 || args.length == 2)
            {
                int page = 1;
                if(args.length == 2)
                    try
                    {
                        page = Integer.parseInt(args[1]);
                    }
                    catch(NumberFormatException e)
                    {
                        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Invalid page number!").toString());
                    }
                sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append(String.format(" === Announcements [Page %d/%d] ===", new Object[] {
                    Integer.valueOf(page), Integer.valueOf(plugin.announcementMessages.size() / 7 + 1)
                })).toString());
                int indexStart = Math.abs(page - 1) * 7;
                int indexStop = Math.min(page * 7, plugin.announcementMessages.size());
                for(int index = indexStart + 1; index <= indexStop; index++)
                    sender.sendMessage(String.format("%d - %s", new Object[] {
                        Integer.valueOf(index), ChatColorHelper.replaceColorCodes(plugin.getAnnouncement(index))
                    }));

            } else
            {
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Invalid number of arguments! Use '/announce help' to view the help.").toString());
            }
            return true;
        } else
        {
            return false;
        }
    }

    boolean onDeleteCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.delete"))
        {
            if(args.length == 2)
                try
                {
                    int index = Integer.parseInt(args[1]);
                    if(index > 0 && index <= plugin.numberOfAnnouncements())
                    {
                        sender.sendMessage(String.format("%sRemoved announcement: '%s'", new Object[] {
                            ChatColor.GREEN, plugin.getAnnouncement(index)
                        }));
                        plugin.removeAnnouncement(index);
                    }
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("There isn't any announcement with the passed index!").toString());
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Use '/announce list' to view all available announcements.").toString());
                }
                catch(NumberFormatException e)
                {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Index must be a integer!").toString());
                }
            else
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Too many arguments! Use '/announce help' to view the help.").toString());
            return true;
        } else
        {
            return false;
        }
    }

    boolean onIntervalCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.moderate"))
        {
            if(args.length == 2)
                try
                {
                    plugin.setAnnouncementInterval(Integer.parseInt(args[1]));
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Set interval of scheduled announcements successfully!").toString());
                }
                catch(NumberFormatException e)
                {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Interval must be a number!").toString());
                }
                catch(ArithmeticException e)
                {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Interval must be greater than 0!").toString());
                }
            else
            if(args.length == 1)
                sender.sendMessage(String.format("%sPeriod duration is %d", new Object[] {
                    ChatColor.LIGHT_PURPLE, Long.valueOf(plugin.getAnnouncementInterval())
                }));
            else
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Too many arguments! Use '/announce help' to view the help!").toString());
            return true;
        } else
        {
            return false;
        }
    }

    boolean onPrefixCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.moderate"))
        {
            if(args.length > 1)
            {
                StringBuilder prefixBuilder = new StringBuilder();
                for(int index = 1; index < args.length; index++)
                {
                    prefixBuilder.append(args[index]);
                    prefixBuilder.append(" ");
                }

                plugin.setAnnouncementPrefix(prefixBuilder.toString());
                sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Set prefix for all announcements successfully!").toString());
            } else
            {
                sender.sendMessage(String.format("%sPrefix is %s", new Object[] {
                    ChatColor.LIGHT_PURPLE, ChatColorHelper.replaceColorCodes(plugin.getAnnouncementPrefix())
                }));
            }
            return true;
        } else
        {
            return false;
        }
    }

    boolean onRandomCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.moderate"))
        {
            if(args.length == 2)
            {
                if("true".equalsIgnoreCase(args[1]))
                {
                    plugin.setRandom(true);
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Random mode enabled!").toString());
                } else
                if("false".equalsIgnoreCase(args[1]))
                {
                    plugin.setRandom(false);
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Sequential mode enabled!").toString());
                } else
                {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Use true or false to enable or disable! ").append("Use '/announce help' to view the help.").toString());
                }
            } else
            if(args.length == 1)
            {
                if(plugin.isRandom())
                    sender.sendMessage((new StringBuilder()).append(ChatColor.LIGHT_PURPLE).append("Random mode is enabled.").toString());
                else
                    sender.sendMessage((new StringBuilder()).append(ChatColor.LIGHT_PURPLE).append("Sequential mode is enabled.").toString());
            } else
            {
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Invalid number of arguments! Use '/announce help' to view the help.").toString());
            }
            return true;
        } else
        {
            return false;
        }
    }

    boolean onEnableCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.moderate"))
        {
            if(args.length == 2)
            {
                if("true".equalsIgnoreCase(args[1]))
                {
                    plugin.setAnnouncerEnabled(true);
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Announcer enabled!").toString());
                } else
                if("false".equalsIgnoreCase(args[1]))
                {
                    plugin.setAnnouncerEnabled(false);
                    sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Announcer disabled!").toString());
                } else
                {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Use ture or false to enable or disable! ").append("Use '/announce help' to view the help.").toString());
                }
            } else
            if(args.length == 1)
            {
                if(plugin.isRandom())
                    sender.sendMessage((new StringBuilder()).append(ChatColor.LIGHT_PURPLE).append("Announcer is enabled.").toString());
                else
                    sender.sendMessage((new StringBuilder()).append(ChatColor.LIGHT_PURPLE).append("Announcer is disabled.").toString());
            } else
            {
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Invalid number of arguments! Use '/announce help' to view the help.").toString());
            }
            return true;
        } else
        {
            return false;
        }
    }

    boolean onReloadCommand(CommandSender sender, Command command, String label, String args[])
    {
        if(sender.hasPermission("announcer.moderate"))
        {
            if(args.length == 1)
            {
                plugin.reloadConfiguration();
                sender.sendMessage((new StringBuilder()).append(ChatColor.LIGHT_PURPLE).append("Configuration reloaded.").toString());
            } else
            {
                sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Any arguments needed! Use '/announce help' to view the help.").toString());
            }
            return true;
        } else
        {
            return false;
        }
    }

    private static final int ENTRIES_PER_PAGE = 7;
    private final AnnouncerPlugin plugin;
}
