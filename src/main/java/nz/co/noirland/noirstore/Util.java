package nz.co.noirland.noirstore;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

public abstract class Util {

    public static ItemStack createItem(String item, String data) {

        Material material = Material.getMaterial(item);
        ItemStack itemStack = new ItemStack(material);
        MaterialData materialData = parseMaterialData(material, data);
        itemStack.setData(materialData);

        return itemStack;

    }

    public static MaterialData parseMaterialData(Material material, String data) {
        MaterialData ret;
        switch(material) {
            case WOOD:
            case SAPLING:
            case LOG:
            case LOG_2:
            case LEAVES:
            case LEAVES_2:
                ret = new Tree(TreeSpecies.valueOf(data));
                break;
            case SANDSTONE:
                ret = new Sandstone(SandstoneType.valueOf(data));
                break;
            case LONG_GRASS:
                ret = new LongGrass(GrassSpecies.valueOf(data));
                break;
            case WOOL:
                ret = new Wool(DyeColor.valueOf(data));
                break;
            //TODO: Change after non-depredated methods are added
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case STAINED_CLAY:
            case CARPET:
                ret = new MaterialData(DyeColor.valueOf(data).getWoolData());
                break;
            case DOUBLE_STEP:
            case STEP:
                ret = new Step(Material.valueOf(data));
                break;
            case SMOOTH_BRICK:
                // STONE for Smooth, MOSSY_COBBLESTONE for mossy, COBBLESTONE for cracked, STONE_BRICK for carved
                ret = new SmoothBrick(Material.valueOf(data));
                break;
            case WOOD_DOUBLE_STEP:
            case WOOD_STEP:
                ret = new WoodenStep(TreeSpecies.valueOf(data));
                break;
            case COAL:
                ret = new Coal(CoalType.valueOf(data));
                break;
            case INK_SACK:
                Dye mat = new Dye();
                mat.setColor(DyeColor.valueOf(data));
                ret = mat;
                break;
            default:
                ret = new MaterialData(material);
                break;
        }
        return ret;
    }

    public static boolean isTradeSign(Block block) {
        if(!isSign(block)) return false;
        Sign sign = (Sign) block.getState();
        return ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase(TradeSign.SIGN_TITLE);
    }

    public static boolean isSign(Block block) {
        return block != null && (block.getState() instanceof Sign);
    }

}
