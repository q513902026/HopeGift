package me.hope;

import com.google.common.collect.Lists;
import me.hope.commands.*;
import me.hope.core.PluginCommandMap;
import me.hope.core.PluginConfig;
import me.hope.core.PluginLogger;
import me.hope.core.inject.Injector;
import me.hope.core.inject.InjectorBuilder;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * 插件主类
 */
public class HopeGift extends JavaPlugin {
    /**
     * 日志文件实例
     */
    private static PluginLogger pluginLogger;
    /**
     * 插件实例
     */
    private static HopeGift instance;
    /**
     * 配置文件实例
     */
    private static PluginConfig configManager;
    /**
     * 管理命令的注册
     */
    private static PluginCommandMap<HopeGift> adminCommand;
    /**
     * 实例注入
     */
    private static Injector injector;
    /**
     * 对于配置文件的处理实例
     */
    private static Config pluginConfig;

    /**
     * 消息前缀
     */
    private String prefix = "%s: ";

    @Override
    public void onLoad() {
        createFolder();
        registerBeans();
        pluginLogger.sendConsoleMessage("Hope's Injector is running!");
        injector.injectClasses();
    }

    /**
     * 当插件开启时
     */
    @Override
    public void onEnable() {
        pluginLogger.sendConsoleMessage("正在检查依赖的HopeCore版本");
        if (!checkHopeCoreVersion()){
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        pluginLogger.sendConsoleMessage("依赖的HopeCore版本正确.");
        registerCommands();
        configManager.saveAllDefaultConfig();

        pluginConfig.init();
        if(Config.overrideUpdateError){
            pluginLogger.sendErrorMessage("覆盖更新导致错误,正在关闭插件!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        prefix = String.format(prefix,pluginConfig.getPrefix());

        pluginLogger.sendConsoleMessage("加载完成.");
        pluginLogger.sendConsoleMessage("Version: "+this.getDescription().getVersion());
        pluginLogger.sendConsoleMessage("本插件更新不能在运行时进行覆盖文件更新,会导致加载错误.");
    }

    /**
     * 用来保存HopeCore依赖的版本
     */
    private static int[] afterVersion = new int[]{1,0,2};

    /**
     * 用来对比HopeCore的版本是否正确
     * @return
     */
    private boolean checkHopeCoreVersion(){
        Plugin hopeCore = getServer().getPluginManager().getPlugin("HopeCore");
        if(hopeCore == null){
            pluginLogger.sendConsoleMessage("HopeCore不存在");
            return false;
        }
        String verStr = hopeCore.getDescription().getVersion();
        String[] versionsStr = verStr.split("\\.");
        int[] versions = new int[3];
        for (int index = 0; index < versionsStr.length; index++) {
            versions[index] = Integer.parseInt(versionsStr[index]);
        }
        pluginLogger.sendConsoleMessage(String.format("HopeCore 当前版本: %s",verStr));
        pluginLogger.sendConsoleMessage(String.format("依赖 HopeCore 最低版本: %s.%s.%s",afterVersion[0],afterVersion[1],afterVersion[2]));
        if(versions[0] >=afterVersion[0] & versions[1] >=afterVersion[1] & versions[2] >= afterVersion[2]){

            return true;
        }
        return false;
    }

    /**
     * 创建文件夹和示例文件
     */
    private void createFolder() {
        getCustomDataFile("import/cdk_import.txt");
        getCustomDataFile("export/cdk_export.txt");
    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        adminCommand.registerCommand("export",injector.getSingleton(CDKCommand.class));
        adminCommand.registerCommand("import",injector.getSingleton(ImportCommand.class));
        adminCommand.registerCommand("reloadConfig",injector.getSingleton(ReloadConfigCommand.class));
        adminCommand.registerCommand("state",injector.getSingleton(StateCommand.class));
        adminCommand.registerCommand("states",injector.getSingleton(StatesCommand.class));
        adminCommand.registerCommand("enable",injector.getSingleton(EnableCommand.class));
        adminCommand.registerCommand("disable",injector.getSingleton(DisableCommand.class));
        adminCommand.registerCommand("help",injector.getSingleton(HelpCommand.class));

        pluginLogger.sendConsoleMessage("注册管理命令成功");
        this.getCommand("hopegift").setExecutor(adminCommand::onCommand);

        this.getCommand("cdk").setExecutor(injector.getSingleton(CDKCommand.class)::onCommand);
        pluginLogger.sendConsoleMessage("注册CDK命令成功");
    }

    /**
     * 把共享实例存入注入器
     */
    private void registerBeans() {
        injector = new InjectorBuilder().setPlugin(this).setDefaultPath("me.hope").build();

        injector.register(Server.class,getServer());
        injector.register(PluginManager.class,getServer().getPluginManager());

        instance = injector.register(HopeGift.class,this);
        pluginLogger = injector.register(PluginLogger.class,new PluginLogger(this.getLogger(),getCustomDataFile("logs/info.log")));
        pluginLogger.sendConsoleMessage("Enable "+this.getDescription().getFullName());

        adminCommand = injector.register(PluginCommandMap.class,new PluginCommandMap(this));
        configManager = injector.register(PluginConfig.class,new PluginConfig<HopeGift>(this, Lists.newArrayList("config","gift","cdk")));
        configManager.setPluginLogger(pluginLogger);
        pluginConfig  = injector.register(Config.class,new Config());

        pluginLogger.sendConsoleMessage("初始化加载中!");


    }

    /**
     * 当插件关闭时
     */
    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(instance);
        this.getCommand("hopegift").setExecutor(null);
        this.getCommand("cdk").setExecutor(null);
        injector = null;
        instance =  null;
        pluginLogger =  null;

    }

    /**
     * 获取自定义文件
     * @param name 文件名称
     * @return 文件实例，当文件不存在时自动创建文件
     */
    public File getCustomDataFile(String name){
        File file = new File(this.getDataFolder(),name);
        File fileParent = file.getParentFile();
        if(!fileParent.exists()){
            fileParent.mkdirs();
        }
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                this.getLogger().warning("文件创建失败.");
            }
        }
        return file;
    }

    /**
     *  返回消息前缀
     * @return 返回保存的消息前缀变量
     */
    public String getPrefix(){
        return prefix;
    }

    public static Injector getInjector() {
        return injector;
    }
}
