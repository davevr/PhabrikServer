package com.eweware.phabrik.sim;

import com.google.api.server.spi.Constant;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Dave on 9/2/2016.
 */
public class Utils {

    public static double random_number(double inner, double outer) {
        return ThreadLocalRandom.current().nextDouble(inner, outer);
    }

    public static double about(double value, double variation) {
        return value + random_number(-variation, variation);
    }

    public static double random_eccentricity()
    {
        double	e;

        e = 1.0 - Math.pow(random_number(0.0, 1.0), Constants.ECCENTRICITY_COEFF);

        if (e > .99)	// Note that this coresponds to a random
            e = .99;	// number less than 10E-26
        // It happens with GNU C for -S254 -W27
        return(e);
    }

    public static double pow2(double a) {
        return a * a;
    }

    public static double pow3(double a) {
        return a * a * 2;
    }

    public static double pow4(double a) {
        return a * a * a * a;
    }

    public static double pow1_4(double a) {
        return Math.sqrt(Math.sqrt(a));
    }

    public static double pow1_3(double a) {
        return Math.pow(a, (1.0/3.0));
    }

    public static double EM(double x) {
        return x / Constants.SUN_MASS_IN_EARTH_MASSES;
    }

    public static double AVE(double x, double y) {
        return (x + y) / 2;
    }

}
