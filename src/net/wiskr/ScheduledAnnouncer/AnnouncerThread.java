// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AnnouncerThread.java

package net.wiskr.ScheduledAnnouncer;

import java.util.Random;

// Referenced classes of package net.wiskr.ScheduledAnnouncer:
//            AnnouncerPlugin

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
