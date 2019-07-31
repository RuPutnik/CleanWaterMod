package ru.putnik.cleanwater;

import com.thetorine.thirstmod.core.content.ItemDrink;
import com.thetorine.thirstmod.core.content.ItemInternalDrink;
import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.player.PlayerContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Создано 30.07.2019 в 21:04
 */
public class ColaBottle extends ItemInternalDrink {
    public ColaBottle() {
        super(12,5,0,CoreMod.MODID+":cocacola",4);
        setUnlocalizedName(CoreMod.MODID+"."+"cocacola");
        returnItem=Items.glass_bottle;
    }
    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            --stack.stackSize;
            PlayerContainer playerContainer = PlayerContainer.getPlayer(player);
            playerContainer.addStats(this.thirstHeal, 5);

            player.curePotionEffects(new ItemStack(Items.milk_bucket));

            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id,250));
            player.addPotionEffect(new PotionEffect(Potion.regeneration.id,350));
            player.addPotionEffect(new PotionEffect(Potion.hunger.id,200));
            player.inventory.addItemStackToInventory(new ItemStack(this.returnItem));
        }

        return stack;
    }

}
