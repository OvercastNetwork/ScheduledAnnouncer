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
        if(!(new File(getDataFolder(), "config.yml")).exists())
            saveDefaultConfig();
        reloadConfiguration();
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
//        getConfig().set("announcement.messages", this.announcerThread.getAnnouncementMessages());
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
        // TODO: properly parse config, introduce some builder type?
        //        announcementMessages = getConfig().getStringList("announcement.messages");
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
