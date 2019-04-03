package ru.putnik.cleanwater;

import buildcraft.api.power.IRedstoneEngineReceiver;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.core.lib.fluids.TankManager;
import com.thetorine.thirstmod.core.content.ItemLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by My Computer on 09.09.2017.
 */
public class CleanMachineTile extends TileBuildCraft implements ISidedInventory,IPipeConnection,IFluidHandler,IRedstoneEngineReceiver {
    private String inventoryTitle="CleanserInventory";
    private int slotsCount;
    private ItemStack[] inventoryContents;
    private List field_70480_d;
    private boolean hasCustomInventoryName;
    private boolean powerRedEnable;
    private Tank tankWater;
    private Tank tankCleanWater;
    private TankManager<Tank> tankManager;
    private int tempVolumeCleanWater=0;
    private int damageFilter=0;

    private double rateProduction=1;
    private double rateEnergyCost=1;

   public CleanMachineTile(String inventoryTitle, boolean hasCustomInventoryName)
    {
        this.inventoryTitle = inventoryTitle;
        this.hasCustomInventoryName = hasCustomInventoryName;
        init();
    }
    public CleanMachineTile(){
        init();
    }

    private void init(){
        if(this.inventoryContents==null) {
            this.inventoryContents = new ItemStack[Constants.SlotCount];
        }
        this.slotsCount = Constants.SlotCount;
        tankWater=new Tank("tank", Constants.CapacityDirtWater, this);
        tankCleanWater=new Tank("tank2",Constants.CapacityClearWater,this);
        tankManager=new TankManager<>();
        this.tankManager.add(tankWater);
        this.tankManager.add(tankCleanWater);
        this.setBattery(new RFBattery(Constants.CapacityEnergy, Constants.MaxReceiveEnergy, 0));
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return this.isItemValidForSlot(p_102007_1_, p_102007_2_);
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        return this.slotsCount;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if(this.inventoryContents!=null){
            return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
        }else
            return null;

    }

    @Override
    public void updateEntity() {
            super.updateEntity();
            //Если соблюдены условия, раз в секунду очищать воду
            if (!worldObj.isRemote) {
                if (checkCondition()) {
                    if (new Date().getTime() % 1000 < 50) {
                        clearWater();
                        getBattery().setEnergy(getBattery().getEnergyStored()-(int)(Constants.CountEnergyForOneCleaning*rateEnergyCost));

                        calculateRate();
                    }
                }
                worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
                updateContainingBlockInfo();

            }
    }
    //Рассчитываем коэффициенты эффективност и затрат энергии в зависимости от количества запасенной энергии
    // (а косвенно от количество приходящей энергии - для поддержания запаса)
    private void calculateRate(){
        if(getBattery().getEnergyStored()<Constants.CapacityEnergy/3){
            rateProduction=1;
            rateEnergyCost=1;
        }else if(getBattery().getEnergyStored()>=Constants.CapacityEnergy/3&&getBattery().getEnergyStored()<Constants.CapacityEnergy*2/3){
            rateProduction=1.5;
            rateEnergyCost=1.5;
        }else if(getBattery().getEnergyStored()>=Constants.CapacityEnergy*2/3&&getBattery().getEnergyStored()<=Constants.CapacityEnergy){
            rateProduction=2.5;
            rateEnergyCost=5;
        }
    }

