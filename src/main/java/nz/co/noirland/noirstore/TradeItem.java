package nz.co.noirland.noirstore;

import nz.co.noirland.noirstore.database.SQLDatabase;
import org.bukkit.inventory.ItemStack;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TradeItem {

    private ItemStack item;
    private int amount;
    private int item_id;
    private double sellPercent;
    private PriceCalculator calc;
    private SQLDatabase db = SQLDatabase.inst();

    public static DecimalFormat format = new DecimalFormat("#.##");

    static {
        format.setRoundingMode(RoundingMode.HALF_UP);
    }

    public TradeItem(int item_id, ItemStack item, int amount, PriceCalculator calc, double sellPercent) {
        this.item = item;
        this.amount = amount;
        this.item_id = item_id;
        this.sellPercent = sellPercent;
        this.calc = calc;
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
        return calc.getPrice(amount);
    }

    public double getSellPrice() {
        double price = getPrice();
        return price - (sellPercent * price);
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
