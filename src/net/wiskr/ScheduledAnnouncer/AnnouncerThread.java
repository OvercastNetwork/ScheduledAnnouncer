package net.wiskr.ScheduledAnnouncer;

import java.util.Random;

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
                lastAnnouncement = Math.abs(randomGenerator.nextInt() % plugin.numberOfAnnouncements());
            else
            if(++lastAnnouncement >= plugin.numberOfAnnouncements())
                lastAnnouncement = 0;
            if(lastAnnouncement < plugin.numberOfAnnouncements())
                plugin.announce(lastAnnouncement + 1);
        }
    }

    private final Random randomGenerator = new Random();
    private final AnnouncerPlugin plugin;
    private int lastAnnouncement;
}
