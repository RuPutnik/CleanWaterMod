package ru.putnik.cleanwater;

import com.thetorine.thirstmod.core.content.ItemLoader;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by My Computer on 21.09.2017.
 */
public class MachineSlot extends Slot {
    private ItemStack[] itemsValid;
    private int stackLimit;
    public MachineSlot(IInventory inventory, int slotNumber, int xDisplayPosition, int yDisplayPosition,ItemStack[] itemsValid, int stackLimit) {
        super(inventory, slotNumber, xDisplayPosition, yDisplayPosition);
        this.itemsValid=itemsValid;
        this.stackLimit=stackLimit;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        boolean valid=false;
        if(stack!=null){
            if(itemsValid==null){
                return false;
            }else{
                for (ItemStack anItemsValid : itemsValid) {
                    if (anItemsValid.getItem() == stack.getItem()) valid = true;
                }
            }
        }
        return valid;
    }

    @Override
    public int getSlotStackLimit() {
        return stackLimit;
    }
}
