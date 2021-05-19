package me.hope.commands.abstractClass;

import me.hope.Config;
import me.hope.HopeGift;
import me.hope.core.PluginLogger;
import me.hope.core.inject.annotation.Inject;
import org.bukkit.command.CommandExecutor;

public abstract class HopeCommand implements CommandExecutor {

    @Inject
    private static HopeGift plugin;
    @Inject
    private static Config pluginConfig;
    @Inject
    private static PluginLogger pluginLogger;

    protected static PluginLogger getPluginLogger() {
        return pluginLogger;
    }

    protected static HopeGift getPlugin() {
        return plugin;
    }

    protected static Config getPluginConfig() {
        return pluginConfig;
    }
}
