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

    public static DecimalFormat format = new DecimalFormat("#.##");

    static {
        format.setRoundingMode(RoundingMode.HALF_UP);
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
                return pRange.calculatePrice(amount);
            }
        }
        if(amount < minPrice.getMinAmount()) {
            return minPrice.calculatePrice(minPrice.getMinAmount());
        }
        if(amount > maxPrice.getMaxAmount()) {
            return maxPrice.calculatePrice(maxPrice.getMaxAmount());
        }
        NoirStore.debug().debug("Couldn't find a price for " + item.toString());
        return 0;
    }

    public double getSellPrice() {
        double price = getPrice();
        return price - (sellPercent * price);
    }

    public ArrayList<PriceRange> getPrices() {
        return prices;
    }

    public String getFormattedPrice(int sellAmount) {
        return formatPrice(getPrice()*sellAmount);
    }
    public String getFormattedSellPrice(int sellAmount) {
        return  formatPrice(getSellPrice()*sellAmount);
    }

    private String formatPrice(double price) {
        DecimalFormat format = TradeItem.format;
        if(price != Math.rint(price)) {
            format = new DecimalFormat("0.00");
            format.setRoundingMode(RoundingMode.HALF_UP);
        }
        String ret = "$" + format.format(price);
        if(ret.length() > 12) {
            ret = "ERROR";
        }
        return ret;
    }

}
