package net.wiskr.ScheduledAnnouncer.plugin;

import java.util.Random;

import org.bukkit.Bukkit;

class AnnouncerThread extends Thread
{

    public AnnouncerThread(AnnouncerPlugin plugin)
    {
        lastAnnouncement = 0;
        this.plugin = plugin;
    }

    public void run()
    {
        if(plugin.isAnnouncerEnabled())
        {
            if(plugin.isRandom())
                lastAnnouncement = Math.abs(randomGenerator.nextInt() % this.plugin.getAnnouncementManager().numberOfAnnouncements());
            else
            if(++lastAnnouncement >= this.plugin.getAnnouncementManager().numberOfAnnouncements())
                lastAnnouncement = 0;
            if(lastAnnouncement < this.plugin.getAnnouncementManager().numberOfAnnouncements())
                this.plugin.getAnnouncementManager().getAnnouncement(lastAnnouncement).broadcast(Bukkit.getOnlinePlayers());
        }
    }

    public AnnouncerPlugin getPlugin()
    {
        return this.plugin;
    }

    private final Random randomGenerator = new Random();
    private final AnnouncerPlugin plugin;
    private int lastAnnouncement;
}
