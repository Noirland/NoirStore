package nz.co.noirland.noirstore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.math.BigDecimal;

public class TradeSign {

    private Material material;
    private int sellAmount;
    private Location loc;
    private BigDecimal price;

    public static String SIGN_TITLE = "[NoirStore]";


    public TradeSign(Material material, int sellAmount, Location loc, BigDecimal price) {
        this.sellAmount = sellAmount;
        this.loc = loc;
        this.material = material;
        this.price = price;

        update();
        NoirStore.debug().warning(material.toString());
    }

    public int getSellAmount() {
        return sellAmount;
    }

    public Location getLocation() {
        return loc;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public Material getMaterial() {
        return material;
    }

    public void update() {
        Sign sign = getSign();

        sign.setLine(0, ChatColor.DARK_GREEN + SIGN_TITLE);
        sign.setLine(1, Integer.toString(getSellAmount()));
        sign.setLine(2, StoreUtil.format(getMaterial()));
        sign.setLine(3, "S: " + StoreUtil.formatPrice(getPrice()));
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
