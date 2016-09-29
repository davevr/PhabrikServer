package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.SectorDAO;
import com.eweware.phabrik.DAO.StructureDAO;
import com.eweware.phabrik.DAO.StructureTypeDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.SectorObj;
import com.eweware.phabrik.obj.StructureObj;
import com.eweware.phabrik.obj.StructureTypeObj;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Dave on 9/22/2016.
 */
public class StructureREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String catalogStr = request.getParameter("catalog");
            String structureIdStr = request.getParameter("structureid");
            String sectorIdStr = request.getParameter("sectorid");

            if (catalogStr != null) {
                // return the catalog of all types
                List<StructureTypeObj> typeList = StructureTypeDAO.FetchForUser(userId);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();

                RestUtils.get_gson().toJson(typeList, out);
                out.flush();
                out.close();
            } else if (structureIdStr != null) {
                // looking for a specific structure
                long structureId = Long.parseLong(structureIdStr);
                StructureObj newStruct = null;

                newStruct = StructureDAO.FetchByID(structureId);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();

                RestUtils.get_gson().toJson(newStruct, out);
                out.flush();
                out.close();
            } else if (sectorIdStr != null) {
                // looking for a specific structure
                long sectorId = Long.parseLong(sectorIdStr);
                List<StructureObj> newList =  StructureDAO.FetchBySectorID(sectorId);

                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();

                RestUtils.get_gson().toJson(newList, out);
                out.flush();
                out.close();
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }



        }
    }
}
