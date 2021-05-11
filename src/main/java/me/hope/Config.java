package me.hope;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hope.core.Gift;
import me.hope.core.PluginConfig;
import me.hope.core.PluginLogger;
import me.hope.core.RepeatGift;
import me.hope.core.enums.GiftResultType;
import me.hope.core.enums.GiftType;
import me.hope.core.inject.annotation.Inject;
import me.hope.exception.CDKNotFoundException;
import me.hope.exception.GiftNotFoundException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 对配置文件进行处理
 *
 * @author HopeAsd
 */
public class Config {

    @Inject
    private static PluginConfig<HopeGift> pluginConfigs;
    @Inject
    private static PluginLogger pluginLogger;

    /**
     * 存放所有的CDK   键是 CDK 值是兑换码的类型名称
     */
    private final Map<String, String> allCDKMap = Maps.newHashMap();
    /**
     * 存放 所有启用的兑换码类型的实例
     */
    private final Map<String, Gift> giftMap = Maps.newHashMap();
    /**
     * 存放 所有还未使用的兑换码
     */
    private final List<String> unusedCDKs = Lists.newArrayList();
    /**
     * 存放 所有启用的兑换码类型的名称
     */
    private List<String> enableGift;

    /**
     * 初始化
     */
    public void init() {
        allCDKMap.clear();
        unusedCDKs.clear();
        giftMap.clear();

        pluginLogger.sendConsoleMessage("正在载入启用的激活码类型");
        enableGift = pluginConfigs.getConfig("config").getStringList("enableGifts");

        Iterator<String> enableGiftIterator = enableGift.iterator();
        String key;
        while (enableGiftIterator.hasNext()) {
            key = enableGiftIterator.next();
            pluginLogger.sendConsoleMessage("载入类型[" + key + "]的可用激活码");
            addCDKToMap(key);
            addGiftToMap(key);
        }
    }

    /**
     * 根据激活码的类型名称 导入激活码类型数据到缓存中
     * @param key 激活码类型
     */
    private void addGiftToMap(String key) {
        try {
            getGift(key);
        } catch (GiftNotFoundException e) {
            pluginLogger.sendErrorMessage("载入类型["+key+"]时发生了未经处理错误");
        }
    }

    /**
     * 判断是否可以激活
     *
     * @param cdk 激活码
     * @return 当结果为true时 则激活码可以使用
     * @throws CDKNotFoundException 当CDK无法被找到
     */
    public boolean canActiveCDK(String cdk) throws CDKNotFoundException {
        if (!allCDKMap.containsKey(cdk)) {
            throw new CDKNotFoundException();
        }
        return unusedCDKs.contains(cdk);
    }

    /**
     * 根据激活码的类型名称 导入所有未使用的代码到缓存中
     *
     * @param key 激活码的类型名称
     */
    private void addCDKToMap(String key) {
        Map<String, Object> keyMap = pluginConfigs.getConfig("cdk").getConfigurationSection(key).getValues(false);
        for (Map.Entry<String, Object> entry : keyMap.entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                registerCDK(key, entry.getKey(), ((Boolean) entry.getValue()).booleanValue());
            }
        }
    }

    /**
     *  唯一激活CDK的入口
     * @param cdk 激活码
     * @param player 执行的玩家
     * @return 当返回true时 代表激活码激活成功
     * @throws GiftNotFoundException 当激活码类型无法被找到或未激活时
     */
    public boolean activeCDK(String cdk, Player player){
        String giftTypeKey = allCDKMap.get(cdk);
        try {
            Gift gift = getGift(giftTypeKey);
            boolean success = gift.run(player.getName());
            if (success) {postActiveCDK(gift,cdk);}
            return success;
        } catch (GiftNotFoundException e) {
            pluginLogger.sendErrorMessage(player.getName()+"尝试使用不存在或未激活的激活码类型["+giftTypeKey+"]");
        }
        return false;
    }

    /**
     * 激活CDK的后处理
     * @param gift 激活码类型实例
     * @param cdk 激活码
     */
    private void postActiveCDK(Gift gift, String cdk) {
        if (gift instanceof RepeatGift){
            RepeatGift repeatGift = (RepeatGift) gift;
            pluginConfigs.getConfig("gift").set("gift."+gift.getName()+"USER_LIST",repeatGift.getUserList());
            pluginConfigs.saveConfig("gift");
        }else{
            unusedCDKs.remove(cdk);
            allCDKMap.remove(cdk);
            pluginConfigs.getConfig("cdk").set(gift.getName()+"."+cdk,true);
            pluginConfigs.saveConfig("cdk");
        }
    }

    /**
     * 注册有效的激活码
     *
     * @param giftTypeKey 激活码类别
     * @param cdk         激活码
     * @param used        是否使用
     */
    private void registerCDK(String giftTypeKey, String cdk, boolean used) {
        if (!used) {
            unusedCDKs.add(cdk);
        }
        allCDKMap.put(cdk, giftTypeKey);
    }

    /**
     * 获取 Gift实例
     *
     * @param giftTypeKey 激活码类别
     * @return Gift的实例
     * @throws GiftNotFoundException 当激活码类别不存在或未激活时
     */
    public Gift getGift(String giftTypeKey) throws GiftNotFoundException {
        if (giftMap.get(giftTypeKey) == null) {
            if (!enableGift.contains(giftTypeKey)) {
                throw new GiftNotFoundException();
            }
            pluginLogger.sendConsoleMessage("初始化Gift实例");
            ConfigurationSection giftData = pluginConfigs.getConfig("gift").getConfigurationSection("gift." + giftTypeKey);
            GiftType giftType = GiftType.valueOf(giftData.getString("type", "UNIQUE"));
            GiftResultType resultType = GiftResultType.valueOf(giftData.getString("result", "COMMANDS"));
            List<String> cmds = giftData.getStringList("value");
            Gift giftInstance;
            if (giftType == GiftType.REPEAT) {
                List<String> userList = giftData.getStringList("USER_LIST");
                giftInstance = new RepeatGift(giftTypeKey,giftType, resultType, userList);
            } else {
                giftInstance = new Gift(giftTypeKey,giftType, resultType);
            }
            giftInstance.setCmds(cmds);
            pluginLogger.sendConsoleMessage(giftInstance.toString());
            giftMap.put(giftTypeKey, giftInstance);
            return giftInstance;
        } else {
            return giftMap.get(giftTypeKey);
        }
    }

    /**
     * 从文件中载入CDK
     *
     * @param giftTypename 激活码类别
     * @param name 文件名称
     */
    public void loadCDKFromFile(String giftTypename, String name) {
        //TODO 从txt文件种读取CDK 根据文件名称来判断激活码种类
        pluginLogger.sendConsoleMessage("loadCDKFromFile "+ giftTypename+ " " + name);
    }

    /**
     * 把CDK输出到文件中
     *
     * @param name 输出的CDK激活码类型名称
     */
    public void saveCDKToFile(String name) {
        //TODO 根据name的名称获取其激活码 然后保存到 {name}.txt 文件中
        pluginLogger.sendConsoleMessage("saveCDKToFile " + name);
    }

    public String getPrefix() {
        return pluginConfigs.getConfig("config").getString("prefix").replaceAll("&", "§");
    }

    /**
     * 重载缓存
     */
    public void reload() {
        pluginConfigs.reloadAllConfig();
        init();
    }
}
