package com.eweware.phabrik.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidvronay on 9/5/16.
 */
public class SectorObj {

    public enum SurfaceType {
        Rock,
        Ice,
        Gas,
        Dirt,
        Water,
        Grass,
        Unknown
    }

    public long Id;
    public double lowTemp;
    public double highTemp;
    public SurfaceType surfaceType;
    public List<StructureObj>   structures;
    public long ownerId;
    public boolean claimed;
    public int xLoc;
    public int yLoc;
    public long terrainId;
    public String sectorUrl;

    public SectorObj() {
        structures = new ArrayList<>();
        ownerId = 0;
        claimed = false;
        surfaceType = SurfaceType.Unknown;

    }

    public SectorObj(SectorObj master) {
        lowTemp = master.lowTemp;
        highTemp = master.highTemp;
        surfaceType = master.surfaceType;
        ownerId = master.ownerId;
        claimed = master.claimed;

        for (StructureObj curStruct : master.structures) {
            StructureObj newStruct = new StructureObj(curStruct);
            newStruct.sector = this;
            structures.add(newStruct);
        }

    }

}
