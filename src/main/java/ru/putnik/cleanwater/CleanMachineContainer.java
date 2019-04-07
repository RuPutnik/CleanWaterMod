package ru.putnik.cleanwater;

import buildcraft.core.lib.gui.BuildCraftContainer;
import com.thetorine.thirstmod.core.content.ItemLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


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

        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return machineTile.isUseableByPlayer(player);
    }


}
