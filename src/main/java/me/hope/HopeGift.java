package me.hope;

import com.google.common.collect.Lists;
import me.hope.commands.CDKCommand;
import me.hope.commands.ExportCommand;
import me.hope.commands.ImportCommand;
import me.hope.commands.ReloadConfigCommand;
import me.hope.core.PluginCommandMap;
import me.hope.core.PluginConfig;
import me.hope.core.PluginLogger;
import me.hope.core.inject.Injector;
import me.hope.core.inject.InjectorBuilder;
import org.bukkit.Server;
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
    private String prefix = " %s: ";

    /**
     * 当插件开启时
     */
    @Override
    public void onEnable() {

        registerBeans();
        registerCommands();

        pluginLogger.sendConsoleMessage("Hope's Injector is running!");
        injector.injectClasses();

        configManager.saveAllDefaultConfig();

        prefix = String.format(prefix,pluginConfig.getPrefix());

        pluginConfig.init();
        pluginLogger.sendConsoleMessage("加载完成.");
        pluginLogger.sendConsoleMessage("Version: "+this.getDescription().getVersion());

    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        adminCommand.registerCommand("export",new ExportCommand());
        adminCommand.registerCommand("import",new ImportCommand());
        adminCommand.registerCommand("reloadConfig",new ReloadConfigCommand());
        pluginLogger.sendConsoleMessage("注册管理命令成功");
        this.getCommand("hopegift").setExecutor(adminCommand::onCommand);

        this.getCommand("cdk").setExecutor(new CDKCommand()::onCommand);
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

        pluginLogger.sendConsoleMessage("Config Loader!");


    }

    /**
     * 当插件关闭时
     */
    @Override
    public void onDisable() {
        instance =  null;
        pluginLogger =     null;
        this.getCommand("hopegift").setExecutor(null);
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
}