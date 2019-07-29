package ru.putnik.cleanwater;

import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.core.lib.gui.GuiBuildCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

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
        Tank tankWater;
        Tank tankClearWater;
        RFBattery battery;
        int heightScaleEnergyTexture;
        if(tile!=null) {
            tankWater = tile.getTankWater();
            tankClearWater = tile.getTankCleanWater();
            battery=tile.getBattery();

            heightScaleEnergyTexture=(int)(((double)battery.getEnergyStored()/battery.getMaxEnergyStored())*42);

            this.drawFluid(tankWater.getFluid(), this.guiLeft + 26, this.guiTop + 20, 16, 58,tankWater.getCapacity());
            this.drawFluid(tankClearWater.getFluid(),this.guiLeft + 135,this.guiTop + 20,16,58,tankClearWater.getCapacity());
            this.drawInventorTexturedModalRect(this.guiLeft + 125,this.guiTop + 77,176,74,5,heightScaleEnergyTexture);
        }
        mc.renderEngine.bindTexture(location);
        this.drawTexturedModalRect(this.guiLeft + 26, this.guiTop + 20, 176, 0, 16, 60);
        this.drawTexturedModalRect(this.guiLeft + 135, this.guiTop + 20, 176, 0, 16, 60);
        this.drawTexturedModalRect(this.guiLeft + 155, this.guiTop + 39, 176, 120, 25, 2*tile.getFillCup());

    }
    //Строит текстуру снизу вверх, то есть инвертивно
    private void drawInventorTexturedModalRect(int guiX, int guiY, int textureFragmentX, int textureFragmentY, int width, int height){
        mc.renderEngine.bindTexture(location);
        float f = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(guiX + width, guiY, this.zLevel, (double)((float)(textureFragmentX + width) * f), (double)((float)(textureFragmentY) * f));
        tessellator.addVertexWithUV(guiX + width, guiY - height, this.zLevel, (double)((float)(textureFragmentX + width) * f), (double)((float)(textureFragmentY + height) * f));
        tessellator.addVertexWithUV(guiX, guiY - height, this.zLevel, (double)((float)(textureFragmentX) * f), (double)((float)(textureFragmentY + height) * f));
        tessellator.addVertexWithUV(guiX, guiY, this.zLevel, (double)((float)(textureFragmentX) * f), (double)((float)(textureFragmentY) * f));
        tessellator.draw();
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String line="Очистное сооружение";
        fontRendererObj.drawString(StatCollector.translateToLocal(line), 50, 6,4210752);

        List<String> waterTip = new ArrayList<>();
        List<String> clearWaterTip = new ArrayList<>();
        List<String> energyTip = new ArrayList<>();

        CleanMachineTile tile = container.machineTile;
        Tank tankWater;
        Tank tankClearWater;
        RFBattery battery;
        if(tile!=null) {
            tankWater = tile.getTankWater();
            tankClearWater = tile.getTankCleanWater();
            battery = tile.getBattery();
            waterTip.add("Вода");
            waterTip.add(tankWater.getFluidAmount()+"/"+tankWater.getCapacity());
            clearWaterTip.add("Очищенная вода");
            clearWaterTip.add(tankClearWater.getFluidAmount()+"/"+tankClearWater.getCapacity());
            energyTip.add("Энергия RF");
            energyTip.add(battery.getEnergyStored()+"/"+battery.getMaxEnergyStored());

            if (x > this.guiLeft + 24 && x < this.guiLeft + 26 + 16 && y > this.guiTop + 18 && y < this.guiTop + 20 + 58) {
                this.drawHoveringText(waterTip, x - this.guiLeft, y - this.guiTop, this.fontRendererObj);
            }
            if (x > this.guiLeft + 133 && x < this.guiLeft + 135 + 16 && y > this.guiTop + 18 && y < this.guiTop + 20 + 58) {
                this.drawHoveringText(clearWaterTip, x - this.guiLeft, y - this.guiTop, this.fontRendererObj);
            }
            if (x > this.guiLeft + 123 && x < this.guiLeft + 125 + 5 && y > this.guiTop + 75 - 42&& y < this.guiTop + 77) {
                this.drawHoveringText(energyTip, x - this.guiLeft, y - this.guiTop, this.fontRendererObj);
            }

        }
    }
}