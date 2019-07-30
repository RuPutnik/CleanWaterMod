package ru.putnik.cleanwater;

import buildcraft.BuildCraftFactory;

import com.thetorine.thirstmod.core.content.ItemLoader;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by My Computer on 09.09.2017.
 */
public class FreshBucketHandler {
    public static FreshBucketHandler INSTANCE = new FreshBucketHandler();
    public Map<Block, Item> buckets = new HashMap<>();
    private FreshBucketHandler() {}
    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        World world = event.world;
        MovingObjectPosition pos = event.target;
        Block block = world.getBlock(pos.blockX, pos.blockY, pos.blockZ);
        int meta = world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ);

        if (block == CoreMod.cleanWaterBlock && meta == 0)
        {
            world.setBlockToAir(pos.blockX, pos.blockY, pos.blockZ);
            event.result = new ItemStack(ItemLoader.freshWaterBucket, 1, 0);
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void rightClickWithBucket(PlayerInteractEvent event) {
        int x=event.x;
        int y=event.y;
        int z=event.z;
        World world=event.world;
        EntityPlayer entityPlayer=event.entityPlayer;
        InventoryPlayer inventory=entityPlayer.inventory;
        Block pickBlockPlayer=world.getBlock(event.x,event.y,event.z);//Блок, на который пользователь щелкнул ПКМ с ведром в руке
        ItemStack currentEquippedItem=entityPlayer.getCurrentEquippedItem();

        if(event.action==Action.RIGHT_CLICK_BLOCK){
            if(currentEquippedItem!=null) {
            /*Если блок, на который нажал игрок - не цистерна из buildcraft и нажатие произведено с ведром чистой воды в руке*/
                if ((!pickBlockPlayer.equals(BuildCraftFactory.tankBlock))&&currentEquippedItem.getItem().equals(ItemLoader.freshWaterBucket)){
                     switch (event.face) {
                                case 0: {
                                    y--;
                                    break;
                                }
                                case 1: {
                                    y++;
                                    break;
                                }
                                case 2: {
                                    z--;
                                    break;
                                }
                                case 3: {
                                    z++;
                                    break;
                                }
                                case 4: {
                                    x--;
                                    break;
                                }
                                case 5: {
                                    x++;
                                    break;
                                }
                            }

                            int meta = world.getBlockMetadata(x, y, z);
                            //В креативе вода из вёдер не тратится
                            if (world.getBlock(x, y, z).equals(Blocks.air) || (world.getBlock(x, y, z).equals(CoreMod.cleanWaterBlock) && meta != 0)) {
                                if(!event.entityPlayer.capabilities.isCreativeMode) {
                                    int currentItem = inventory.currentItem;
                                    --inventory.mainInventory[currentItem].stackSize;
                                    inventory.setInventorySlotContents(currentItem, new ItemStack(Items.bucket));
                                }
                                //Если пытаемся ставить в аду - испаряется как и обычная вода
                                if(!world.provider.isHellWorld) {
                                    world.setBlock(x, y, z, CoreMod.cleanWaterBlock);
                                }else{
                                    world.setBlock(x, y, z, Blocks.air);
                                    //Частицы пара
                                    for (int l = 0; l < 8; ++l) {
                                        world.spawnParticle("largesmoke", x+Math.random(), y+Math.random(),
                                                z+Math.random(), 0D, 0D, 0D);
                                    }

                                }
                            }
                        }
                    }
                }
    }
}