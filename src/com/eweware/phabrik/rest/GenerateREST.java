package com.eweware.phabrik.rest;

import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SunObj;
import com.eweware.phabrik.sim.StarGen;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Dave on 9/4/2016.
 */
public class GenerateREST extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StarGen generator = new StarGen();

        String systemName = "DaveSystem";
        double minMass = 0.3;
        double maxMass = 3;
        double mass = ThreadLocalRandom.current().nextDouble(minMass, maxMass);

        SunObj newSun = null;
        int numTries = 0;

        while (numTries++ < 1000) {
            newSun = generator.GenerateSystem(systemName, mass);

            if (newSun.habitable > 0) {
                break;
            }
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();


        RestUtils.get_gson().toJson(newSun, out);
        out.flush();
        out.close();

    }
}
