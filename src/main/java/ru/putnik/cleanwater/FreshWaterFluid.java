package ru.putnik.cleanwater;


import com.thetorine.thirstmod.core.main.ThirstMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import java.util.Date;

/**
 * Created by My Computer on 09.09.2017.
 */
public class FreshWaterFluid extends BlockFluidClassic {
    @SideOnly(Side.CLIENT)
    public IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    public IIcon flowingIcon;
    public FreshWaterFluid(Fluid fluid, Material material) {
        super(fluid, material);
        setCreativeTab(ThirstMod.thirstCreativeTab);
        setDensity(10);//Плотность
        setTickRate(6);//Скорость растекания
        setQuantaPerBlock(8);//Дальность растекания
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return (side == 0 || side == 1)? stillIcon : flowingIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        stillIcon=register.registerIcon(CoreMod.MODID+":cleanwaterstill");
        flowingIcon=register.registerIcon(CoreMod.MODID+":cleanwaterflowing");
        CoreMod.cleanWaterFluid.setIcons(stillIcon,flowingIcon);
    }

    @Override
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.canDisplace(world, x, y, z);
    }

    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        return !world.getBlock(x, y, z).getMaterial().isLiquid() && super.displaceIfPossible(world, x, y, z);
    }
}
