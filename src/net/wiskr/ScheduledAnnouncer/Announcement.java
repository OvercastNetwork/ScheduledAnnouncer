package net.wiskr.ScheduledAnnouncer;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.google.common.base.Preconditions;

public class Announcement {

    private final @Nonnull String message;
    private final @Nullable Permission exemptPermission;
    private final @Nullable Permission recievePermission;

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

    public void broadcast(final List<Player> recipients) {
        Bukkit.getScheduler().scheduleAsyncDelayedTask(null, new Runnable() {
            @Override
            public void run() {
                Iterator<Player> playerIterator = recipients.iterator();

                while (playerIterator.hasNext()) {
                    Player recipient = playerIterator.next();
                    
                    if (exemptPermission != null && recipient.hasPermission(exemptPermission)) {
                        recipient.sendMessage(message);
                    } else if (recievePermission != null && recipient.hasPermission(recievePermission)) {
                        recipient.sendMessage(message);
                    }

                    playerIterator.remove();
                }
            }
        });
    }
}
