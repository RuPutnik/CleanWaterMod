package ru.putnik.cleanwater;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static ru.putnik.cleanwater.Constants.GuiIDCleanMachine;

/**
 * Created by My Computer on 21.09.2017.
 */
public class CleanMachineGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GuiIDCleanMachine: {
                if (world.getBlock(x, y, z).equals(CoreMod.cleanMachine)) {
                    return new CleanMachineContainer(player.inventory, (CleanMachineTile)world.getTileEntity(x,y,z));
                }
            }
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GuiIDCleanMachine: {
                if (world.getBlock(x, y, z).equals(CoreMod.cleanMachine))
                    return new CleanMachineGui(player.inventory, (CleanMachineTile)world.getTileEntity(x,y,z));
            }

        }
        return null;
    }
}
