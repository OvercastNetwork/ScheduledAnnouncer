package net.wiskr.ScheduledAnnouncer.settings;

import me.anxuiz.settings.Setting;
import me.anxuiz.settings.SettingBuilder;
import me.anxuiz.settings.SettingCallbackManager;
import me.anxuiz.settings.SettingRegistry;
import me.anxuiz.settings.bukkit.PlayerSettings;
import me.anxuiz.settings.types.BooleanType;

public class Settings {

    public static void register() {
        SettingRegistry registry = PlayerSettings.getRegistry();
        SettingCallbackManager callbackManager = PlayerSettings.getCallbackManager();

        registry.register(Settings.TIPS);
    }

    public static final Setting TIPS = new SettingBuilder()
        .name("Tips")
        .summary("Show tips displayed in chat to you")
        .type(new BooleanType())
        .defaultValue(true).get();
}
