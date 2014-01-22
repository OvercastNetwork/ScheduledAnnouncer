package net.wiskr.ScheduledAnnouncer;

import java.io.File;
import java.util.logging.Logger;

import net.wiskr.ScheduledAnnouncer.settings.Settings;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AnnouncerPlugin extends JavaPlugin
{

    public AnnouncerPlugin()
    {
        announcerThread = new AnnouncerThread(this);
    }

    public void onEnable()
    {
        this.announcementManager = new AnnouncementManager(this.announcerThread);
        logger = getServer().getLogger();
        
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        System.out.println(this.getConfig().getConfigurationSection("announcement.messages"));
        this.reloadConfiguration();
        
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, announcerThread, this.announcementManager.getAnnouncementInterval() * TICKS_PER_SECOND, this.announcementManager.getAnnouncementInterval() * TICKS_PER_SECOND);
        
        AnnouncerCommandExecutor announcerCommandExecutor = new AnnouncerCommandExecutor(this);
        getCommand("announce").setExecutor(announcerCommandExecutor);
        getCommand("announcer").setExecutor(announcerCommandExecutor);

        Settings.register();
    }

    public void onDisable()
    {
        logger.info(String.format("%s is disabled!\n", new Object[] {
            getDescription().getFullName()
        }));
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
    
    private static final long TICKS_PER_SECOND = 20L;
    protected boolean enabled;
    protected boolean random;
    private AnnouncerThread announcerThread;
    private Logger logger;
    private AnnouncementManager announcementManager;
}
