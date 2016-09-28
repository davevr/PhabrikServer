package com.eweware.phabrik.rest;

import com.eweware.phabrik.DAO.AccessDAO;
import com.eweware.phabrik.DAO.SolSysDAO;
import com.eweware.phabrik.admin.Authenticator;
import com.eweware.phabrik.obj.SolSysObj;
import com.eweware.phabrik.obj.SolSysStatusObj;

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
public class SolSysREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long userId = Authenticator.CurrentUserId(request.getSession());

        if (userId == 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            String xLocStr = request.getParameter("xloc");
            String yLocStr = request.getParameter("yloc");
            String zLocStr = request.getParameter("zloc");
            String idStr = request.getParameter("solsysid");
            String statusStr = request.getParameter("status");
            String knownStr = request.getParameter("known");

            if (knownStr != null) {
                List<SolSysObj> knownSystems = AccessDAO.FetchKnownSystems(userId);
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_OK);
                PrintWriter out = response.getWriter();
                RestUtils.get_gson().toJson(knownSystems, out);
                out.flush();
                out.close();
            } else if (idStr != null) {
                long solSysId = Long.parseLong(idStr);

                if (statusStr != null && Boolean.parseBoolean(statusStr)) {
                    List<SolSysStatusObj> statusList = SolSysDAO.FetchSolSysStatus(solSysId);

                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();
                    RestUtils.get_gson().toJson(statusList, out);
                    out.flush();
                    out.close();
                } else {
                    // fetch the system, not just the status
                    SolSysObj theSys = SolSysDAO.FetchByID(solSysId);
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();
                    RestUtils.get_gson().toJson(theSys, out);
                    out.flush();
                    out.close();
                }

            } else {

                if (xLocStr != null && yLocStr != null && zLocStr != null) {
                    int xLoc = Integer.parseInt(xLocStr);
                    int yLoc = Integer.parseInt(yLocStr);
                    int zLoc = Integer.parseInt(zLocStr);

                    SolSysObj newSystem = SolSysDAO.FetchByXYZ(xLoc, yLoc, zLoc);

                    if (newSystem != null) {
                        if (SolSysDAO.UserHasAccess(userId, newSystem)) {
                            // do any init work
                        } else
                            newSystem = null;
                    }

                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_OK);
                    PrintWriter out = response.getWriter();


                    RestUtils.get_gson().toJson(newSystem, out);
                    out.flush();
                    out.close();
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }
    }
}
