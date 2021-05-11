package me.hope.commands;

import me.hope.Config;
import me.hope.HopeGift;
import me.hope.commands.abstractClass.HopeCommand;
import me.hope.core.inject.annotation.Inject;
import me.hope.exception.CDKNotFoundException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 导入CDK命令处理类
 */
public class ImportCommand extends HopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length > 1){
            String giftTypename = args[1];
            String fileName = args[2];
            if (label.equalsIgnoreCase("import")){
                getPluginConfig().loadCDKFromFile(giftTypename,fileName);
                sender.sendMessage(getPlugin().getPrefix()+"导入文件"+ fileName +"成功!");
                return true;
            }
        }else{
            sender.sendMessage(getPlugin().getPrefix()+"/hopegift import [激活码类型名称] [文件名称]");
            return true;
        }
        return false;
    }
}
