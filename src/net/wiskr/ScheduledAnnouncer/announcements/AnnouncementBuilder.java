package net.wiskr.ScheduledAnnouncer.announcements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.permissions.Permission;

import com.google.common.base.Preconditions;

public class AnnouncementBuilder {

    List<Announcement> announcements;
    ConfigurationSection config;

    public AnnouncementBuilder(ConfigurationSection config) {
        this.announcements = new ArrayList<Announcement>();
        this.config = Preconditions.checkNotNull(config, "config");

        for(Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            MemorySection options = (MemorySection) entry.getValue();
            Announcement announcement = new Announcement(entry.getKey());
            
            announcement.setExemptPermission(new Permission(options.getString("exempt-permission")));
            announcement.setRecievePermission(new Permission(options.getString("recieve-permission")));
            
            this.announcements.add(announcement);
        }
    }
    
    public List<Announcement> getAnnouncements() {
        return this.announcements;
    }
}
