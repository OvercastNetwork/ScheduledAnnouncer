package net.wiskr.ScheduledAnnouncer.announcements;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.permissions.Permission;

public class YamlAnnouncement {

    public static Announcement load(Map.Entry<String, Object> entry) {
        Announcement announcement = new Announcement(entry.getKey());
        
        if (entry.getValue() instanceof MemorySection) {
            MemorySection section = (MemorySection) entry.getValue();
            
            if (section.getString("exempt-permission") != null) {
                announcement.setExemptPermission(new Permission(section.getString("exempt-permission")));
            }
            if (section.getString("recieve-permission") != null) {
                announcement.setRecievePermission(new Permission(section.getString("recieve-permission")));
            }
        } else {
            throw new IllegalArgumentException();
        }
        
        return announcement;
    }
    
    public static void save(Announcement announcement, ConfigurationSection config, boolean forceUpdate) {
        if (config.getConfigurationSection(announcement.getMessage()) == null || forceUpdate) {
            MemoryConfiguration options = new MemoryConfiguration();
            if (announcement.getExemptPermission() != null) {
                options.set("exempt-permission", announcement.getExemptPermission().getName());            
            }
            if (announcement.getRecievePermission() != null) {
                options.set("recieve-permission", announcement.getRecievePermission().getName());
            }
            
            config.set(announcement.getMessage(), options);            
        }
    }
    
}
