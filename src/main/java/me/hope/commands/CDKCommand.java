package me.hope.commands;


import me.hope.commands.abstractClass.HopeCommand;
import me.hope.exception.CDKNotFoundException;
import org.bukkit.command.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 处理CDK的兑换
 */
public class CDKCommand extends HopeCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 仅玩家可以执行
        if (args.length == 1){
            if (sender instanceof Player) {
                String cdk = args[0];
                try {
                    if (getPluginConfig().canActiveCDK(cdk)) {
                        boolean success = getPluginConfig().activeCDK(cdk,(Player)sender);
                        if (success) {
                            sender.sendMessage(getPlugin().getPrefix() + "CDK兑换通过");
                            getPluginLogger().sendConsoleMessage("CDK[" + cdk + "]兑换通过,兑换者: " + sender.getName());
                        }else{
                            sender.sendMessage(getPlugin().getPrefix()+"CDK兑换失败,请确认");
                        }
                    }
                } catch (CDKNotFoundException e) {
                    sender.sendMessage(getPlugin().getPrefix() + "不存在的激活码，请重新确认!");
                }
                return true;
            }
        }
        return false;
    }
}
