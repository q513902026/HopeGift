package me.hope.commands;

import me.hope.commands.abstractClass.HopeCommand;
import me.hope.core.enums.GiftResultType;
import me.hope.core.enums.GiftType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CreateGiftCommand extends HopeCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length> 1){
            String giftTypeName = args[0];
            GiftType giftType = GiftType.valueOf(args[1]);
            GiftResultType resultType = GiftResultType.valueOf(args[2]);
            List<String> defaultCmdList = Arrays.asList(args).subList(3,args.length);
            StringBuilder defaultCmd = getCmdForList(defaultCmdList);
            for(String subCmd : args){}
            if(label.equalsIgnoreCase("createGift")){
                boolean success = getPluginConfig().createGift(giftTypeName,giftType,resultType,defaultCmd.toString());
                if (success){
                    //TODO CREATE GIFT SUCCESS MESSAGE
                    sender.sendMessage(getPlugin().getPrefix() + " SUCCESS MESSAGE");
                }else{
                    //TODO CREATE GIFT ERROR MESSAGE
                    sender.sendMessage(getPlugin().getPrefix() + " ERROR MESSAGE");
                }
                return true;
            }
        }else{
            sender.sendMessage(getPlugin().getPrefix() + "/hopegift createGift [激活码类型名称] [激活码类别] [默认激活码指令]");
            sender.sendMessage(getPlugin().getPrefix() + "示例: /hopegift createGift UNIQUE COMMANDS GAMEMODE 1 %PLAYER%");
        }
        return false;
    }

    public static StringBuilder getCmdForList(List<String> defaultCmdList) {
        StringBuilder defaultCmd = new StringBuilder();
        for (int index = 0; index < defaultCmdList.size(); index++) {
            defaultCmd.append(defaultCmdList.get(index));
            defaultCmd.append(" ");
        }
        defaultCmd.delete(defaultCmd.length()-1,defaultCmd.length());
        return defaultCmd;
    }
}
