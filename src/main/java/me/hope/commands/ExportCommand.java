package me.hope.commands;

import me.hope.commands.abstractClass.HopeCommand;
import org.bukkit.command.CommandSender;

/**
 * 导出CDK命令处理类
 */
public class ExportCommand extends HopeCommand {


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length > 1){
            String name = args[1];
            if (label.equalsIgnoreCase("export")){
                getPluginConfig().saveCDKToFile(name);
                sender.sendMessage(getPlugin().getPrefix()+"导出文件"+name +"成功!");
                return true;
            }
        }else{
            sender.sendMessage(getPlugin().getPrefix()+"/hopegift export [激活码类型名称]");
            return true;
        }
        return false;
    }
}
