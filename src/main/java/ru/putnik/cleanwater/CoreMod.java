package ru.putnik.cleanwater;

import buildcraft.*;
import buildcraft.core.config.BuildCraftConfiguration;
import buildcraft.core.lib.block.BlockBuildCraft;
import buildcraft.core.lib.gui.BuildCraftContainer;
import com.thetorine.thirstmod.core.content.ItemLoader;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import static ru.putnik.cleanwater.CoreMod.DEPENDENCIES;
import static ru.putnik.cleanwater.CoreMod.MODID;

/**
 * Created by My Computer on 26.08.2017.
 */
@Mod(modid = MODID, dependencies = DEPENDENCIES)
public class CoreMod {
    public static final String MODID = "cleanwatermod";
    public static final String DEPENDENCIES="required-after:BuildCraft|Core;required-after:thirstmod";
    public static Block cleanMachine;
    public static Fluid cleanWaterFluid;
    public static Block cleanWaterBlock;
    public static Item cocaCola;

    @Mod.Instance(MODID)
    public static CoreMod INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
            cleanMachine = (new CleanMachine(Material.iron, this));
            GameRegistry.registerBlock(cleanMachine, "cleanMachine");
            LanguageRegistry.addName(cleanMachine, "Очистное сооружение");

            cleanWaterFluid = new Fluid("CleanWaterFluid");
            FluidRegistry.registerFluid(cleanWaterFluid);
            FluidContainerRegistry.registerFluidContainer(cleanWaterFluid, new ItemStack(ItemLoader.freshWaterBucket),
                    new ItemStack(Items.bucket));

            cleanWaterBlock = new FreshWaterFluid(cleanWaterFluid, Material.water);
            GameRegistry.registerBlock(cleanWaterBlock, "cleanWaterBlock");
            LanguageRegistry.addName(cleanWaterBlock, "Чистая вода");

            GameRegistry.registerTileEntity(CleanMachineTile.class, MODID + ":CleanMachineTile");
            FreshBucketHandler.INSTANCE.buckets.put(cleanWaterBlock, ItemLoader.freshWaterBucket);

            ItemLoader.freshWaterBucket.setTextureName(CoreMod.MODID + ":cleanwaterbucket");

            cocaCola=new ColaBottle();
            GameRegistry.registerItem(cocaCola,"cocacola");
            LanguageRegistry.addName(cocaCola,"Кока-кола");
    }
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event){
            NetworkRegistry.INSTANCE.registerGuiHandler(this, new CleanMachineGuiHandler());
    }
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
            MinecraftForge.EVENT_BUS.register(FreshBucketHandler.INSTANCE);

            GameRegistry.addRecipe(new ItemStack(cleanMachine, 1),
                    "#X#", "ZYZ", "WXW",
                    ('#'), GameRegistry.findItem("BuildCraft|Core", "goldGearItem"),
                    ('W'), GameRegistry.findItem("BuildCraft|Core", "diamondGearItem"),
                    ('X'), new ItemStack(GameRegistry.findBlock("BuildCraft|Core", "engineBlock"), 1, 1),
                    ('Y'), Blocks.sticky_piston,
                    ('Z'), GameRegistry.findBlock("BuildCraft|Factory", "tankBlock"));

            GameRegistry.addShapelessRecipe(new ItemStack(cocaCola,1),Items.sugar,Items.sugar,Items.blaze_powder,
                    Items.blaze_powder,Items.fermented_spider_eye,Items.fermented_spider_eye,new ItemStack(Items.dye, 1, 0),
                    new ItemStack(Items.dye, 1, 0),GameRegistry.findItem("thirstmod", "fresh_water"));
    }

}