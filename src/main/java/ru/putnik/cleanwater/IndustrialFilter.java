package ru.putnik.cleanwater;

import com.thetorine.thirstmod.core.main.ThirstMod;
import net.minecraft.item.Item;

/**
 * Создано 30.07.2019 в 23:51
 */
public class IndustrialFilter extends Item {
    public IndustrialFilter() {
        this.setNoRepair();
        this.setUnlocalizedName(CoreMod.MODID+"."+"industrialfilter");
        this.setCreativeTab(ThirstMod.thirstCreativeTab);
        this.setTextureName(CoreMod.MODID+":"+"industrialfilter");
        this.setMaxStackSize(3);
        this.setMaxDamage(14);
    }
}
