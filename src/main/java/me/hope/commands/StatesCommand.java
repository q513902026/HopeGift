package me.hope.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class StatesCommand extends StateCommand{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            if (label.equalsIgnoreCase("states")){
                Set<String> giftTypeNames = getPluginConfig().getGiftTypeNames();
                for(String giftTypeName : giftTypeNames){
                    super.onCommand(sender,command,"state", new String[]{giftTypeName});
                }
                return true;
            }
        }else{
            sender.sendMessage(getPlugin().getPrefix()+"/hopegift states ");
            return true;
        }
        return false;
    }
}
