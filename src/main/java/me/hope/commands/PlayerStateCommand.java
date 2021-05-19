package me.hope.commands;

import me.hope.commands.abstractClass.HopeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public class PlayerStateCommand extends HopeCommand {

    //TODO  通过玩家名字来获取玩家曾经使用过的激活码
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
