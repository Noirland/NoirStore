package nz.co.noirland.noirstore;

import nz.co.noirland.zephcore.Util;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public abstract class StoreUtil {

    public static MathContext ROUND_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

    public static boolean isTradeSign(Block block) {
        if(!Util.isSign(block)) return false;
        Sign sign = (Sign) block.getState();
        return isTradeLine(sign.getLine(0));
    }

    public static boolean isTradeLine(String line) {
        return ChatColor.stripColor(line).equalsIgnoreCase(TradeSign.SIGN_TITLE);
    }

    public static String formatPrice(BigDecimal price) {
        price = price.setScale(2, RoundingMode.HALF_UP);
        String ret = "$" + price.toPlainString();
        if(ret.length() > 12) {
            ret = "ERROR";
        }
        return ret;
    }

    public static String formatPrice(BigDecimal price, int amount) {
        return formatPrice(multPrice(price, amount));
    }

    public static BigDecimal multPrice(BigDecimal price, int amount) {
        BigDecimal sellAmount = BigDecimal.valueOf(amount, 2);
        return price.multiply(sellAmount);
    }

    public static String format(Material material) {
        String ret = material.toString();
        ret = ret.replace("_", " ");
        ret = ret.toLowerCase();
        return WordUtils.capitalize(ret);
    }
}
