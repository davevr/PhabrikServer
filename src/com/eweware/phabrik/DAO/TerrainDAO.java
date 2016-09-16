package com.eweware.phabrik.DAO;

import com.eweware.phabrik.obj.SectorObj;
import com.eweware.phabrik.obj.TerrainObj;

/**
 * Created by davidvronay on 9/5/16.
 */
public class TerrainDAO {

    public static TerrainObj InitTerrain(int width, int height) {
        TerrainObj newTerrain = new TerrainObj();
        newTerrain.Width = width;
        newTerrain.Height = height;

        newTerrain._sectorArray = new SectorObj[width][height];

        return newTerrain;
    }

    public static void SetSector(TerrainObj theObj, int w, int h, SectorObj newSect) {
        theObj._sectorArray[w][h] = newSect;
    }

    public static void ClearMap(TerrainObj theObj, SectorObj masterSector) {
        for (int curX = 0; curX < theObj.Width; curX++) {
            for (int curY = 0; curY < theObj.Height; curY++) {
                SectorObj sectorObj = new SectorObj(masterSector);
                SetSector(theObj, curX, curY, sectorObj);
            }
        }

        InsertNewObjIntoDB(theObj);
    }

    public static void InsertNewObjIntoDB(TerrainObj theObj) {

    }
}
