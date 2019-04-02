package ru.putnik.cleanwater;

import buildcraft.core.lib.fluids.Tank;
import buildcraft.core.lib.gui.GuiBuildCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by My Computer on 21.09.2017.
 */
@SideOnly(Side.CLIENT)
public class CleanMachineGui extends GuiBuildCraft {
    private static ResourceLocation location=new ResourceLocation(CoreMod.MODID+":textures/gui/cleanser.png");
    private CleanMachineContainer container;
    CleanMachineGui(InventoryPlayer inventory, CleanMachineTile testTile){
        super(new CleanMachineContainer(inventory,testTile),null,location);
        container=(CleanMachineContainer)this.inventorySlots;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {
        GL11.glColor4f(1F, 1F, 1F, 1F);

        int q = (width - xSize) / 2;
        int ui = (height - ySize) / 2;

        mc.renderEngine.bindTexture(location);
        drawTexturedModalRect(q, ui, 0, 0, xSize, ySize);

        CleanMachineTile tile = container.machineTile;
        Tank tankWater=null;
        Tank tankClearWater=null;
        if(tile!=null) {
            tankWater = tile.getTankWater();
            tankClearWater = tile.getTankCleanWater();
        }
            this.drawFluid(Objects.requireNonNull(tankWater).getFluid(), this.guiLeft + 26, this.guiTop + 20, 16, 58,tankWater.getCapacity());
            this.drawFluid(Objects.requireNonNull(tankClearWater).getFluid(),this.guiLeft + 135,this.guiTop + 20,16,58,tankClearWater.getCapacity());

        mc.renderEngine.bindTexture(location);
        this.drawTexturedModalRect(this.guiLeft + 26, this.guiTop + 20, 176, 0, 16, 60);
        this.drawTexturedModalRect(this.guiLeft + 135, this.guiTop + 20, 176, 0, 16, 60);

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String line="Очистное сооружение";
        fontRendererObj.drawString(StatCollector.translateToLocal(line), 50, 6,4210752);
        fontRendererObj.drawString("Energy: " + container.machineTile.getBattery().getEnergyStored(), 10, 30, 4210752);
        fontRendererObj.drawString("Dirt water: " + container.machineTile.getTankWater().getFluidAmount(), 10, 50, 4210752);
        fontRendererObj.drawString("Clear water: " + container.machineTile.getTankCleanWater().getFluidAmount(), 10, 70, 4210752);

        /*if (container.machineTile.getTankWater().getFluid() != null && container.machineTile.getTankWater().getFluid().amount > 0) {
            List<String> fluidTip = new ArrayList<>();
            fluidTip.add(container.machineTile.getTankWater().getFluid().getLocalizedName());
            fluidTip.add(String.valueOf(container.machineTile.getTankWater().getFluid().amount));
            if(Mouse.getX()>458&&Mouse.getX()<489&&Mouse.getY()>253&&Mouse.getY()<367) {//Привязать к границам текстуры из метода выше
                System.out.println(true);
                this.drawHoveringText(fluidTip, x - this.guiLeft, y - this.guiTop, this.fontRendererObj);
            }
            //System.out.println(x-this.guiLeft+":"+(y-this.guiTop));
            //Mouse.getX() = x*2
            //Mouse.getY() = y*2 при отсчете снизу
        }*/

    }
}