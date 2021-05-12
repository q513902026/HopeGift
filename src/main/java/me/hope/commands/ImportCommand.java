package me.hope.commands;

import me.hope.Config;
import me.hope.HopeGift;
import me.hope.commands.abstractClass.HopeCommand;
import me.hope.core.inject.annotation.Inject;
import me.hope.exception.CDKNotFoundException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 导入CDK命令处理类
 */
public class ImportCommand extends HopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length >= 2){
            final String giftTypename = args[0];
            getPluginLogger().sendConsoleMessage("GiftTypeName: "+giftTypename);
            final String fileName = args[1];
            getPluginLogger().sendConsoleMessage("fileName: "+fileName);
            if (label.equalsIgnoreCase("import")){
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        getPluginConfig().loadCDKFromFile(giftTypename,fileName);
                    }
                }.runTaskAsynchronously(getPlugin());
                sender.sendMessage(getPlugin().getPrefix()+"导入文件中"+ fileName +".");
                return true;
            }
        }else{
            sender.sendMessage(getPlugin().getPrefix()+"/hopegift import [激活码类型名称] [文件名称]");
            return true;
        }
        return false;
    }
}
