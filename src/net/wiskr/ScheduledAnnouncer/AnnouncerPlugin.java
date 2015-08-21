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

    public void announce()
    {
        announcerThread.run();
    }

    public void announce(int index)
    {
        announce((String)announcementMessages.get(index - 1));
    }

    public void announce(String line)
    {
        if(line.startsWith("/"))
        {
            getServer().dispatchCommand(getServer().getConsoleSender(), line.substring(1));
        }
        else if(getServer().getOnlinePlayers().size() > 0)
        {
            String messageToSend = ChatColorHelper.replaceColorCodes(announcementPrefix + line);
            for(Player player : getServer().getOnlinePlayers())
            {
                if(player.hasPermission("announcer.receiver") && PlayerSettings.getManager(player).getValue(Settings.TIPS, Boolean.class))
                {
                    player.sendMessage(messageToSend.replaceAll("%player", player.getName()));
                }
            }
        }
    }

    public void saveConfiguration()
    {
        getConfig().set("announcement.messages", announcementMessages);
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

    public void addAnnouncement(String message)
    {
        announcementMessages.add(message);
        saveConfiguration();
    }

    public String getAnnouncement(int index)
    {
        return (String)announcementMessages.get(index - 1);
    }

    public int numberOfAnnouncements()
    {
        return announcementMessages.size();
    }

    public void removeAnnouncements()
    {
        announcementMessages.clear();
        saveConfiguration();
    }

    public void removeAnnouncement(int index)
    {
        announcementMessages.remove(index - 1);
        saveConfiguration();
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

    protected List announcementMessages;
    protected String announcementPrefix;
    protected long announcementInterval;
    protected boolean enabled;
    protected boolean random;
    private AnnouncerThread announcerThread;
    private Logger logger;
}
