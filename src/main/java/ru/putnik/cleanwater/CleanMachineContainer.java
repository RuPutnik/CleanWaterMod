package ru.putnik.cleanwater;

import buildcraft.core.lib.gui.BuildCraftContainer;
import com.thetorine.thirstmod.core.content.ItemLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionHelper;


/**
 * Created by My Computer on 21.09.2017.
 */
public class CleanMachineContainer extends BuildCraftContainer {
    public CleanMachineTile machineTile;

    public CleanMachineContainer(InventoryPlayer player, CleanMachineTile tile){
        super(Constants.SlotCount);
        machineTile=tile;

        this.addSlotToContainer(new MachineSlot(tile,0,59,24,new ItemStack[]{new ItemStack(ItemLoader.filter)},1));
        this.addSlotToContainer(new MachineSlot(tile,1,59,58,null,64));

        this.addSlotToContainer(new MachineSlot(tile,2,79,24,new ItemStack[]{new ItemStack(ItemLoader.filter)},1));
        this.addSlotToContainer(new MachineSlot(tile,3,79,58,null,64));

        this.addSlotToContainer(new MachineSlot(tile,4,99,24,new ItemStack[]{new ItemStack(ItemLoader.filter)},1));
        this.addSlotToContainer(new MachineSlot(tile,5,99,58,null,64));

        this.addSlotToContainer(new MachineSlot(tile,6,6,40,new ItemStack[]{new ItemStack(Items.water_bucket),new ItemStack(Items.potionitem)},1));
        this.addSlotToContainer(new MachineSlot(tile,7,154,22,new ItemStack[]{new ItemStack(Items.bucket),new ItemStack(Items.glass_bottle)},1));
        this.addSlotToContainer(new MachineSlot(tile,8,154,58,null,4));

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return machineTile.isUseableByPlayer(player);
    }


}
