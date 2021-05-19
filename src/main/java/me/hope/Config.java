package me.hope;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hope.commands.CDKCommand;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

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
    @Inject
    private static HopeGift plugin;

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
        while (enableGiftIterator.hasNext()) {
            addToMap(enableGiftIterator.next());
        }
    }

    /**
     * 把激活码添加到内存中的入口
     * @param key
     */
    private void addToMap(String key) {
        pluginLogger.sendConsoleMessage("载入类型[" + key + "]的可用激活码");
        addCDKToMap(key);
        try {
            addGiftToMap(key);
        } catch (GiftNotFoundException e) {
            pluginLogger.sendConsoleMessage("载入类型[" + key + "]时,发生了错误.");
        }
    }

    /**
     * 从配置文件中启用激活码类型
     *
     * @param key
     */
    public void enableGift(String key) throws GiftNotFoundException {
        enableGift.add(key);
        addToMap(key);
        saveEnableGifts();
    }

    private void saveEnableGifts() {
        pluginConfigs.getConfig("config").set("enableGifts", enableGift);
    }

    /**
     * 从内存中关闭激活码类型
     *
     * @param key
     */
    public void disableGift(String key) throws GiftNotFoundException {
        pluginLogger.sendConsoleMessage("清除类型[" + key + "]的激活码");
        removeGiftWithMap(key);
        removeCDKWithMap(key);
        enableGift.remove(key);
        saveEnableGifts();
    }

    /**
     * 根据给定的激活码类型 如果CDK已被启用则从内存中返回使用情况，否则从文件中读取
     *
     * @param giftTypeName 激活码类型名称
     * @return 使用的激活码数量
     */
    public int getCDKsStateByGiftTypeName(String giftTypeName) throws GiftNotFoundException {
        if (enableGift.contains(giftTypeName)) {

            Gift gift = getGift(giftTypeName);
            if (gift instanceof RepeatGift) {
                return ((RepeatGift) gift).getUserList().size();
            } else {
                int result = 0;
                for (Map.Entry<String, String> entry : allCDKMap.entrySet()) {
                    if (entry.getValue().equals(giftTypeName) && !unusedCDKs.contains(entry.getKey())) {
                        result += 1;
                    }
                }
                return result;
            }
        } else {
            GiftType giftType = getGiftWithConfigFile(giftTypeName);
            int result = 0;
            if (giftType == GiftType.REPEAT) {
                return getGiftConfig().getStringList("gift." + giftTypeName + ".USER_LIST").size();
            } else {
                Map<String, Object> keyMap = getCdkConfig().getConfigurationSection(giftTypeName).getValues(false);

                for (Map.Entry<String, Object> entry : keyMap.entrySet()) {
                    if (entry.getValue() instanceof Boolean) {
                        if ((boolean) entry.getValue()) {
                            result += 1;
                        }
                    }
                }
            }
            return result;
        }
    }

    /**
     * 从配置文件中获取 激活码类型
     *
     * @param giftTypeName 激活码类型名称
     * @return GiftType的实例
     * @throws GiftNotFoundException 激活码类型不存在时抛出
     */
    private GiftType getGiftWithConfigFile(String giftTypeName) throws GiftNotFoundException {
        if (getGiftConfig().isConfigurationSection("gift." + giftTypeName)) {
            String giftTypeString = getGiftConfig().getString("gift." + giftTypeName + ".type");
            GiftType giftType = GiftType.valueOf(giftTypeString);
            return giftType;
        }
        throw new GiftNotFoundException();

    }

    /**
     * 快速获取gift配置文件实例
     *
     * @return gift配置文件实例
     */
    private FileConfiguration getGiftConfig() {
        return pluginConfigs.getConfig("gift");
    }

    /**
     * 从配置文件中获取特定激活码总量
     *
     * @param giftTypeName 激活码类型名称
     * @return 返回激活码总数量
     */
    public int getCDKsSizeByGiftTypeName(String giftTypeName) throws GiftNotFoundException {
        GiftType giftType = getGiftWithConfigFile(giftTypeName);
        if (giftType == GiftType.REPEAT) {
            return -1;
        }
        Map<String, Object> keyMap = getCdkConfig().getConfigurationSection(giftTypeName).getValues(false);
        return keyMap.size();

    }

    /**
     * 从配置文件中获取所有激活码类型的集合
     *
     * @return 所有激活码类型的集合
     */
    public Set<String> getGiftTypeNames() {
        return getGiftConfig().getConfigurationSection("gift").getKeys(false);
    }

    /**
     * 根据激活码的类型名称 导入激活码类型数据到缓存中
     *
     * @param key 激活码类型
     */
    private void addGiftToMap(String key) throws GiftNotFoundException {
        getGift(key);

    }

    /**
     * 根据激活码的类型 从内存中去除
     *
     * @param key 激活码类型
     */
    private void removeGiftWithMap(String key) throws GiftNotFoundException {
        Gift gift = getGift(key);
        giftMap.remove(gift);

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
        Map<String, Object> keyMap = getCdkConfig().getConfigurationSection(key).getValues(false);
        pluginLogger.sendConsoleMessage("激活码总量:" + keyMap.size());
        for (Map.Entry<String, Object> entry : keyMap.entrySet()) {
            if (entry.getValue() instanceof Boolean) {
                registerCDK(key, entry.getKey(), (Boolean) entry.getValue());
            }
        }
    }

    /**
     * 从内存中去除激活码
     *
     * @param key
     */
    private void removeCDKWithMap(String key) {
        Set<String> CDKs = getCdkConfig().getConfigurationSection(key).getKeys(false);
        for (String cdk : CDKs) {
            unreigsterCDK(cdk);
        }
    }

    /**
     * 唯一激活CDK的入口
     *
     * @param cdk    激活码
     * @param player 执行的玩家
     * @return 当返回true时 代表激活码激活成功
     */
    public boolean activeCDK(final String cdk, Player player) {
        String giftTypeKey = allCDKMap.get(cdk);
        try {
            final Gift gift = getGift(giftTypeKey);
            boolean success = gift.run(player.getName());
            if (success) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        postActiveCDK(gift, cdk);
                    }
                }.runTaskAsynchronously(plugin);
            }
            return success;
        } catch (GiftNotFoundException e) {
            pluginLogger.sendErrorMessage(player.getName() + "尝试使用不存在或未激活的激活码类型[" + giftTypeKey + "]");
        }
        return false;
    }

    /**
     * 激活CDK的后处理
     *
     * @param gift 激活码类型实例
     * @param cdk  激活码
     */
    private void postActiveCDK(Gift gift, String cdk) {
        if (gift instanceof RepeatGift) {
            RepeatGift repeatGift = (RepeatGift) gift;
            getGiftConfig().set("gift." + gift.getName() + ".USER_LIST", repeatGift.getUserList());
            pluginConfigs.saveConfig("gift");
        } else {
            unusedCDKs.remove(cdk);
            getCdkConfig().set(gift.getName() + "." + cdk, true);
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
     * 从内存中去除激活码
     *
     * @param cdk 要去除的激活码
     */
    private void unreigsterCDK(String cdk) {
        unusedCDKs.remove(cdk);
        allCDKMap.remove(cdk);
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
            //pluginLogger.sendConsoleMessage("初始化Gift实例");
            ConfigurationSection giftData = getGiftConfig().getConfigurationSection("gift." + giftTypeKey);
            GiftType giftType = GiftType.valueOf(giftData.getString("type", "UNIQUE"));
            GiftResultType resultType = GiftResultType.valueOf(giftData.getString("result", "COMMANDS"));
            List<String> cmds = giftData.getStringList("value");
            Gift giftInstance;
            if (giftType == GiftType.REPEAT) {
                List<String> userList = giftData.getStringList("USER_LIST");
                giftInstance = new RepeatGift(giftTypeKey, giftType, resultType, userList);
            } else {
                giftInstance = new Gift(giftTypeKey, giftType, resultType);
            }
            giftInstance.setCmds(cmds);
            giftMap.put(giftTypeKey, giftInstance);
            return giftInstance;
        } else {
            return giftMap.get(giftTypeKey);
        }
    }

    /**
     * 从文件中载入CDK
     *
     * @param giftTypeName 激活码类别
     * @param name         文件名称
     */
    public synchronized void loadCDKFromFile(final String giftTypeName, String name) {

        File importFile = plugin.getCustomDataFile("import/" + name + ".txt");
        List<String> cdks = Collections.synchronizedList(new ArrayList<>());
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(importFile), Charset.defaultCharset()));
            String lineText;
            while ((lineText = br.readLine()) != null) {
                synchronized (cdks) {
                    cdks.add(lineText);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    ConfigurationSection cdkData;
                    if(getCdkConfig().isConfigurationSection(giftTypeName)){
                        cdkData = getCdkConfig().getConfigurationSection(giftTypeName);
                    }else{
                        cdkData = getCdkConfig().createSection(giftTypeName);
                    }

                    pluginLogger.sendConsoleMessage("激活码类型[" + giftTypeName + "]导入开始");
                    if (cdks.isEmpty()) {
                        pluginLogger.sendErrorMessage("文件[" + name + "]中没有数据.");
                    } else {
                        CDKCommand.setExportOrImport(true);
                        for (String cdk : cdks) {
                            cdkData.set(cdk, false);
                            pluginLogger.sendConsoleMessage("导入激活码 " + cdk);
                        }
                    }
                    pluginLogger.sendConsoleMessage("激活码类型[" + giftTypeName + "]导入完成");
                    pluginConfigs.saveConfig("cdk");
                    init();
                    CDKCommand.setExportOrImport(false);
                }
            }.runTask(plugin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pluginLogger.sendConsoleMessage("loadCDKFromFile " + giftTypeName + " " + name);
    }

    /**
     * 统一获取cdk配置文件
     * @return cdk配置文件
     */
    private FileConfiguration getCdkConfig() {
        return pluginConfigs.getConfig("cdk");
    }

    /**
     * 把CDK输出到文件中
     *
     * @param fileName 输出的CDK激活码类型名称
     */
    public synchronized void saveCDKToFile(String giftTypeName, String fileName) {
        File exportFile = plugin.getCustomDataFile("export/" + fileName + ".txt");
        CDKCommand.setExportOrImport(true);
        Set<String> cdks = Collections.synchronizedSet(getCdkConfig().getConfigurationSection(giftTypeName).getKeys(false));
        CDKCommand.setExportOrImport(false);
        FileWriter fw;
        PrintWriter pw;
        try {
            fw = new FileWriter(exportFile, false);
            pw = new PrintWriter(fw);
            synchronized (cdks) {
                pluginLogger.sendConsoleMessage("激活码类型[" + giftTypeName + "]导出开始");
                if (cdks.isEmpty()) {
                    pluginLogger.sendErrorMessage("激活码类型[" + giftTypeName + "]中没有数据.");
                } else {
                    for (String cdk : cdks) {
                        pw.println(cdk);
                        pluginLogger.sendConsoleMessage("导出激活码 " + cdk);
                    }
                }
                pw.close();
                fw.close();
                pluginLogger.sendConsoleMessage("激活码类型[" + giftTypeName + "]导出结束");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        pluginLogger.sendConsoleMessage("saveCDKToFile " + fileName);
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

    /**
     * 从内存直接判断启用的激活码类型
     *
     * @param giftTypeName 激活码类型名称
     * @return 如果返回false，则被禁用
     */
    public boolean isEnableByGiftType(String giftTypeName) {
        return enableGift.contains(giftTypeName);
    }

}
