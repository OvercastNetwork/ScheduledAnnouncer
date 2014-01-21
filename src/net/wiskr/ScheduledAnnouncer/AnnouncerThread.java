package net.wiskr.ScheduledAnnouncer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;

class AnnouncerThread extends Thread
{

    private List<Announcement> announcements;

    public AnnouncerThread(AnnouncerPlugin plugin)
    {
        lastAnnouncement = 0;
        this.plugin = plugin;
        this.announcements = new ArrayList<Announcement>();
    }

    public void run()
    {
        if(plugin.isAnnouncerEnabled())
        {
            if(plugin.isRandom())
                lastAnnouncement = Math.abs(randomGenerator.nextInt() % this.announcements.size());
            else
            if(++lastAnnouncement >= this.announcements.size())
                lastAnnouncement = 0;
            if(lastAnnouncement < this.announcements.size())
                this.announcements.get(lastAnnouncement).broadcast(Bukkit.getOnlinePlayers());
        }
    }

    public void addAnnouncement(Announcement announcement)
    {
        this.announcements.add(announcement);
        this.plugin.saveConfiguration();
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
        this.plugin.saveConfiguration();
    }

    public void removeAnnouncement(int index)
    {
        this.announcements.remove(index);
        this.plugin.saveConfiguration();
    }

    public String[] getAnnouncementMessages() {
        String[] messages = new String[this.announcements.size()];
        
        for (int i = 0; i < messages.length; i++) {
            messages[i] = this.announcements.get(i).getMessage();
        }
        
        return messages;
    }
    
    private final Random randomGenerator = new Random();
    private final AnnouncerPlugin plugin;
    private int lastAnnouncement;
}
