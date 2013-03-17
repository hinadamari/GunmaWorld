package hinadamari.gunmaworld;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * GunmaWorld
 * @author hinadamari
 */
public class GunmaWorld extends JavaPlugin
{

	public static Location config_location;
	public static double config_range;
    public final static Logger log = Logger.getLogger("Minecraft");

    private File pluginFolder;
    private File configFile;

    /**
     * プラグイン起動処理
     */
    public void onEnable()
    {

    	pluginFolder = getDataFolder();
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        this.getConfig().options().copyDefaults(true);

        saveConfig();
        loadConfig();

        new GunmaWorldEventListener(this);
        getServer().getPluginManager().registerEvents(new GunmaWorldEventListener(this), this);

        log.info("[GunmaWorld] GunmaArea is enabled!");

    }

    /**
     * プラグイン停止処理
     */
    public void onDisable()
    {
        this.getServer().getScheduler().cancelTasks(this);
    }

    /**
     * コンフィグファイル作成処理
     */
    private void createConfig() {
        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (Exception e) {
                log.info("[GunmaWorld] ERROR: " + e.getMessage());
            }
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                log.info("[GunmaWorld] ERROR: " + e.getMessage());
            }
        }
    }

    /**
     * コンフィグファイル読込処理
     */
    private void loadConfig() {

        config_location = getServer().getWorld(getConfig().getString("world")).getSpawnLocation();
        config_location.setX(getConfig().getDouble("X"));
        config_location.setY(getConfig().getDouble("Y"));
        config_location.setZ(getConfig().getDouble("Z"));
        config_range = getConfig().getDouble("Range");

    }

    /**
     * コマンド呼出時処理
     */
    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("GunmaWorld"))
        {
            if(args.length > 0)
            {
                // コンフィグ再読込
                if(args[0].equalsIgnoreCase("Reload"))
                {
                    if(!sender.hasPermission("gunmaworld.reload"))
                    {
                        sender.sendMessage("You don't have gunmaworld.reload");
                        return true;
                    }
                    this.reloadConfig();
                    loadConfig();
                    sender.sendMessage(ChatColor.GREEN + "GunmaWorld has been reloaded.");
                    return true;
                }
            }
        }
        return false;
    }

    public static Location getConfigLocation(){
        return config_location;
    }

    public static double getConfigRange(){
        return config_range;
    }

}