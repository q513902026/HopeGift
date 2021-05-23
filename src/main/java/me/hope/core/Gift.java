package me.hope.core;

import com.google.common.collect.Lists;
import me.hope.HopeGift;
import me.hope.core.enums.GiftResultType;
import me.hope.core.enums.GiftType;
import me.hope.core.inject.annotation.Inject;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class Gift {
    @Inject
    private static Server server;
    @Inject
    private static HopeGift plugin;
    protected String name;
    protected GiftType giftType;
    protected GiftResultType resultType;
    protected List<String> cmds;
    public Gift(String name,GiftType giftType,GiftResultType resultType){
        this.name = name;
        this.giftType = giftType;
        this.resultType = resultType;
        this.cmds = Lists.newArrayList();
    }
    public GiftType getGiftType(){return giftType;}
    public void addCmds(String... cmds){
        this.cmds.addAll(Arrays.asList(cmds));
    }
    public void setCmds(List<String> cmds){
        this.cmds = cmds;
    }

    public boolean run(final String player){
        Player p = server.getPlayerExact(player);
        final Location location = p.getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                for(String cmd:cmds){
                    cmd = cmd.replaceAll("%PLAYER_X%", String.valueOf(location.getX()));
                    cmd = cmd.replaceAll("%PLAYER_Y%", String.valueOf(location.getY()));
                    cmd = cmd.replaceAll("%PLAYER_Z%", String.valueOf(location.getZ()));
                    cmd = cmd.replaceAll("%PLAYER%",player);
                    server.dispatchCommand(server.getConsoleSender(),cmd);
                }
            }
        }.runTask(plugin);
        return true;
    }
    @Override
    public String toString() {
        return "Gift{" +
                "name="+ name +
                "giftType=" + giftType +
                ", resultType=" + resultType +
                ", cmds=" + cmds +
                '}';
    }

    public String getName() {
        return name;
    }
}
