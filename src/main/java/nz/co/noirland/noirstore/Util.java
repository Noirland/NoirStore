package nz.co.noirland.noirstore;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

import java.text.DecimalFormat;

public abstract class Util {

    public static ItemStack createItem(String item, String data) {

        Material material = Material.getMaterial(item);
        MaterialData materialData = parseMaterialData(material, data);
        return materialData.toItemStack();
    }

    @SuppressWarnings("deprecated")
    public static MaterialData parseMaterialData(Material material, String data) {
        try{
            int nRet = Integer.parseInt(data);
            return new MaterialData(material, (byte) nRet);
        }catch(NumberFormatException ignored) {}
        MaterialData ret;
        switch(material) {
            case WOOD:
            case SAPLING:
            case LOG:
            case LEAVES:
                ret = new Tree(TreeSpecies.valueOf(data));
                break;
            //TODO: Change after non-deprecated methods are added
            case LOG_2:
            case LEAVES_2:
                TreeSpecies species = TreeSpecies.valueOf(data);
                switch(species) {
                    case ACACIA:
                    default:
                        ret = new MaterialData(material, (byte) 0x0);
                        break;
                    case DARK_OAK:
                        ret = new MaterialData(material, (byte) 0x1);
                        break;
                }
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
            //TODO: Change after non-deprecated methods are added
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case STAINED_CLAY:
            case CARPET:
                ret = new MaterialData(material, DyeColor.valueOf(data).getWoolData());
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
            case MONSTER_EGG:
                ret = new SpawnEgg(EntityType.valueOf(data));
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

    public static double round(double in, DecimalFormat format) {
        return Double.parseDouble(format.format(in));
    }

}
