package me.hope.commands;


import me.hope.HopeGift;
import me.hope.commands.abstractClass.HopeCommand;
import me.hope.core.PluginCommandMap;
import me.hope.core.inject.annotation.Inject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand extends HopeCommand {

    @Inject
    private static PluginCommandMap<HopeGift> commandManager;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            if(label.equalsIgnoreCase("help")){
                sender.sendMessage(getPlugin().getPrefix()+"子命令:");
                sender.sendMessage(commandManager.getCommandMap().keySet().toArray(new String[]{}));
                return true;
            }
        }
        return false;
    }
}
