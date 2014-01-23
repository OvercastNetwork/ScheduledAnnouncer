package net.wiskr.ScheduledAnnouncer.commands;

import net.wiskr.ScheduledAnnouncer.announcements.Announcement;
import net.wiskr.ScheduledAnnouncer.plugin.AnnouncerPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;

public class AnnouncerCommands {

    private static final AnnouncerPlugin plugin = AnnouncerPlugin.getInstance();

    @Command(
        aliases = {"version", "info"},
        desc = "Gets the version of the plugin",
        min = 0,
        max = 0
    )
    public static void version(CommandContext args, CommandSender sender) throws CommandException {
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
    }

    @Command (
        aliases = {"help"},
        desc = "Help for Announcer",
        min = 0,
        max = 0
    )
    public static void help(CommandContext args, CommandSender sender) throws CommandException {
        sender.sendMessage(String.format("%s === %s [Version %s] === ", new Object[] {
            ChatColor.LIGHT_PURPLE, plugin.getDescription().getName(), plugin.getDescription().getVersion()
        }));
        if(sender.hasPermission("announcer.add"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce add <message>").append(ChatColor.WHITE).append(" - Adds a new announcement").toString());
        if(sender.hasPermission("announcer.broadcast"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce broadcast [<index>]").append(ChatColor.WHITE).append(" - Broadcast an announcement NOW").toString());
        if(sender.hasPermission("announcer.delete"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce delete <index>").append(ChatColor.WHITE).append(" - Removes the announcement with the passed index").toString());
        if(sender.hasPermission("announcer.moderate")) {
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce enable [true|false]").append(ChatColor.WHITE).append(" - Enables or disables the announcer.").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce interval <seconds>").append(ChatColor.WHITE).append(" - Sets the seconds between the announcements.").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce prefix <message>").append(ChatColor.WHITE).append(" - Sets the prefix for all announcements.").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce list").append(ChatColor.WHITE).append(" - Lists all announcements").toString());
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce random [true|false]").append(ChatColor.WHITE).append(" - Enables or disables the random announcing mode.").toString());
        }
        if(sender.hasPermission("announcer.admin"))
            sender.sendMessage((new StringBuilder()).append(ChatColor.GRAY).append("/announce reload").append(ChatColor.WHITE).append(" - Reloads the config.yml").toString());
    }

    @Command (
        aliases = {"add"},
        desc = "Adds an announcement to the queue",
        flags = "er",
        min = 1,
        max = 3
    )
    @CommandPermissions("announcer.add")
    public static void add(CommandContext args, CommandSender sender) throws CommandException {
        Permission recievePermission = new Permission(args.getFlag('e'));
        Permission exemptPermission = new Permission(args.getFlag('r'));
        Announcement announcement = new Announcement(args.getJoinedStrings(0));

        if (recievePermission.getName() != null) { announcement.setRecievePermission(recievePermission); }
        if (exemptPermission.getName() != null) { announcement.setExemptPermission(exemptPermission); }

        if (announcement.getMessage().length() > 100) {
            throw new CommandException("This message is too long!");
        } else {
            plugin.getAnnouncementManager().addAnnouncement(announcement);
            sender.sendMessage(ChatColor.GREEN + "Added announcement successfully!");
        }
    }

    @Command(
        aliases = {"broadcast"},
        desc = "Calls the broadcast of a specific announcement",
        usage = "<index>",
        min = 1,
        max = 1
    )
    @CommandPermissions("announcer.broadcast")
    public static void broadcast(CommandContext args, CommandSender sender) throws CommandException {
        int index = args.getInteger(0);

        if (index <= plugin.getAnnouncementManager().numberOfAnnouncements()) {
            plugin.getAnnouncementManager().getAnnouncement(args.getInteger(0)).broadcast(Bukkit.getOnlinePlayers());
            sender.sendMessage(ChatColor.GREEN + "Succesfully broadcasted announcement!");
        } else if (index > plugin.getAnnouncementManager().numberOfAnnouncements() || index < 0){
            throw new CommandException("There isn't an announcement with that index");
        }
    }

    @Command(
        aliases = {"list"},
        desc = "Lists all active announcements",
        usage = "<index>",
        min = 1,
        max = 1
    )
    public static void list(CommandContext args, CommandSender sender) throws CommandException {
        int page = args.getInteger(0);

        sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append(String.format(" === Announcements [Page %d/%d] ===", new Object[] {
            Integer.valueOf(page), Integer.valueOf(plugin.getAnnouncementManager().numberOfAnnouncements() / 7 + 1)
        })).toString());

        int indexStart = Math.abs(page - 1) * 7;
        int indexStop = Math.min(page * 7, plugin.getAnnouncementManager().numberOfAnnouncements());

        for(int index = indexStart + 1; index <= indexStop; index++) {
            sender.sendMessage(String.format("%d - %s", new Object[] {
                Integer.valueOf(index), ChatColor.translateAlternateColorCodes('`', plugin.getAnnouncementManager().getAnnouncement(index).getMessage()),
            }));
        }
    }

    @Command(
        aliases = {"delete"},
        desc = "Deletes an announcement",
        usage = "<index>",
        min = 1,
        max = 1
    )
    @CommandPermissions("announcer.delete")
    public static void delete(CommandContext args, CommandSender sender) throws CommandException {
        int index = args.getInteger(0);
        if(index > 0 && index <= plugin.getAnnouncementManager().numberOfAnnouncements()) {
            sender.sendMessage(String.format("%sRemoved announcement: '%s'", new Object[] {
                ChatColor.GREEN, plugin.getAnnouncementManager().getAnnouncement(index).getMessage()
            }));
            plugin.getAnnouncementManager().removeAnnouncement(index);
        }
        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("There isn't any announcement with the passed index!").toString());
        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Use '/announce list' to view all available announcements.").toString());
    }

    @Command(
        aliases = {"interval"},
        desc = "Sets the interval for announcements",
        usage = "<interval>",
        min = 0,
        max = 1
    )
    @CommandPermissions("announcer.moderate")
    public static void interval(CommandContext args, CommandSender sender) throws CommandException {
        if (args.argsLength() == 1) {
            sender.sendMessage(String.format("%sPeriod duration is %d", new Object[] {
                    ChatColor.LIGHT_PURPLE, Long.valueOf(plugin.getAnnouncementManager().getAnnouncementInterval())
            }));
        } else {
            plugin.getAnnouncementManager().setAnnouncementInterval(args.getInteger(0));
            sender.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Set interval of scheduled announcements successfully!").toString());
        }
    }

    @Command(
        aliases = {"prefix"},
        desc = "Gets or sets the prefix for all messages",
        usage = "[prefix]",
        min = 0
    )
    @CommandPermissions("announcer.moderate")
    public static void prefix(CommandContext args, CommandSender sender) throws CommandException {
        if(args.argsLength() > 1) {
            plugin.getAnnouncementManager().setAnnouncementPrefix(args.getJoinedStrings(0));
        }

        sender.sendMessage(String.format("%sPrefix is %s", new Object[] {
                ChatColor.LIGHT_PURPLE, ChatColor.translateAlternateColorCodes('`', plugin.getAnnouncementManager().getAnnouncementPrefix())
        }));
    }

    @Command(
        aliases = {"random"},
        desc = "Gets or sets the \"random\" mode on the announcer plugin",
        usage = "[state]",
        min = 0,
        max = 1
    )
    @CommandPermissions("announcer.moderate")
    public static void random(CommandContext args, CommandSender sender) throws CommandException {
        if (args.argsLength() == 1) {
            if ("true".equalsIgnoreCase(args.getString(0))) {
                plugin.setRandom(true);
            } else if ("false".equalsIgnoreCase(args.getString(0))) {
                plugin.setRandom(false);
            } else {
                throw new CommandException("Set the random state with either true or false");
            }
        }
        sender.sendMessage(ChatColor.LIGHT_PURPLE + (plugin.isRandom() ? "Random" : "Sequential") + " mode is enabled.");
    }

    @Command(
        aliases = {"enabled"},
        desc = "Enables or disables the announcer plugin",
        usage = "<state>",
        min = 0,
        max = 1
    )
    @CommandPermissions("announcer.moderate")
    public static void enable(CommandContext args, CommandSender sender) throws CommandException {
        if (args.argsLength() == 1) {
            if (args.getString(0).equalsIgnoreCase("true")) {
                plugin.setAnnouncerEnabled(true);
            } else if(args.getString(0).equalsIgnoreCase("false")) {
                plugin.setAnnouncerEnabled(false);
            } else {
                throw new CommandException("Use <true|false> to set the state of the plugin.");
            }
        } else {
            plugin.setAnnouncerEnabled(!plugin.isEnabled());
        }
        sender.sendMessage(ChatColor.GREEN + (plugin.isEnabled() ? "Enabled" : "Disabled") + "plugin succesfully!");
    }

    @Command(
        aliases = {"interval"},
        desc = "Sets the interval for announcements",
        usage = "<interval>",
        min = 0,
        max = 0
    )
    @CommandPermissions("announcer.moderate")
    public static void reload(CommandContext args, CommandSender sender) throws CommandException {
        plugin.reloadConfiguration();
        sender.sendMessage((new StringBuilder()).append(ChatColor.LIGHT_PURPLE).append("Configuration reloaded.").toString());
    }
}
