package me.hope.commands;

import me.hope.commands.abstractClass.HopeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * 重载CDK设置命令
 */
public class ReloadConfigCommand extends HopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("reloadConfig")){
            getPluginConfig().reload();
            sender.sendMessage(getPlugin().getPrefix()+"重载文件成功!");
            return true;
        }
        return false;
    }
}
