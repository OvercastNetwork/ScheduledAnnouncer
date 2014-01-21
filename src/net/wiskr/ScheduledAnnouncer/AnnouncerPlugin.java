package net.wiskr.ScheduledAnnouncer;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import me.anxuiz.settings.bukkit.PlayerSettings;
import net.wiskr.ScheduledAnnouncer.settings.Settings;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.entity.Player;

public class AnnouncerPlugin extends JavaPlugin
{

    public AnnouncerPlugin()
    {
        announcerThread = new AnnouncerThread(this);
    }

    public void onEnable()
    {
        logger = getServer().getLogger();
        if(!(new File(getDataFolder(), "config.yml")).exists())
            saveDefaultConfig();
        reloadConfiguration();
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, announcerThread, announcementInterval * 20L, announcementInterval * 20L);
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
        getConfig().set("announcement.messages", this.announcerThread.getAnnouncementMessages());
        getConfig().set("announcement.interval", Long.valueOf(announcementInterval));
        getConfig().set("announcement.prefix", announcementPrefix);
        getConfig().set("announcement.enabled", Boolean.valueOf(enabled));
        getConfig().set("announcement.random", Boolean.valueOf(random));
        saveConfig();
    }

    public void reloadConfiguration()
    {
        reloadConfig();
        announcementPrefix = getConfig().getString("announcement.prefix", "&c[Announcement] ");
        announcementMessages = getConfig().getStringList("announcement.messages");
        announcementInterval = getConfig().getInt("announcement.interval", 1000);
        enabled = getConfig().getBoolean("announcement.enabled", true);
        random = getConfig().getBoolean("announcement.random", false);
    }

    public String getAnnouncementPrefix()
    {
        return announcementPrefix;
    }

    public void setAnnouncementPrefix(String announcementPrefix)
    {
        this.announcementPrefix = announcementPrefix;
        saveConfig();
    }

    public long getAnnouncementInterval()
    {
        return announcementInterval;
    }

    public void setAnnouncementInterval(long announcementInterval)
    {
        this.announcementInterval = announcementInterval;
        saveConfiguration();
        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.cancelTasks(this);
        scheduler.scheduleSyncRepeatingTask(this, announcerThread, announcementInterval * 20L, announcementInterval * 20L);
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

    protected String announcementPrefix;
    protected long announcementInterval;
    protected boolean enabled;
    protected boolean random;
    private AnnouncerThread announcerThread;
    private Logger logger;
}