    @Override
    public ItemStack decrStackSize(int numberSlot, int p_70298_2_) {
        if (this.inventoryContents[numberSlot] != null){
            ItemStack itemstack;

            if (this.inventoryContents[numberSlot].stackSize <= p_70298_2_){
                itemstack = this.inventoryContents[numberSlot];
                this.inventoryContents[numberSlot] = null;
                this.markDirty();
                return itemstack;
            }
            else{
                itemstack = this.inventoryContents[numberSlot].splitStack(p_70298_2_);

                if(this.inventoryContents[numberSlot].stackSize == 0){
                    this.inventoryContents[numberSlot] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }else{
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int indexSlot) {
        if (this.inventoryContents[indexSlot] != null){
            ItemStack itemstack = this.inventoryContents[indexSlot];
            this.inventoryContents[indexSlot] = null;
            return itemstack;
        }else{
            return null;
        }

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if(this.inventoryContents!=null){
            this.inventoryContents[index] = stack;
            if(stack!=null&&stack.getItem()==ItemLoader.filter){
                damageFilter = stack.getItemDamage();
            }

            if(index==0){
                if (stack != null && stack.stackSize > this.getInventoryStackLimit()){
                    stack.stackSize = this.getInventoryStackLimit();

                }
            }else if(index==1){
                if (stack != null && stack.stackSize > 64) {
                    stack.stackSize = 64;

                }
            }
            this.markDirty();
        }
    }

    @Override
    public String getInventoryName(){
        return this.hasCustomInventoryName() ? this.inventoryTitle : "CleanserInventory";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return this.hasCustomInventoryName;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack stack) {
        if(stack!=null){
            return (stack.getItem() == ItemLoader.filter);
        } else return false;
    }
    @Override
    public void readFromNBT(NBTTagCompound data) {
    super.readFromNBT(data);
    NBTTagList tagList = data.getTagList("Data", 10);
    inventoryContents = new ItemStack[Constants.SlotCount];
    if(data.hasKey("CleanserInventory", 8)){
        this.inventoryTitle = data.getString("CleanserInventory");
    }
    //Последний тег под дополнительные данные, а все до него - под предметы
    for(int i = 0; i < tagList.tagCount()-1; i++){
        NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
        int j = tagCompound.getByte("Slot") & 255;
        setInventorySlotContents(j,ItemStack.loadItemStackFromNBT(tagCompound));
    }
        //Подгружаем дополнительные данные
        NBTTagCompound tagCompound = tagList.getCompoundTagAt(tagList.tagCount()-1);
        powerRedEnable = tagCompound.getBoolean("redPowerEnable");
        tankWater.fill(new FluidStack(FluidRegistry.WATER,tagCompound.getInteger("waterAmount")),true);
        tankCleanWater.fill(new FluidStack(CoreMod.cleanWaterFluid,tagCompound.getInteger("clearWaterAmount")),true);
        damageFilter=tagCompound.getInteger("damageFilter");

        tankWater.readFromNBT(data);
        tankCleanWater.readFromNBT(data);
    }
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound tagCompound;
        for(int i = 0; i < this.inventoryContents.length; i++){
            if (getStackInSlot(i)!= null) {
                tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte) i);//Номер слота
                tagCompound=getStackInSlot(i).writeToNBT(tagCompound);//Сам предмет

                tagList.appendTag(tagCompound);
            }
        }
        tagCompound = new NBTTagCompound();//Тег для дополнительных данных
        tagCompound.setBoolean("redPowerEnable", powerRedEnable);
        tagCompound.setInteger("waterAmount",tankWater.getFluidAmount());
        tagCompound.setInteger("clearWaterAmount",tankCleanWater.getFluidAmount());
        tagCompound.setInteger("damageFilter",damageFilter);
        tagList.appendTag(tagCompound);
        data.setTag("Data", tagList);

        if(this.hasCustomInventoryName()) {
            data.setString("CleanserInventory", this.inventoryTitle);
        }
        tankWater.writeToNBT(data);
        tankCleanWater.writeToNBT(data);
    }

    @Override
    public ConnectOverride overridePipeConnection(IPipeTile.PipeType pipeType, ForgeDirection forgeDirection) {
        if(pipeType!=IPipeTile.PipeType.ITEM) {
            return ConnectOverride.CONNECT;
        }else {
            return ConnectOverride.DISCONNECT;
        }
    }
    //Нужен ли?
    @Override
    public void markDirty(){
        if (this.field_70480_d != null){
            for (int i = 0; i < this.field_70480_d.size(); ++i){
                ((IInventory)this.field_70480_d.get(i)).markDirty();
            }
        }
    }


