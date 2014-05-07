package nz.co.noirland.noirstore;

import nz.co.noirland.zephcore.Util;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public abstract class StoreUtil {

    public static boolean isTradeSign(Block block) {
        if(!Util.isSign(block)) return false;
        Sign sign = (Sign) block.getState();
        return isTradeLine(sign.getLine(0));
    }

    public static boolean isTradeLine(String line) {
        return ChatColor.stripColor(line).equalsIgnoreCase(TradeSign.SIGN_TITLE);
    }
}
