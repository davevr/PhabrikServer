package com.eweware.phabrik.rest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by davidvronay on 9/5/16.
 */
public class ProbeSystemREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String xLocStr = request.getParameterValues("xloc")[0];
        String yLocStr = request.getParameterValues("yloc")[0];
        String zLocStr = request.getParameterValues("zloc")[0];
        String nameStr = request.getParameterValues("name")[0];
        String radiusStr = request.getParameterValues("radius")[0];
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
