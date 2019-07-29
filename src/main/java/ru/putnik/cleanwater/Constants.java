package ru.putnik.cleanwater;

/**
 * Created by My Computer on 21.09.2017.
 */
public class Constants {
    public static final int SlotCount=9;//Количество слотов
    public static final int GuiIDCleanMachine=0;//ID GUI

    public static final int AmountClearWaterForFilter=1000;//кол-во очищенной воды, получаемый за 1 повреждение фильтра 5*1000=5000=5 ведер с одного фильтра
    public static final int AmountCWaterAtATime=40;//Кол-во очищенной воды, получаемой за 1 раз в секунду
    public static final int AmountWaterAbsorbing=60;//Кол-во воды, потребляемой за 1 очищение
    public static final int CapacityDirtWater=10000;//Количество воды, которое может накопить очиститель
    public static final int CapacityClearWater=10000;//Количество очищенной воды, которое может накопить очиститель

    public static final int CapacityEnergy=3000;//Количество энергии, которое может накопить очиститель
    public static final int MaxReceiveEnergy=150;//Максимальное количество энергии, которую можно получить за раз
    public static final int AmountEnergyForOneCleaning=150;//Количество энергии, необходимое для одного очищения
}