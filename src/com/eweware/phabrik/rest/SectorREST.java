package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.AccessDAO;
import com.eweware.phabrik.DAO.PlanetDAO;
import com.eweware.phabrik.DAO.SectorDAO;
import com.eweware.phabrik.DAO.TerrainDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.AccessObj;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SectorObj;
import com.eweware.phabrik.obj.TerrainObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Dave on 9/22/2016.
 */
public class SectorREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
           String updateUrlStr = request.getParameter("updateurl");

            if (updateUrlStr != null) {
                String newUrlStr = request.getParameter("url");
                String sectorIdStr = request.getParameter("sectorid");

                long sectorId = Long.parseLong(sectorIdStr);
                SectorDAO.UpdateSectorUrl(sectorId, newUrlStr);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String sectorIdStr = request.getParameter("sectorid");

            long sectorid = Long.parseLong(sectorIdStr);

            SectorObj newSector = null;

            newSector = SectorDAO.FetchById(sectorid);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();

            RestUtils.get_gson().toJson(newSector, out);
            out.flush();
            out.close();
        }
    }
}
