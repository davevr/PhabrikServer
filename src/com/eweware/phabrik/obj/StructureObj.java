package com.eweware.phabrik.obj;

import org.joda.time.DateTime;

/**
 * Created by davidvronay on 9/5/16.
 */
public class StructureObj {

    public enum StructureType {
        Mine,
        Farm,
        Garden,
        Housing,
        Factory,
        School,
        Library,
        SpacePort,
        Storage
    }
    public long Id;
    public SectorObj sector;
    public long sectorId;
    public int xLoc;
    public int yLoc;
    public int xSize;
    public int ySize;
    public int curPop;
    public int maxPop;
    public int curHP;
    public int maxHP;
    public int minPowerNeed;
    public int minPopNeed;
    public int solidStorageSpace;
    public int gasStorageSpace;
    public int foodStorageSpace;
    public int liquidStorageSpace;
    public int energyStorageSpace;
    public int strangeStorageSpace;
    public int maxSolidStorageSpace;
    public int maxGasStorageSpace;
    public int maxFoodStorageSpace;
    public int maxLiquidStorageSpace;
    public int maxEnergyStorageSpace;
    public int maxStrangeStorageSpace;
    public DateTime creationDate;
    public DateTime lastTick;
    public long ownerId;
    public double physicalDefense;
    public double energyDefense;
    public boolean isVacuumSafe;
    public boolean isRadiationSafe;
    public boolean isPublic;
    public StructureType structureType;

    public StructureObj() {
        // empty
        creationDate = new DateTime();
        lastTick = new DateTime();
    }

    public StructureObj(StructureObj master) {
        structureType = master.structureType;
        isPublic = master.isPublic;
        xLoc = master.xLoc;
        yLoc = master.yLoc;
        xSize = master.xSize;
        ySize = master.ySize;
        curPop = master.curPop;
        maxPop = master.maxPop;
        curHP = master.curHP;
        maxHP = master.maxHP;
        minPowerNeed = master.minPowerNeed;
        solidStorageSpace = master.solidStorageSpace;
        gasStorageSpace = master.gasStorageSpace;
        foodStorageSpace = master.foodStorageSpace;
        liquidStorageSpace = master.liquidStorageSpace;
        energyStorageSpace = master.energyStorageSpace;
        strangeStorageSpace = master.strangeStorageSpace;
        maxSolidStorageSpace = master.maxSolidStorageSpace;
        maxGasStorageSpace = master.maxGasStorageSpace;
        maxFoodStorageSpace = master.maxFoodStorageSpace;
        maxLiquidStorageSpace = master.maxLiquidStorageSpace;
        maxEnergyStorageSpace = master.maxEnergyStorageSpace;
        maxStrangeStorageSpace = master.maxStrangeStorageSpace;
        minPopNeed = master.minPopNeed;
        creationDate = new DateTime();
        lastTick = master.lastTick;
        ownerId = master.ownerId;
        physicalDefense = master.physicalDefense;
        energyDefense = master.energyDefense;
        isVacuumSafe = master.isVacuumSafe;
        isRadiationSafe = master.isRadiationSafe;
    }

}
