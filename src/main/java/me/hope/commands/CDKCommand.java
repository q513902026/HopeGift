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
    public static boolean fileExportOrImport = false;
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // 仅玩家可以执行
        if (args.length == 1){
            if (sender instanceof Player) {
                String cdk = args[0];
                try {
                    if (isExportOrImport()){
                        sender.sendMessage(getPlugin().getPrefix()+"CDK系统维护中,请稍后再试");
                        return true;
                    }
                    if (getPluginConfig().canActiveCDK(cdk)) {
                        boolean success = getPluginConfig().activeCDK(cdk,(Player)sender);
                        if (success) {
                            sender.sendMessage(getPlugin().getPrefix() + "CDK兑换通过");
                            getPluginLogger().sendConsoleMessage("CDK[" + cdk + "]兑换通过,兑换者: " + sender.getName());
                        }else{
                            sender.sendMessage(getPlugin().getPrefix()+"CDK兑换失败,该激活码每个玩家只能使用一次");
                        }
                        return true;
                    }else{
                        sender.sendMessage(getPlugin().getPrefix()+"CDK兑换失败，已被使用的CDK");
                    }
                } catch (CDKNotFoundException e) {
                    sender.sendMessage(getPlugin().getPrefix() + "不存在的激活码，请重新确认!");
                }
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean isExportOrImport(){
        return CDKCommand.fileExportOrImport;
    }
    public static synchronized void setExportOrImport(boolean states){
        CDKCommand.fileExportOrImport = states;
    }
}
