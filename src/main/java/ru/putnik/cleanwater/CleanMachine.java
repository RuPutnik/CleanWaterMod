package ru.putnik.cleanwater;

/**
 * Created by My Computer on 26.08.2017.
 */
import com.thetorine.thirstmod.core.main.ThirstMod;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

public class CleanMachine extends BlockContainer implements ITileEntityProvider {
    private final Random random = new Random();

    private static CoreMod instance;
    public CleanMachine(Material material, CoreMod instanceMod)
    {
        super(material);
        setCreativeTab(ThirstMod.thirstCreativeTab);
        setBlockName(CoreMod.MODID + "." + "cleanMachine");
        setHardness(2.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe",3);
        setStepSound(Block.soundTypeMetal);
        setBlockTextureName(CoreMod.MODID+":cleanser");

        instance = instanceMod;
    }
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int par1, float par2, float par3, float par4) {
        if (!player.isSneaking()) {
            player.openGui(instance, Constants.GuiIDCleanMachine, world, x, y, z);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(CoreMod.cleanMachine);
    }
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new CleanMachineTile("CleanMachineInventory", true);
    }
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_) {
        CleanMachineTile entity = (CleanMachineTile) world.getTileEntity(x, y, z);

        if (entity != null) {
            ItemStack itemStack = entity.getStackInSlot(0);

            if (itemStack != null) {
                float f = this.random.nextFloat() * 0.8F + 0.1F;
                float f1 = this.random.nextFloat() * 0.8F + 0.1F;
                float f2 = this.random.nextFloat() * 0.8F + 0.1F;

                while (itemStack.stackSize > 0) {
                    int j1 = this.random.nextInt(21) + 10;

                    if (j1 > itemStack.stackSize) {
                        j1 = itemStack.stackSize;
                    }

                    itemStack.stackSize -= j1;
                    EntityItem entityItem = new EntityItem(world, (double) ((float) x + f), (double) ((float) y + f1),
                            (double) ((float) z + f2), new ItemStack(itemStack.getItem(), j1, itemStack.getItemDamage()));

                    if (itemStack.hasTagCompound()) {
                        entityItem.getEntityItem().setTagCompound((NBTTagCompound) itemStack.getTagCompound().copy());
                    }


                    float f3 = 0.05F;
                    entityItem.motionX = (double) ((float) this.random.nextGaussian() * f3);
                    entityItem.motionY = (double) ((float) this.random.nextGaussian() * f3 + 0.2F);
                    entityItem.motionZ = (double) ((float) this.random.nextGaussian() * f3);
                    world.spawnEntityInWorld(entityItem);
                }
            }


            world.func_147453_f(x, y, z, block);
        }

    }
    @SideOnly(Side.CLIENT)
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
        if(!world.isRemote) {
            CleanMachineTile tile=(CleanMachineTile)world.getTileEntity(x,y,z);
            if (tile.isPowerRedEnable() && !world.isBlockIndirectlyGettingPowered(x, y, z))
            {
                tile.setPowerRedEnable(false);
            }
            else if (!tile.isPowerRedEnable() && world.isBlockIndirectlyGettingPowered(x, y, z))
            {
                tile.setPowerRedEnable(true);
            }

        }
    }


}