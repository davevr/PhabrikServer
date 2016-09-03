package com.eweware.phabrik.obj;

import java.util.List;

/**
 * Created by davidvronay on 8/23/16.
 */
public class PlanetObj {

    public enum planet_type {
        tUnknown,
        tRock,
        tVenusian,
        tTerrestrial,
        tGasGiant,
        tMartian,
        tWater,
        tIce,
        tSubGasGiant,
        tSubSubGasGiant,
        tAsteroids,
        t1Face

    }
    public long Id;
    public long systemId;
    public long discovererId;
    public String planetName;
    public Boolean owned;
    public long ownerId;

    // planet specific stats
    public int			planet_no;
    public double a;					/* semi-major axis of solar orbit (in AU)*/
    public double e;					/* eccentricity of solar orbit		 */
    public double	axial_tilt;			/* units of degrees					 */
    public double mass;				/* mass (in solar masses)			 */
    public Boolean 		gas_giant;			/* TRUE if the planet is a gas giant */
    public double	dust_mass;			/* mass, ignoring gas				 */
    public double	gas_mass;			/* mass, ignoring dust				 */
     public double moon_a;				/* semi-major axis of lunar orbit (in AU)*/
    public double moon_e;				/* eccentricity of lunar orbit		 */
    public double	core_radius;		/* radius of the rocky core (in km)	 */
    public double radius;				/* equatorial radius (in km)		 */
    public int 		orbit_zone;			/* the 'zone' of the planet			 */
    public double density;			/* density (in g/cc)				 */
    public double orb_period;			/* length of the local year (days)	 */
    public double day;				/* length of the local day (hours)	 */
    public Boolean 		resonant_period;	/* TRUE if in resonant rotation		 */
    public double	esc_velocity;		/* units of cm/sec					 */
    public double	surf_accel;			/* units of cm/sec2					 */
    public double	surf_grav;			/* units of Earth gravities			 */
    public double	rms_velocity;		/* units of cm/sec					 */
    public double molec_weight;		/* smallest molecular weight retained*/
    public double	volatile_gas_inventory;
    public double	surf_pressure;		/* units of millibars (mb)			 */
    public Boolean		 	greenhouse_effect;	/* runaway greenhouse effect?		 */
    public double	boil_point;			/* the boiling point of water (Kelvin)*/
    public double	albedo;				/* albedo of the planet				 */
    public double	exospheric_temp;	/* units of degrees Kelvin			 */
    public double estimated_temp;     /* quick non-iterative estimate (K)  */
    public double estimated_terr_temp;/* for terrestrial moons and the like*/
    public double	surf_temp;			/* surface temperature in Kelvin	 */
    public double	greenhs_rise;		/* Temperature rise due to greenhouse */
    public double high_temp;			/* Day-time temperature              */
    public double low_temp;			/* Night-time temperature			 */
    public double max_temp;			/* Summer/Day						 */
    public double min_temp;			/* Winter/Night						 */
    public double	hydrosphere;		/* fraction of surface covered		 */
    public double	cloud_cover;		/* fraction of surface covered		 */
    public double	ice_cover;			/* fraction of surface covered		 */
    public List<Long> atmosphere;      /* list of gasses in the atmosphere */
    public long  planetTypeId;				/* Type code						 */
    public List<Long> moonList;        /* list of moons, if any */

}
