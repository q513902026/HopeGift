package me.hope.commands;

import me.hope.commands.abstractClass.HopeCommand;
import me.hope.exception.GiftNotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class StateCommand extends HopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1){
            final String giftTypeName = args[0];
            if (label.equalsIgnoreCase("state")){
                int usedCDKSize = 0;
                int maxCDKSize = 0;
                try {
                    usedCDKSize = getPluginConfig().getCDKsStateByGiftTypeName(giftTypeName);
                    maxCDKSize = getPluginConfig().getCDKsSizeByGiftTypeName(giftTypeName);
                } catch (GiftNotFoundException e) {
                    sender.sendMessage(getPlugin().getPrefix()+"输入的激活码类型"+giftTypeName+"不存在 请检查后重新输入");
                    return true;
                }
                if (maxCDKSize == -1){
                    sender.sendMessage(getPlugin().getPrefix()+"激活码类型:"+giftTypeName+", 被使用了"+usedCDKSize+"次");
                    return true;
                }
                if (maxCDKSize > 0 && maxCDKSize >= usedCDKSize){
                    sender.sendMessage(getPlugin().getPrefix()+"激活码类型:"+giftTypeName+", ["+usedCDKSize+"/"+maxCDKSize+"]");
                }else{
                    sender.sendMessage(getPlugin().getPrefix()+"输入的激活码类型"+giftTypeName+"存在错误 请检查后重新输入");
                }
                return true;
            }
        }else{
            sender.sendMessage(getPlugin().getPrefix()+"/hopegift state [激活码类型名称]");
            return true;
        }
        return false;
    }
}
