package me.hope.commands;

import me.hope.commands.abstractClass.HopeCommand;
import me.hope.exception.GiftNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DisableCommand extends HopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length ==1){
            if(label.equalsIgnoreCase("disable")){
                String giftTypeName = args[0];
                try {
                    getPluginConfig().disableGift(giftTypeName);
                } catch (GiftNotFoundException e) {
                    sender.sendMessage(getPlugin().getPrefix()+"未找到"+giftTypeName+",终止命令");
                    return true;
                }
                sender.sendMessage(getPlugin().getPrefix()+"禁用"+giftTypeName+"完成");
                return true;
            }
        }else{
            sender.sendMessage(getPlugin().getPrefix()+"/hopegift disable [激活码类型]");
        }
        return false;
    }
}