    public boolean isPowerRedEnable() {
        return powerRedEnable;
    }

    public void setPowerRedEnable(boolean powerRedEnable) {
        this.powerRedEnable = powerRedEnable;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        if(resource!= null && resource.getFluid()!= null) {
            if(resource.getFluid()==FluidRegistry.WATER) {
                return tankWater.fill(resource, doFill);
            } else if(resource.getFluid()==CoreMod.cleanWaterFluid){
                return tankCleanWater.fill(resource, doFill);
            }else
                return 0;
        }else
            return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if(resource==null){
            return null;
        }
        if(tankCleanWater.getFluid()!=null) {
            //Слив из аппарата в трубу?
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
            return tankCleanWater.drain(resource.amount,doDrain);
        }else return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        return tankCleanWater.drain(maxDrain,doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid==FluidRegistry.WATER;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid==CoreMod.cleanWaterFluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return this.tankManager.getTankInfo(from);
    }

    public void litterFilter(int damage,ItemStack[] inventory){
        if(inventory[0].getItem()==ItemLoader.filter) {
            if (damage >= 5) {
                ItemStack dirtyFilter = new ItemStack(ItemLoader.dirtyFilter);
                if (inventory[1]!=null) {
                    inventory[0]=null;
                    ++inventory[1].stackSize;
                }else {
                    inventory[0]=null;
                    inventory[1] = dirtyFilter;
                }

            }
        }
    }
    public boolean checkCondition(){
        if(inventoryContents[0] != null &&(inventoryContents[0].getItem().equals(ItemLoader.filter)||
                inventoryContents[0].getItem().equals(ItemLoader.charcoalFilter))&&checkSlotDirtyFilters(inventoryContents[1])){
            //Если хватает энергии и бак с чистой водой не заполнен
            if(getBattery().getEnergyStored()>Constants.CountEnergyForOneCleaning&&getTankCleanWater().getFluidAmount()<getTankCleanWater().getCapacity()) {
                return tankWater.getFluid() != null && powerRedEnable;
            }else return false;
        }else return false;
    }
    //Проверяем, что слот для загрязненных фильтров не заполнен
    private boolean checkSlotDirtyFilters(ItemStack slot) {
        return slot == null || slot.stackSize < 64;
    }
    public void clearWater() {
        tempVolumeCleanWater+=Constants.VolumeCWaterAtATime;
        if (tempVolumeCleanWater >= Constants.CountClearWaterForFilter) {
            damageFilter++;
            inventoryContents[0].setItemDamage(damageFilter);
            tempVolumeCleanWater = 0;
            litterFilter(damageFilter, inventoryContents);
        }
        tankWater.drain((int)(Constants.CountWaterAbsorbing * rateProduction), true);
        tankCleanWater.fill(new FluidStack(CoreMod.cleanWaterFluid, (int)(Constants.VolumeCWaterAtATime * rateProduction)), true);
    }
    @Override
    public Packet getDescriptionPacket() {
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("countWater",tankWater.getFluidAmount());
        writeToNBT(nbt);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        readFromNBT(packet.func_148857_g());

    }

    @Override
    public boolean canConnectRedstoneEngine(ForgeDirection forgeDirection) {
        return true;
    }

    public Tank getTankWater() {
        return tankWater;
    }

    public Tank getTankCleanWater() {
        return tankCleanWater;
    }
    //Нужны ли эти методы??
    public void func_110134_a(IInvBasic p_110134_1_)
    {
        if (this.field_70480_d == null)
        {
            this.field_70480_d = new ArrayList();
        }

        this.field_70480_d.add(p_110134_1_);
    }
    public void func_110132_b(IInvBasic p_110132_1_)
    {
        this.field_70480_d.remove(p_110132_1_);
    }
    public void func_110133_a(String p_110133_1_)
    {
        this.hasCustomInventoryName = true;
        this.inventoryTitle = p_110133_1_;
    }
}