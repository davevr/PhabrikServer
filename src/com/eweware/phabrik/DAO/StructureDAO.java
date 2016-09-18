package com.eweware.phabrik.DAO;

import com.eweware.phabrik.admin.DBHelper;
import com.eweware.phabrik.obj.PointOfPresenceObj;
import com.eweware.phabrik.obj.StructureObj;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by davidvronay on 9/5/16.
 */
public class StructureDAO {
    private static final Logger log = Logger.getLogger(PointOfPresenceDAO.class.getName());

    public static StructureObj CreateFromRS(ResultSet rs) {
        StructureObj newObj = new StructureObj();
        try {

            newObj.Id = rs.getLong("Id");
            // todo - inflate structure

        } catch (SQLException sqlexp) {
            log.log(Level.SEVERE, sqlexp.getMessage());
            newObj = null;
        }



        return newObj;
    }

    public static StructureObj FetchByID(Long objId) {
        StructureObj newObj = null;

        try {

            Connection conn = DBHelper.GetConnection();
            if (conn != null) {

                String queryStr = "SELECT * FROM PhabrikObjects.Structures WHERE Id = ?";
                PreparedStatement statement = DBHelper.PrepareStatement(queryStr, true);
                statement.setLong(1, objId);

                ResultSet newRs = statement.executeQuery();

                if (newRs.next()) {
                    newObj = StructureDAO.CreateFromRS(newRs);
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
}
