package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.database.SQLDatabase;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TradeItem {

    private ItemStack item;
    private int amount;
    private int item_id;
    private ArrayList<PriceRange> prices = new ArrayList<PriceRange>();
    private SQLDatabase db = SQLDatabase.inst();

    public TradeItem(int item_id, ItemStack item, int amount, ArrayList<PriceRange> prices) {
        this.item = item;
        this.amount = amount;
        this.prices = prices;
        this.item_id = item_id;
    }


    public ItemStack getItem() {
        return item;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        db.updateItemAmount(item_id, amount);
    }

    public int getId() {
        return item_id;
    }

    public ArrayList<PriceRange> getPrices() {
        return prices;
    }
}
