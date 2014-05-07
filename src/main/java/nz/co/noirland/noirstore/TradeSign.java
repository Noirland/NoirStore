package nz.co.noirland.noirstore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class TradeSign {

    private TradeItem item;
    private int sellAmount;
    private Location loc;

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
        long amount = item.getAmount();
        Sign sign = getSign();

        if(amount < sellAmount) {
            sign.setLine(0, ChatColor.DARK_RED + SIGN_TITLE);
        }else{
            sign.setLine(0, ChatColor.DARK_GREEN + SIGN_TITLE);
        }
        sign.setLine(1, "B: " + item.getFormattedPrice(sellAmount));
        sign.setLine(2, "S: " + item.getFormattedSellPrice(sellAmount));
        sign.setLine(3, ChatColor.DARK_GRAY + "" + getSellAmount() + ChatColor.RESET + " : " + Long.toString(amount));
        sign.update();
    }

    private Sign getSign() {
        BlockState state = loc.getBlock().getState();
        Sign sign = null;
        try {
            sign = (Sign) state;
        } catch(ClassCastException e) {
            NoirStore.debug().debug("Trade sign at " + loc.toString() + " not a sign!");
        }
        return sign;
    }
}
