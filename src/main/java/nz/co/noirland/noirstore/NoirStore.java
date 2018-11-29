package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.config.StoreConfig;
import nz.co.noirland.noirstore.database.StoreDatabase;
import nz.co.noirland.zephcore.Debug;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoirStore extends JavaPlugin {

    private static NoirStore inst;
    private static Map<Location, TradeSign> signs = new HashMap<>();
    private StoreDatabase db;
    private static Debug debug;

    public static NoirStore inst() {
        return inst;
    }

    public static Debug debug() { return debug; }

    @Override
    public void onEnable() {
        inst = this;
        debug = new Debug(this);
        db = StoreDatabase.inst();
        db.checkSchema();

        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginCommand("noirstore").setExecutor(new NoirStoreCommand());

        reload();
    }

    public void reload() {
        StoreConfig.inst().reload();

        NoirStore.signs.clear();
        ArrayList<TradeSign> signs = db.getSigns();
        for(TradeSign sign : signs) {
            addTradeSign(sign, false);
        }
        getLogger().info("Loaded " + signs.size() + " signs.");
    }

    public void addTradeSign(TradeSign sign, boolean addToDB) {
        Material type = sign.getLocation().getBlock().getType();
        if(type != Material.SIGN && type != Material.WALL_SIGN) {
            debug().warning("Found non-existent sign at " + sign.getLocation());
            return;
        }
        if(addToDB) db.addSign(sign);
        signs.put(sign.getLocation(), sign);
    }

    public TradeSign getTradeSign(Location loc) {
        return signs.get(loc);
    }

    public Map<Location, TradeSign> getTradeSigns() {
        return signs;
    }

    public void removeSign(TradeSign sign) {
        signs.remove(sign.getLocation());
        db.removeSign(sign);
    }

    public void sendMessage(CommandSender to, String msg) {
        to.sendMessage(ChatColor.RED + "[NoirStore] " + ChatColor.RESET + msg);
    }

}
