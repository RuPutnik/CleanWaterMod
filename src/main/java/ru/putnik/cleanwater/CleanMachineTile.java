package ru.putnik.cleanwater;

import buildcraft.api.power.IRedstoneEngineReceiver;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.core.lib.fluids.TankManager;
import com.thetorine.thirstmod.core.content.ItemLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInvBasic;
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
    private int slotsCount=Constants.SlotCount;
    private ItemStack[] inventoryContents;
    private List field_70480_d;
    private Tank tankWater;
    private Tank tankCleanWater;
    private TankManager<Tank> tankManager;
    private int tempVolumeCleanWater=0;
    private double damageFilters[]=new double[]{0,0,0};

    private double rateProduction=1;
    private double rateEnergyCost=1;

    private int fillCup=0;

    public CleanMachineTile(){
        if(this.inventoryContents==null) {
            this.inventoryContents = new ItemStack[Constants.SlotCount];
        }
        tankWater=new Tank("tankWater", Constants.CapacityDirtWater, this);
        tankCleanWater=new Tank("tankCleanWater",Constants.CapacityClearWater,this);
        tankManager=new TankManager<>();
        this.tankManager.add(tankWater);
        this.tankManager.add(tankCleanWater);
        this.setBattery(new RFBattery(Constants.CapacityEnergy, Constants.MaxReceiveEnergy, 0));
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int indexSlot) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int indexSlot, ItemStack stack, int direction) {
        return this.isItemValidForSlot(indexSlot, stack);
    }

    @Override
    public boolean canExtractItem(int indexSlot, ItemStack itemStackIn, int direction) {
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
                        int countWorkFilter=getCountWorkFiler();
                        clearWater(countWorkFilter);
                        getBattery().setEnergy(getBattery().getEnergyStored()-(int)(Constants.AmountEnergyForOneCleaning *rateEnergyCost));

                        calculateRate();
                    }
                }
                worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
                updateContainingBlockInfo();

            }
    }
    //Рассчитываем коэффициенты эффективности и затрат энергии в зависимости от количества запасенной энергии
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
    public ItemStack decrStackSize(int numberSlot, int amount) {
        if (this.inventoryContents[numberSlot] != null){
            ItemStack itemstack;

            if (this.inventoryContents[numberSlot].stackSize <= amount){
                itemstack = this.inventoryContents[numberSlot];
                this.inventoryContents[numberSlot] = null;
                this.markDirty();
                return itemstack;
            }else{
                itemstack = this.inventoryContents[numberSlot].splitStack(amount);

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

    @Override//Когда кладем предмет в некоторый слот
    public void setInventorySlotContents(int index, ItemStack stack) {
        if(this.inventoryContents!=null){
            this.inventoryContents[index] = stack;
            if(stack!=null&&stack.getItem()==ItemLoader.filter){
                damageFilters[index/2] = stack.getItemDamage();//если фильтр в 0 слоте, то повреждение фильтра в 0, если 2, то повреждения в 1, если 4, то во 2
            }

            if(index==0||index==2||index==4){
                if (stack != null && stack.stackSize > this.getInventoryStackLimit()){
                    stack.stackSize = this.getInventoryStackLimit();
                }
            }else if(index==1||index==3||index==5){
                if (stack != null && stack.stackSize > 64) {
                    stack.stackSize = 64;
                }
            }else if(index==6&&stack!=null){
                if (stack.getItem().equals(Items.water_bucket)) {
                    tankWater.fill(new FluidStack(FluidRegistry.WATER,1000),true);
                    inventoryContents[6]=new ItemStack(Items.bucket);
                }else if(stack.getItem().equals(Items.potionitem)){
                    tankWater.fill(new FluidStack(FluidRegistry.WATER,500),true);
                    inventoryContents[6]=new ItemStack(Items.glass_bottle);
                }
            }else if(index==7&&stack!=null){
                new Thread(() -> {
                    boolean ac=false;
                    while (!ac) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!worldObj.isRemote) {
                            if (stack.getItem().equals(Items.bucket)) {
                                if (inventoryContents[8] == null) {
                                    if (tankCleanWater.getFluid().amount >= 1000) {

                                        tankCleanWater.drain(1000, true);
                                        inventoryContents[7] = null;

                                        updateProcessFillCup(300);
                                        inventoryContents[8] = new ItemStack(ItemLoader.freshWaterBucket);
                                        ac=true;
                                    }
                                }
                            }else if(stack.getItem().equals(Items.glass_bottle)){
                                    if (tankCleanWater.getFluid().amount >= 500) {
                                        if (inventoryContents[8] == null) {
                                            tankCleanWater.drain(500, true);
                                            inventoryContents[7]=null;
                                            updateProcessFillCup(150);
                                            inventoryContents[8] = new ItemStack(ItemLoader.freshWater);
                                            ac=true;
                                        } else if(inventoryContents[8].getItem().equals(ItemLoader.freshWater)){
                                            if (inventoryContents[8].stackSize < 4) {
                                                tankCleanWater.drain(500, true);
                                                inventoryContents[7]=null;
                                                updateProcessFillCup(150);
                                                inventoryContents[8].stackSize++;
                                                ac=true;
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }).start();
                }
            }
            this.markDirty();
        }
    private void updateProcessFillCup(int timeOneStage){
        for (int a = 0; a < 11; a++) {
            try {
                fillCup = a;
                markDirty();
                Thread.sleep(timeOneStage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fillCup=0;
            markDirty();
        }
    }

    @Override
    public String getInventoryName(){
        return this.hasCustomInventoryName() ? this.inventoryTitle : "CleanserInventory";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
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
    public boolean isItemValidForSlot(int indexSlot, ItemStack stack) {
        if(stack!=null){
            return (stack.getItem() == ItemLoader.filter);
        } else return false;
    }
    @Override
    public void readFromNBT(NBTTagCompound data) {
    super.readFromNBT(data);
    NBTTagList tagList = data.getTagList("Data", 10);
    inventoryContents = new ItemStack[slotsCount];
    if(data.hasKey("CleanserInventory", 8)){
        this.inventoryTitle = data.getString("CleanserInventory");
    }
    //Данные слотов
    for(int i = 0; i < tagList.tagCount(); i++){
        NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
        int j = tagCompound.getByte("Slot") & 255;//Индекс слота
        setInventorySlotContents(j,ItemStack.loadItemStackFromNBT(tagCompound));
    }

        tankWater.readFromNBT(data);
        tankCleanWater.readFromNBT(data);

        fillCup=data.getInteger("cupFill");
    }
    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound tagCompound;
        for(int i = 0; i < slotsCount; i++){
            if (getStackInSlot(i)!= null) {
                tagCompound = new NBTTagCompound();
                tagCompound.setByte("Slot", (byte) i);//Индекс слота
                tagCompound=getStackInSlot(i).writeToNBT(tagCompound);//Предмет в слоте

                tagList.appendTag(tagCompound);
            }
        }
        data.setTag("Data", tagList);

        if(this.hasCustomInventoryName()) {
            data.setString("CleanserInventory", this.inventoryTitle);
        }
        tankWater.writeToNBT(data);
        tankCleanWater.writeToNBT(data);

        data.setInteger("cupFill",fillCup);
    }

    @Override
    public ConnectOverride overridePipeConnection(IPipeTile.PipeType pipeType, ForgeDirection forgeDirection) {
        if(pipeType!=IPipeTile.PipeType.ITEM) {
            return ConnectOverride.CONNECT;
        }else {
            return ConnectOverride.DISCONNECT;
        }
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

    private void litterFilter(double damages[],ItemStack[] inventory){
        for(int n=0;n<=4;n=n+2){
            int n1=0;
            if(inventory[n]!=null&&inventory[n].getItem()==ItemLoader.filter) {
                if (parseDoubleDamageToInt(damages[n1])>= 5) {
                    ItemStack dirtyFilter = new ItemStack(ItemLoader.dirtyFilter);
                    if (inventory[n+1]!=null) {
                        inventory[n]=null;
                        ++inventory[1].stackSize;
                    }else {
                        inventory[n]=null;
                        inventory[n+1] = dirtyFilter;
                    }
                    markDirty();
                }
            }
            n1++;
        }

    }
    private boolean checkCondition(){
        if(checkSlotCondition()){
            //Если хватает энергии и бак с чистой водой не заполнен
            if(getBattery().getEnergyStored()>Constants.AmountEnergyForOneCleaning &&getTankCleanWater().getFluidAmount()<getTankCleanWater().getCapacity()) {
                return tankWater.getFluid() != null && worldObj.isBlockIndirectlyGettingPowered(xCoord,yCoord,zCoord);
            }else return false;
        }else return false;
    }
    private boolean checkSlotCondition(){
        boolean result=false;

        if(inventoryContents[0] != null &&(inventoryContents[0].getItem().equals(ItemLoader.filter))&&checkSlotDirtyFilters(inventoryContents[1])) result=true;
        else if(inventoryContents[2] != null &&(inventoryContents[2].getItem().equals(ItemLoader.filter))&&checkSlotDirtyFilters(inventoryContents[3])) result=true;
        else if(inventoryContents[4] != null &&(inventoryContents[4].getItem().equals(ItemLoader.filter))&&checkSlotDirtyFilters(inventoryContents[5])) result=true;


        return result;
    }
    //Проверяем, что слот для загрязненных фильтров не заполнен
    private boolean checkSlotDirtyFilters(ItemStack slot) {
        return slot == null || slot.stackSize < 64;
    }
    private void clearWater(int countWorkFiler) {
        tempVolumeCleanWater+=Constants.AmountCWaterAtATime;
        if (tempVolumeCleanWater >= Constants.AmountClearWaterForFilter) {
            double sizeDamage=1.0/countWorkFiler;
            if(inventoryContents[0]!=null){
                damageFilters[0]=damageFilters[0]+sizeDamage;
                inventoryContents[0].setItemDamage(parseDoubleDamageToInt(damageFilters[0]));
            }
            if(inventoryContents[2]!=null){
                damageFilters[1]=damageFilters[1]+sizeDamage;
                inventoryContents[2].setItemDamage(parseDoubleDamageToInt(damageFilters[1]));
            }
            if(inventoryContents[4]!=null){
                damageFilters[2]=damageFilters[2]+sizeDamage;
                inventoryContents[4].setItemDamage(parseDoubleDamageToInt(damageFilters[2]));
            }
            tempVolumeCleanWater = 0;
            litterFilter(damageFilters, inventoryContents);
        }
        tankWater.drain((int)(Constants.AmountWaterAbsorbing * rateProduction), true);
        tankCleanWater.fill(new FluidStack(CoreMod.cleanWaterFluid, (int)(Constants.AmountCWaterAtATime * rateProduction)), true);
        markDirty();
    }
    private int getCountWorkFiler(){
        int count=0;

        if(inventoryContents[0]!=null) count++;
        if(inventoryContents[2]!=null) count++;
        if(inventoryContents[4]!=null) count++;

        return count;
    }
    private int parseDoubleDamageToInt(double dmg){
        int ip;//целая часть
        double fp;//дробная часть
        ip=(int)dmg;
        fp=dmg-ip;

        if(fp>0.9&&fp<1) dmg=ip+1;//0.333+0.333+0.333=0.999
        if(fp>0.1&&fp<0.2) dmg=ip;//0.333+0.5+0.333=1.166


        return (int)dmg;
    }
    @Override
    public Packet getDescriptionPacket() {
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        NBTTagCompound nbt = new NBTTagCompound();
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

    public int getFillCup() {
        return fillCup;
    }
}
