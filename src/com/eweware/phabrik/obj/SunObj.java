package com.eweware.phabrik.obj;

import java.util.List;

/**
 * Created by davidvronay on 8/23/16.
 */
public class SunObj {
    public long Id;
    public long solarSystemId;
    public List<PlanetObj> planets;
    public long discovererId;
    public String name;

    // simulation variables
    public double	luminosity;
    public double	mass;
    public double life;
    public double age;
    public double r_ecosphere;
    public double		m2;
    public double		e;
    public double		a;

    public int earthlike		 = 0;
    public int habitable		 = 0;
    public int habitable_jovians = 0;

}
