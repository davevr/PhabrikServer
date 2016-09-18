package com.eweware.phabrik.sim;

import com.eweware.phabrik.DAO.TerrainDAO;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SectorObj;
import com.eweware.phabrik.obj.TerrainObj;

/**
 * Created by davidvronay on 9/5/16.
 */
public class TerrainGenerator {
    static double EARTH_VERTICAL_SIZE = 5;
    public TerrainGenerator() {
        // init
    }

    public TerrainObj GenerateTerrain(PlanetObj thePlanet) {
        double planetCircumference = thePlanet.radius * 2 * Math.PI;
        double sizeRatio = planetCircumference / Constants.EARTH_CIRCUMFERENCE;
        int vSize = (int)(EARTH_VERTICAL_SIZE * sizeRatio);
        if (vSize < 1)
            vSize = 1;
        int hSize = vSize * 2;

        TerrainObj newMap = new TerrainObj();

        newMap.Width = hSize;
        newMap.Height = vSize;
        SectorObj master = new SectorObj();
        master.claimed = false;
        master.highTemp = thePlanet.high_temp;
        master.lowTemp = thePlanet.low_temp;
        master.surfaceType = GetSurfaceType(thePlanet, 0,0);

        TerrainDAO.ClearMap(newMap, master);

        return newMap;
    }

    public SectorObj.SurfaceType GetSurfaceType(PlanetObj thePlanet, int xLoc, int yLoc) {
        SectorObj.SurfaceType newType = SectorObj.SurfaceType.Unknown;

        switch (thePlanet.planetType) {

            case tRock:
            case tVenusian:
            case tAsteroids:
                newType = SectorObj.SurfaceType.Rock;
                break;
            case tUnknown:
                newType = SectorObj.SurfaceType.Unknown;
                break;
            case t1Face:
            case tTerrestrial:
            case tMartian:
                newType = SectorObj.SurfaceType.Dirt;
                break;
            case tWater:
                newType = SectorObj.SurfaceType.Water;
                break;
            case tIce:
                newType = SectorObj.SurfaceType.Ice;
                break;
            case tGasGiant:
            case tSubGasGiant:
            case tSubSubGasGiant:
                newType = SectorObj.SurfaceType.Gas;
                break;
        }

        return newType;
    }
}
