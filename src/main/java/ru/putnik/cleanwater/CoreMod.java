package ru.putnik.cleanwater;

import com.thetorine.thirstmod.core.content.ItemLoader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import static ru.putnik.cleanwater.CoreMod.MODID;

/**
 * Created by My Computer on 26.08.2017.
 */
@Mod(modid = MODID)
public class CoreMod {
    public static final String MODID = "cleanwatermod";
    public static Block cleanMachine;
    public static Fluid cleanWaterFluid;
    public static Block cleanWaterBlock;

    @Mod.Instance(MODID)
    public static CoreMod INSTANCE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        cleanMachine = (new CleanMachine(Material.iron,this));
        GameRegistry.registerBlock(cleanMachine, "cleanMachine");
        LanguageRegistry.addName(cleanMachine,"Очистное сооружение");

        cleanWaterFluid = new Fluid("CleanWaterFluid");
        FluidRegistry.registerFluid(cleanWaterFluid);
        FluidContainerRegistry.registerFluidContainer(cleanWaterFluid,new ItemStack(ItemLoader.freshWaterBucket),
                new ItemStack(Items.bucket));

        cleanWaterBlock = new FreshWaterFluid(cleanWaterFluid, Material.water);
        GameRegistry.registerBlock(cleanWaterBlock, "cleanWaterBlock");
        LanguageRegistry.addName(cleanWaterBlock, "Чистая вода");

        GameRegistry.registerTileEntity(CleanMachineTile.class,MODID+":CleanMachineTile");
        FreshBucketHandler.INSTANCE.buckets.put(cleanWaterBlock, ItemLoader.freshWaterBucket);

        ItemLoader.freshWaterBucket.setTextureName(CoreMod.MODID+":cleanwaterbucket");
    }
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event){
        NetworkRegistry.INSTANCE.registerGuiHandler(this,new CleanMachineGuiHandler());

    }
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(FreshBucketHandler.INSTANCE);
    }
}