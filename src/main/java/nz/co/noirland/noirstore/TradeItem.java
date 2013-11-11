package nz.co.noirland.noirstore;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TradeItem {

    private ItemStack item;
    private long amount;
    private ArrayList<PriceRange> prices = new ArrayList<PriceRange>();

    public TradeItem(ItemStack item, long amount, ArrayList<PriceRange> prices) {
        this.item = item;
        this.amount = amount;
        this.prices = prices;
    }


    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
