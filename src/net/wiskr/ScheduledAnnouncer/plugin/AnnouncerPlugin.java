package net.wiskr.ScheduledAnnouncer.plugin;

import java.util.logging.Logger;

import net.wiskr.ScheduledAnnouncer.announcements.Announcement;
import net.wiskr.ScheduledAnnouncer.announcements.AnnouncementBuilder;
import net.wiskr.ScheduledAnnouncer.announcements.YamlAnnouncement;
import net.wiskr.ScheduledAnnouncer.commands.AnnouncerCommands;
import net.wiskr.ScheduledAnnouncer.settings.Settings;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;

public class AnnouncerPlugin extends JavaPlugin
{

    public AnnouncerPlugin()
    {
        announcerThread = new AnnouncerThread(this);
    }

    public void onEnable()
    {
        instance = this;

        this.announcementManager = new AnnouncementManager(this.announcerThread);
        logger = getServer().getLogger();
        
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.reloadConfiguration();
        
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, announcerThread, this.announcementManager.getAnnouncementInterval() * TICKS_PER_SECOND, this.announcementManager.getAnnouncementInterval() * TICKS_PER_SECOND);

        Settings.register();
        this.setupCommands();
    }

    public void onDisable()
    {
        instance = null;

        logger.info(String.format("%s is disabled!\n", new Object[] {
            getDescription().getFullName()
        }));
    }

    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };

        CommandsManagerRegistration register = new CommandsManagerRegistration(this, this.commands);
        register.register(AnnouncerCommands.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }
    
    public void doAnnouncement()
    {
        announcerThread.run();
    }

    public void saveConfiguration()
    {
        for (Announcement announcement : this.announcementManager.getAnnouncements()) {
            YamlAnnouncement.save(announcement, this.getConfig().getConfigurationSection("announcement.messages"), true);
        }
        getConfig().set("announcement.interval", Long.valueOf(this.announcementManager.getAnnouncementInterval()));
        getConfig().set("announcement.prefix", this.announcementManager.getAnnouncementPrefix());
        getConfig().set("announcement.enabled", Boolean.valueOf(this.enabled));
        getConfig().set("announcement.random", Boolean.valueOf(this.random));
        saveConfig();
    }

    public void reloadConfiguration()
    {
        reloadConfig();
        this.announcementManager.setAnnouncementPrefix(getConfig().getString("announcement.prefix", "&c[Announcement] "));
        this.announcementManager.removeAnnouncements();
        this.announcementManager.getAnnouncements().addAll(new AnnouncementBuilder(this.getConfig().getConfigurationSection("announcement.messages")).getAnnouncements());
        this.announcementManager.setAnnouncementInterval(getConfig().getInt("announcement.interval", 1000));
        enabled = getConfig().getBoolean("announcement.enabled", true);
        random = getConfig().getBoolean("announcement.random", false);
    }

    public boolean isAnnouncerEnabled()
    {
        return enabled;
    }

    public void setAnnouncerEnabled(boolean enabled)
    {
        this.enabled = enabled;
        saveConfiguration();
    }

    public boolean isRandom()
    {
        return random;
    }

    public void setRandom(boolean random)
    {
        this.random = random;
        saveConfiguration();
    }

    public AnnouncementManager getAnnouncementManager()
    {
        return this.announcementManager;
    }

    public void callThread()
    {
        if (this.enabled)
        {
            this.announcerThread.run();            
        }
    }

    public static AnnouncerPlugin getInstance() {
        return instance;
    }

    private CommandsManager<CommandSender> commands;
    private static final long TICKS_PER_SECOND = 20L;
    protected boolean enabled;
    protected boolean random;
    private AnnouncerThread announcerThread;
    private Logger logger;
    private AnnouncementManager announcementManager;
    private static AnnouncerPlugin instance;
}
