package nz.co.noirland.noirstore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.text.DecimalFormat;

public class TradeSign {

    private TradeItem item;
    private int sellAmount;
    private Location loc;

    private NoirStore plugin = NoirStore.inst();

    public static String SIGN_TITLE = "[NoirStore]";


    public TradeSign(TradeItem item, int sellAmount, Location loc) {
        this.item = item;
        this.sellAmount = sellAmount;
        this.loc = loc;

        update();

    }

    public TradeItem getItem() {
        return item;
    }

    public int getSellAmount() {
        return sellAmount;
    }

    public Location getLocation() {
        return loc;
    }

    public void update() {
        double price = item.getPrice();
        double sellPrice = item.getSellPrice();
        long amount = item.getAmount();
        Sign sign = getSign();

        if(amount < sellAmount) {
            sign.setLine(0, ChatColor.DARK_RED + SIGN_TITLE);
        }else{
            sign.setLine(0, ChatColor.DARK_GREEN + SIGN_TITLE);
        }
        DecimalFormat format = TradeItem.decFormat;
        sign.setLine(2, "$" + format.format(price*sellAmount) + " : $" + format.format(sellPrice*sellAmount));
        sign.setLine(3, Long.toString(amount));
        sign.update();
    }

    private Sign getSign() {
        BlockState state = loc.getBlock().getState();
        Sign sign = null;
        try {
            sign = (Sign) state;
        } catch(ClassCastException e) {
            plugin.debug("Trade sign at " + loc.toString() + " not a sign!");
        }
        return sign;
    }
}
