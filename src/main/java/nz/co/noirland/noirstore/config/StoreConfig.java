package nz.co.noirland.noirstore.config;

import nz.co.noirland.noirstore.NoirStore;
import nz.co.noirland.zephcore.Config;
import nz.co.noirland.zephcore.Debug;
import org.bukkit.plugin.Plugin;

public class StoreConfig extends Config {

    private static StoreConfig inst;

    private StoreConfig() {
        super("config.yml");
    }

    public static StoreConfig inst() {
        if(inst == null) {
            inst = new StoreConfig();
        }

        return inst;
    }

    // MySQL
    public String getPrefix()   { return config.getString("noirstore.mysql.prefix", "store_"); }
    public String getDatabase() { return config.getString("noirstore.mysql.database"); }
    public String getUsername() { return config.getString("noirstore.mysql.username"); }
    public String getPassword() { return config.getString("noirstore.mysql.password"); }
    public int    getPort()     { return config.getInt   ("noirstore.mysql.port", 3306); }
    public String getHost()     { return config.getString("noirstore.mysql.host", "localhost"); }

    public int getSellPercent() { return config.getInt("noirstore.sellpercent", 0); }

    public long getTradeDelay() { return config.getInt("noirstore.tradedelay", 0); }


    @Override
    protected Debug getDebug() {
        return NoirStore.debug();
    }

    @Override
    protected Plugin getPlugin() {
        return NoirStore.inst();
    }

    public void reload() {
        load();
    }
}
