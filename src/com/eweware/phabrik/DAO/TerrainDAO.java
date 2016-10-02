package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SectorObj;
import com.eweware.phabrik.obj.SunObj;
import com.eweware.phabrik.obj.TerrainObj;
import com.eweware.phabrik.sim.Constants;
import com.sun.org.apache.xml.internal.security.Init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class TerrainDAO {
    private static final Logger log = Logger.getLogger(TerrainDAO.class.getName());

    public static TerrainObj InitTerrain(PlanetObj thePlanet) {
        TerrainObj newTerrain = new TerrainObj();
        int width, height;
        double ratio = thePlanet.radius / Constants.KM_EARTH_RADIUS;

        width = (int)Math.round(10.0 * ratio);
        height = (int)Math.round(5.0 * ratio);

        if (width < 2)
            width = 2;
        if (height < 1)
            height = 1;

        newTerrain.width = width;
        newTerrain.height = height;
        newTerrain.planetId = thePlanet.Id;
        newTerrain._sectorArray = new SectorObj[width][height];

        return newTerrain;
    }

    public static void SetSector(TerrainObj theObj, int w, int h, SectorObj newSect) {
        newSect.xLoc = w;
        newSect.yLoc = h;
        theObj._sectorArray[w][h] = newSect;
    }

    public static void ClearMap(TerrainObj theObj, SectorObj masterSector) {
        for (int curX = 0; curX < theObj.width; curX++) {
            for (int curY = 0; curY < theObj.height; curY++) {
                SectorObj sectorObj = new SectorObj(masterSector);
                SetSector(theObj, curX, curY, sectorObj);
            }
        }
    }

    public static TerrainObj CreateFromRS(ResultSet rs) {
        TerrainObj newObj = new TerrainObj();
        try {
            newObj.Id = rs.getLong("Id");
            newObj.width = rs.getInt("width");
            newObj.height = rs.getInt("height");
            newObj.planetId = rs.getLong("planetid");

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }

        if (newObj != null) {
            // inflate secondary structures
            newObj._sectorArray = new SectorObj[newObj.width][newObj.height];
            SectorDAO.FetchForTerrain(newObj);

        }

        return newObj;
    }


    public static TerrainObj FetchOrCreateForPlanet(PlanetObj planetObj) {
        TerrainObj newObj = FetchByPlanetID(planetObj.Id);

        if (newObj == null) {
            newObj = InitTerrain(planetObj);
            SectorObj masterSector = SectorDAO.CreateMasterSectorFromPlanet(planetObj);
            ClearMap(newObj, masterSector);
            InsertNewObjIntoDB(newObj);
        }

        return newObj;
    }

    public static TerrainObj FetchByPlanetID(Long planetId) {
        TerrainObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.terrain WHERE planetid = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, planetId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = TerrainDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "Terrain not found");
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return newObj;
    }



    public static void InsertNewObjIntoDB(TerrainObj theObj) {
        try {
            List<SectorObj> newSectorList = new ArrayList<>();
            String queryStr = "INSERT INTO phabrikobjects.terrain (width, height, planetid)" +
                    " VALUES (?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setInt(1, theObj.width);
            statement.setInt(2, theObj.height);
            statement.setLong(3, theObj.planetId);

            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                theObj.Id = rs.getLong(1);

                for (int x = 0; x < theObj.width; x++) {
                    for (int y = 0; y < theObj.height; y++) {
                        SectorObj curSector = theObj._sectorArray[x][y];
                        curSector.terrainId = theObj.Id;
                        newSectorList.add(curSector);
                    }

                }
            }
            rs.close();
            statement.close();

            if (newSectorList.size() > 0)
                SectorDAO.InsertNewObjsIntoDB(newSectorList);

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}
