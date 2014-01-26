package nz.co.noirland.noirstore;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class EconManager {

    private static EconManager inst;
    private Economy econ;

    public static EconManager inst() {
        if(inst == null) {
            inst = new EconManager();
        }
        return inst;
    }


    private EconManager() {
        econ = NoirStore.inst().getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    public boolean canWithdraw(Player player, double amount) {
        return econ.has(player.getName(), amount);
    }


    public boolean withdraw(Player player, double amount) {
        if(!canWithdraw(player, amount)) return false;
        EconomyResponse response = econ.withdrawPlayer(player.getName(), amount);
        return response.transactionSuccess();
    }

    public boolean deposit(Player player, double amount) {
        EconomyResponse response = econ.depositPlayer(player.getName(), amount);
        return response.transactionSuccess();
    }

    public long getBalance(Player player) {
        return (long) econ.getBalance(player.getName());
    }

}
