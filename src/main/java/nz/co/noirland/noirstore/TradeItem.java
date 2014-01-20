package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.database.SQLDatabase;
import org.bukkit.inventory.ItemStack;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TradeItem {

    private ItemStack item;
    private int amount;
    private int item_id;
    private double sellPercent;
    private ArrayList<PriceRange> prices = new ArrayList<PriceRange>();
    private PriceRange minPrice;
    private PriceRange maxPrice;
    private SQLDatabase db = SQLDatabase.inst();

    public static DecimalFormat decFormat = new DecimalFormat("#.##");

    static {
        decFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    public TradeItem(int item_id, ItemStack item, int amount, ArrayList<PriceRange> prices, double sellPercent) {
        this.item = item;
        this.amount = amount;
        this.prices = prices;
        this.item_id = item_id;
        this.sellPercent = sellPercent;
        minPrice = prices.get(0);
        maxPrice = prices.get(0);
        for(PriceRange price : prices) {
            if(price.getMinAmount() < minPrice.getMinAmount()) {
                minPrice = price;
            }
            if(price.getMaxAmount() > maxPrice.getMaxAmount()) {
                maxPrice = price;
            }
        }
    }


    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        db.updateItemAmount(item_id, amount);
    }

    public int getId() {
        return item_id;
    }

    public double getPrice() {
        for(PriceRange pRange : prices) {
            if(pRange.canCalculate(amount)) {
                return Util.round(pRange.calculatePrice(amount), decFormat);
            }
        }
        if(amount < minPrice.getMinAmount()) {
            return Util.round(minPrice.calculatePrice(minPrice.getMinAmount()), decFormat);
        }
        if(amount > maxPrice.getMaxAmount()) {
            return Util.round(maxPrice.calculatePrice(maxPrice.getMaxAmount()), decFormat);
        }
        NoirStore.inst().debug("Couldn't find a price for " + item.toString());
        return 0;
    }

    public double getSellPrice() {
        double price = getPrice();
        return Util.round(price - (sellPercent * price), decFormat);
    }

    public ArrayList<PriceRange> getPrices() {
        return prices;
    }

}
