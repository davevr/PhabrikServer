package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.PlanetDAO;
import com.eweware.phabrik.DAO.TerrainDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.TerrainObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Dave on 9/23/2016.
 */
public class TerrainREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String planetidStr = request.getParameterValues("planetid")[0];

            long planetId = Long.parseLong(planetidStr);

            TerrainObj newTerrain = TerrainDAO.FetchByPlanetID(planetId);
            if (newTerrain != null) {
                if (PlanetDAO.UserHasAccess(userId, planetId)) {
                    // do any init work
                } else
                    newTerrain = null;
            }

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();


            RestUtils.get_gson().toJson(newTerrain, out);
            out.flush();
            out.close();
        }
    }
}
