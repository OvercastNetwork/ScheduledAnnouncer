package net.wiskr.ScheduledAnnouncer;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

public class AnnouncementManager {
    private List<Announcement> announcements;
    private String announcementPrefix;
    private long announcementInterval;
    private final AnnouncerThread parent;

    public AnnouncementManager(AnnouncerThread parent)
    {
        this.parent = parent;
    }
    
    public String getAnnouncementPrefix()
    {
        return this.announcementPrefix;
    }
    
    public long getAnnouncementInterval()
    {
        return this.announcementInterval;
    }

    public void setAnnouncementPrefix(String string)
    {
        this.announcementPrefix = string;
        this.parent.getPlugin().saveConfig();
    }
    
    public void setAnnouncementInterval(long interval)
    {
        this.announcementInterval = interval;
        this.parent.getPlugin().saveConfiguration();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.cancelTasks(this.parent.getPlugin());
        scheduler.scheduleSyncRepeatingTask(this.parent.getPlugin(), this.parent, announcementInterval * 20L, announcementInterval * 20L);

    }
    
    public List<Announcement> getAnnouncements() {
        return this.announcements;
    }

    public void addAnnouncement(Announcement announcement)
    {
        this.announcements.add(announcement);
        this.parent.getPlugin().saveConfiguration();
    }

    public Announcement getAnnouncement(int index)
    {
        return this.announcements.get(index);
    }

    public int numberOfAnnouncements()
    {
        return this.announcements.size();
    }

    public void removeAnnouncements()
    {
        this.announcements.clear();
        this.parent.getPlugin().saveConfiguration();
    }

    public void removeAnnouncement(int index)
    {
        this.announcements.remove(index);
        this.parent.getPlugin().saveConfiguration();
    }

    public String[] getAnnouncementMessages()
    {
        String[] messages = new String[this.announcements.size()];
        
        for (int i = 0; i < messages.length; i++) {
            messages[i] = this.announcements.get(i).getMessage();
        }
        
        return messages;
    }
    

}
