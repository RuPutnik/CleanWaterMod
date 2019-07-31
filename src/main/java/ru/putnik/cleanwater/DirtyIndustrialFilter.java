package ru.putnik.cleanwater;

import com.thetorine.thirstmod.core.main.ThirstMod;
import net.minecraft.item.Item;

/**
 * Создано 31.07.2019 в 12:57
 */
public class DirtyIndustrialFilter extends Item {
    public DirtyIndustrialFilter(){
        this.setNoRepair();
        this.setUnlocalizedName(CoreMod.MODID+"."+"dirtyindustrfilter");
        this.setTextureName(CoreMod.MODID+":"+"dirtyindustrfilter");
        this.setCreativeTab(ThirstMod.thirstCreativeTab);
        this.setMaxStackSize(16);
    }
}
