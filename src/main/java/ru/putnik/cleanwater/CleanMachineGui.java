package ru.putnik.cleanwater;

import buildcraft.core.lib.gui.GuiBuildCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * Created by My Computer on 21.09.2017.
 */
@SideOnly(Side.CLIENT)
public class CleanMachineGui extends GuiBuildCraft {
    private static ResourceLocation location=new ResourceLocation(CoreMod.MODID+":textures/gui/cleanser.png");
    private CleanMachineContainer container;
    public CleanMachineGui(InventoryPlayer inventory, CleanMachineTile testTile){
        super(new CleanMachineContainer(inventory,testTile),(IInventory)null,location);
        container=(CleanMachineContainer)this.inventorySlots;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {
        GL11.glColor4f(1F,1F,1F,1F);
        mc.renderEngine.bindTexture(location);
        int q=(width-xSize)/2;
        int ui=(height-ySize)/2;
        drawTexturedModalRect(q,ui,0,0,xSize,ySize);


    }
    //TO DO
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String line="Очистное сооружение";
        fontRendererObj.drawString(StatCollector.translateToLocal(line), 50, 6,4210752);
        fontRendererObj.drawString("Energy: " + container.machineTile.getBattery().getEnergyStored(), 10, 30, 4210752);
        fontRendererObj.drawString("Dirt water: " + container.machineTile.getTankWater().getFluidAmount(), 10, 50, 4210752);
        fontRendererObj.drawString("Clear water: " + container.machineTile.getTankCleanWater().getFluidAmount(), 10, 70, 4210752);
    }

}