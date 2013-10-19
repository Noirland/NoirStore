package nz.co.noirland.noirstore;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public class NoirStore extends JavaPlugin {

    private static NoirStore inst;

    public static NoirStore inst() {
        return inst;
    }

    @Override
    public void onEnable() {
        inst = this;

        Economy econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public void onDisable() {

    }


}
