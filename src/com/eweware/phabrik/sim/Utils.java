package com.eweware.phabrik.sim;

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
}
