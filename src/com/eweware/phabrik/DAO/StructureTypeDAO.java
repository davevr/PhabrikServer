package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.StructureTypeObj;
import org.joda.time.DateTime;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PointOfPresenceObj;
import com.eweware.phabrik.obj.StructureObj;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Created by Dave on 9/28/2016.
 */
public class StructureTypeDAO {

    private static final Logger log = Logger.getLogger(StructureTypeDAO.class.getName());

    public static StructureTypeObj CreateFromRS(ResultSet rs) {
        StructureTypeObj newObj = new StructureTypeObj();
        try {

            newObj.Id = rs.getLong("Id");
            newObj.structuretype = rs.getString("type");
            newObj.structurename = rs.getString("name");
            newObj.description = rs.getString("description");
            newObj.width = rs.getInt("width");
            newObj.height = rs.getInt("height");
            newObj.ispublic = rs.getBoolean("public");
        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }
        return newObj;
    }



    public static StructureTypeObj FetchByID(Long objId) {
        StructureTypeObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM phabrikobjects.structuretypes WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, objId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = StructureTypeDAO.CreateFromRS(newRs);
                } else {
                    log.log(Level.WARNING, "Structure not found");
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

    public static List<StructureTypeObj> FetchForUser(Long userId) {
        List<StructureTypeObj> theList = new ArrayList<>();

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {
                // todo - check for user known...
                String queryStr = "SELECT * FROM phabrikobjects.structuretypes WHERE public = true";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

                ResultSet newRs = statement.executeQuery();

                while (newRs.next()) {
                    StructureTypeObj newObj = StructureTypeDAO.CreateFromRS(newRs);
                    if (newObj != null)
                        theList.add(newObj);
                }

            } else {
                log.log(Level.SEVERE, "No connection");
            }

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());

        } finally {
            DBHelper.ReleaseConnection();
        }

        return theList;
    }


    public static void InsertNewObjIntoDB(StructureTypeObj newStruct) {
        try {
            String queryStr = "INSERT INTO phabrikobjects.structuretypes (type,name,description,width,height,public )" +
                    " VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);

            statement.setString(1, newStruct.structuretype);
            statement.setString(2, newStruct.structurename);
            statement.setString(3, newStruct.description);
            statement.setInt(4, newStruct.width);
            statement.setInt(5, newStruct.height);
            statement.setBoolean(6, newStruct.ispublic);


            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
                newStruct.Id = rs.getLong(1);
            }
            rs.close();
            statement.close();

        } catch (Exception exp) {
            log.log(Level.SEVERE, exp.getMessage());
        }
    }
}

