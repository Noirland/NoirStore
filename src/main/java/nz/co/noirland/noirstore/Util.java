package nz.co.noirland.noirstore;

import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;

public abstract class Util {

    public static ItemStack createItem(String item, String data) {

        Material material = Material.getMaterial(item);
        ItemStack itemStack = new ItemStack(material);
        MaterialData materialData;

        switch(material) {
            case WOOD:
            case SAPLING:
            case LOG:
            case LEAVES:
                materialData = new Tree(TreeSpecies.valueOf(data));
                break;
            case SANDSTONE:
                materialData = new Sandstone(SandstoneType.valueOf(data));
                break;
            case LONG_GRASS:
                materialData = new LongGrass(GrassSpecies.valueOf(data));
                break;
            case WOOL:
                materialData = new Wool(DyeColor.valueOf(data));
                break;
            case DOUBLE_STEP:
            case STEP:
                materialData = new Step(Material.valueOf(data));
                break;
            case SMOOTH_BRICK:
                // STONE for Smooth, MOSSY_COBBLESTONE for mossy, COBBLESTONE for cracked, SMOOTH_STONE for carved
                materialData = new SmoothBrick(Material.valueOf(data));
                break;
            case WOOD_DOUBLE_STEP:
            case WOOD_STEP:
                materialData = new WoodenStep(TreeSpecies.valueOf(data));
                break;
            case COAL:
                materialData = new Coal(CoalType.valueOf(data));
                break;
            case INK_SACK:
                Dye mat = new Dye();
                mat.setColor(DyeColor.valueOf(data));
                materialData = mat;
                break;
            default:
                materialData = new MaterialData(material);
                break;
        }

        itemStack.setData(materialData);

        return itemStack;

    }

    public static PriceRange parseRange(String range, double buy, double sell) {

        int lower = 0;
        int upper = 0;

        if(range.equals("max") || range.equals("min")) return null;

        if(range.contains("-")) {
            String[] bounds = range.split("-");
            lower = Integer.parseInt(bounds[0]);
            upper = Integer.parseInt(bounds[1]);
        }else{
            try {
                upper = Integer.parseInt(range);
                lower = upper;
            }catch(NumberFormatException e) {
                return null;
            }
        }

        return new PriceRange(lower, upper, buy, sell);
    }

}
