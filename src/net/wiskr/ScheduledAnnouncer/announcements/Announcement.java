package net.wiskr.ScheduledAnnouncer.announcements;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.anxuiz.settings.bukkit.PlayerSettings;
import net.wiskr.ScheduledAnnouncer.settings.Settings;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.google.common.base.Preconditions;

public class Announcement {

    private final @Nonnull String message;
    private @Nullable Permission exemptPermission;
    private @Nullable Permission recievePermission;

    public Announcement(@Nonnull String message) {
        this(message, null, null);
    }
    public Announcement(@Nonnull String message, @Nullable Permission exemptPermission, @Nullable Permission recievePermission) {

        this.message = Preconditions.checkNotNull(message, "message");
        this.exemptPermission = exemptPermission;
        this.recievePermission = recievePermission;
    }
    
    public @Nonnull String getMessage() {
        return this.message;
    }

    public @Nullable Permission getExemptPermission() {
        return this.exemptPermission;
    }

    public @Nullable Permission getRecievePermission() {
        return this.recievePermission;
    }
    
    public void setExemptPermission(Permission permission) {
        this.exemptPermission = permission;
    }

    public void setRecievePermission(Permission permission) {
        this.recievePermission = permission;
    }

    public void broadcast(final Player[] recipients) {
        this.broadcast(Arrays.asList(recipients));
    }
    
    public void broadcast(final List<Player> recipients) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(null, new Runnable() {
            @Override
            public void run() {
                Iterator<Player> playerIterator = recipients.iterator();

                while (playerIterator.hasNext()) {
                    Player recipient = playerIterator.next();
                    
                    if (getExemptPermission() != null && recipient.hasPermission(getExemptPermission())) {
                        sendMessage(recipient, getMessage());                            
                    } else if (getRecievePermission() != null && recipient.hasPermission(getRecievePermission())) {
                        sendMessage(recipient, getMessage());                            
                    }

                    playerIterator.remove();
                }
            }
            
            private void sendMessage(Player player, String messageRaw) {
                boolean announcementsEnabled = PlayerSettings.getManager(player).getValue(Settings.TIPS, Boolean.class);

                if (announcementsEnabled) {
                    player.sendMessage(messageRaw.replaceAll("%player", player.getDisplayName()));
                }
            }
        });
    }
}
