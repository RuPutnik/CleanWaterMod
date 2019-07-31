package ru.putnik.cleanwater;

import com.thetorine.thirstmod.core.main.ThirstMod;
import net.minecraft.item.Item;

/**
 * Создано 30.07.2019 в 23:52
 */
public class LaboratoryFilter extends Item {
    public LaboratoryFilter() {
        this.setNoRepair();
        this.setUnlocalizedName(CoreMod.MODID+"."+"laboratoryfilter");
        this.setCreativeTab(ThirstMod.thirstCreativeTab);
        this.setTextureName(CoreMod.MODID+":"+"laboratoryfilter");
        this.setMaxStackSize(6);
        this.setMaxDamage(39);
    }
}
