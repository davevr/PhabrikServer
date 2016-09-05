package com.eweware.phabrik.sim;

/**
 * Created by Dave on 9/2/2016.
 */
import com.eweware.phabrik.obj.PlanetObj;

public class Enviro {


    public static String breathability_phrase[] =
    {
        "none",
        "breathable",
        "unbreathable",
        "poisonous"
    };

    double luminosity(double mass_ratio)
    {
        double n;

        if (mass_ratio < 1.0)
            n = 1.75 * (mass_ratio - 0.1) + 3.325;
        else
            n = 0.5 * (2.0 - mass_ratio) + 4.4;
        return(Math.pow(mass_ratio,n));
    }


/*--------------------------------------------------------------------------*/
/*	 This function, given the orbital radius of a planet in AU, returns		*/
/*	 the orbital 'zone' of the particle.									*/
/*--------------------------------------------------------------------------*/

    int orb_zone(double luminosity, double orb_radius)
    {
        if (orb_radius < (4.0 * Math.sqrt(luminosity)))
            return(1);
        else if (orb_radius < (15.0 * Math.sqrt(luminosity)))
            return(2);
        else
            return(3);
    }


/*--------------------------------------------------------------------------*/
/*	 The mass is in units of solar masses, and the density is in units		*/
/*	 of grams/cc.  The radius returned is in units of km.					*/
/*--------------------------------------------------------------------------*/

    double volume_radius(double mass, double density)
    {
        double volume;

        mass = mass * Constants.SOLAR_MASS_IN_GRAMS;
        volume = mass / density;
        return(Math.pow((3.0 * volume) / (4.0 * Math.PI),(1.0 / 3.0)) / Constants.CM_PER_KM);
    }

/*--------------------------------------------------------------------------*/
/*	 Returns the radius of the planet in kilometers.						*/
/*	 The mass passed in is in units of solar masses.						*/
/*	 This formula is listed as eq.9 in Fogg's article, although some typos	*/
/*	 crop up in that eq.  See "The Internal Constitution of Planets", by	*/
/*	 Dr. D. S. Kothari, Mon. Not. of the Royal Astronomical Society, vol 96 */
/*	 pp.833-843, 1936 for the derivation.  Specifically, this is Kothari's	*/
/*	 eq.23, which appears on page 840.										*/
/*--------------------------------------------------------------------------*/

    double kothari_radius(double mass, boolean giant, int zone)
    {
        double temp1;
        double temp, temp2, atomic_weight, atomic_num;

        if (zone == 1)
        {
            if (giant)
            {
                atomic_weight = 9.5;
                atomic_num = 4.5;
            }
            else
            {
                atomic_weight = 15.0;
                atomic_num = 8.0;
            }
        }
        else
        if (zone == 2)
        {
            if (giant)
            {
                atomic_weight = 2.47;
                atomic_num = 2.0;
            }
            else
            {
                atomic_weight = 10.0;
                atomic_num = 5.0;
            }
        }
        else
        {
            if (giant)
            {
                atomic_weight = 7.0;
                atomic_num = 4.0;
            }
            else
            {
                atomic_weight = 10.0;
                atomic_num = 5.0;
            }
        }

        temp1 = atomic_weight * atomic_num;

        temp = (2.0 * Constants.BETA_20 * Math.pow(Constants.SOLAR_MASS_IN_GRAMS,(1.0 / 3.0)))
                / (Constants.A1_20 * Math.pow(temp1, (1.0 / 3.0)));

        temp2 = Constants.A2_20 * Math.pow(atomic_weight,(4.0 / 3.0)) * Math.pow(Constants.SOLAR_MASS_IN_GRAMS,(2.0 / 3.0));
        temp2 = temp2 * Math.pow(mass,(2.0 / 3.0));
        temp2 = temp2 / (Constants.A1_20 * Math.pow(atomic_num,2));
        temp2 = 1.0 + temp2;
        temp = temp / temp2;
        temp = (temp * Math.pow(mass,(1.0 / 3.0))) / Constants.CM_PER_KM;

        temp /= Constants.JIMS_FUDGE;			/* Make Earth = actual earth */

        return(temp);
    }


/*--------------------------------------------------------------------------*/
/*	The mass passed in is in units of solar masses, and the orbital radius	*/
/*	is in units of AU.	The density is returned in units of grams/cc.		*/
/*--------------------------------------------------------------------------*/

    double empirical_density(double mass, double orb_radius,
                                  double r_ecosphere, boolean gas_giant)
    {
        double temp;

        temp = Math.pow(mass * Constants.SUN_MASS_IN_EARTH_MASSES,(1.0 / 8.0));
        temp = temp * Utils.pow1_4(r_ecosphere / orb_radius);
        if (gas_giant)
            return(temp * 1.2);
        else
            return(temp * 5.5);
    }


/*--------------------------------------------------------------------------*/
/*	The mass passed in is in units of solar masses, and the equatorial		*/
/*	radius is in km.  The density is returned in units of grams/cc.			*/
/*--------------------------------------------------------------------------*/

    double volume_density(double mass, double equat_radius)
    {
        double volume;

        mass = mass * Constants.SOLAR_MASS_IN_GRAMS;
        equat_radius = equat_radius * Constants.CM_PER_KM;
        volume = (4.0 * Math.PI * Utils.pow3(equat_radius)) / 3.0;
        return(mass / volume);
    }


/*--------------------------------------------------------------------------*/
/*	The separation is in units of AU, and both masses are in units of solar */
/*	masses.	 The period returned is in terms of Earth days.					*/
/*--------------------------------------------------------------------------*/

    double period(double separation, double small_mass, double large_mass)
    {
        double period_in_years;

        period_in_years = Math.sqrt(Utils.pow3(separation) / (small_mass + large_mass));
        return(period_in_years * Constants.DAYS_IN_A_YEAR);
    }


/*--------------------------------------------------------------------------*/
/*	 Fogg's information for this routine came from Dole "Habitable Planets	*/
/* for Man", Blaisdell Publishing Company, NY, 1964.  From this, he came	*/
/* up with his eq.12, which is the equation for the 'base_angular_velocity' */
/* below.  He then used an equation for the change in angular velocity per	*/
/* time (dw/dt) from P. Goldreich and S. Soter's paper "Q in the Solar		*/
/* System" in Icarus, vol 5, pp.375-389 (1966).	 Using as a comparison the	*/
/* change in angular velocity for the Earth, Fogg has come up with an		*/
/* approximation for our new planet (his eq.13) and take that into account. */
/* This is used to find 'change_in_angular_velocity' below.					*/
/*																			*/
/*	 Input parameters are mass (in solar masses), radius (in Km), orbital	*/
/* period (in days), orbital radius (in AU), density (in g/cc),				*/
/* eccentricity, and whether it is a gas giant or not.						*/
/*	 The length of the day is returned in units of hours.					*/
/*--------------------------------------------------------------------------*/

    double day_length(PlanetObj	planet)
    {
        double planetary_mass_in_grams = planet.mass * Constants.SOLAR_MASS_IN_GRAMS;
        double	equatorial_radius_in_cm = planet.radius * Constants.CM_PER_KM;
        double	year_in_hours			= planet.orb_period * 24.0;
        boolean giant = (planet.planetType == PlanetObj.planet_type.tGasGiant ||
                planet.planetType == PlanetObj.planet_type.tSubGasGiant ||
                        planet.planetType == PlanetObj.planet_type.tSubSubGasGiant);
        double	k2;
        double	base_angular_velocity;
        double	change_in_angular_velocity;
        double	ang_velocity;
        double	spin_resonance_factor;
        double	day_in_hours;

        boolean stopped = false;

        planet.resonant_period = false;	/* Warning: Modify the planet */

        if (giant)
            k2 = 0.24;
        else
            k2 = 0.33;

        base_angular_velocity = Math.sqrt(2.0 * Constants.J * (planetary_mass_in_grams) /
                (k2 * Math.pow(equatorial_radius_in_cm, 2)));

/*	This next calculation determines how much the planet's rotation is	 */
/*	slowed by the presence of the star.								 */

        change_in_angular_velocity = Constants.CHANGE_IN_EARTH_ANG_VEL *
                (planet.density / Constants.EARTH_DENSITY) *
                (equatorial_radius_in_cm / Constants.EARTH_RADIUS) *
                (Constants.EARTH_MASS_IN_GRAMS / planetary_mass_in_grams) *
                Math.pow(planet.sun.mass, 2.0) *
                (1.0 / Math.pow(planet.a, 6.0));
        ang_velocity = base_angular_velocity + (change_in_angular_velocity *
                planet.sun.age);

/* Now we change from rad/sec to hours/rotation.						 */

        if (ang_velocity <= 0.0)
        {
            stopped = true;
            day_in_hours = Constants.INCREDIBLY_LARGE_NUMBER ;
        }
        else
            day_in_hours = Constants.RADIANS_PER_ROTATION / (Constants.SECONDS_PER_HOUR * ang_velocity);

        if ((day_in_hours >= year_in_hours) || stopped)
        {
            if (planet.e > 0.1)
            {
                spin_resonance_factor 	= (1.0 - planet.e) / (1.0 + planet.e);
                planet.resonant_period 	= true;
                return(spin_resonance_factor * year_in_hours);
            }
            else
                return(year_in_hours);
        }

        return(day_in_hours);
    }


/*--------------------------------------------------------------------------*/
/*	 The orbital radius is expected in units of Astronomical Units (AU).	*/
/*	 Inclination is returned in units of degrees.							*/
/*--------------------------------------------------------------------------*/

    int inclination(double orb_radius)
    {
        int temp;

        temp = (int)(Math.pow(orb_radius,0.2) * Utils.about(Constants.EARTH_AXIAL_TILT,0.4));
        return(temp % 360);
    }


/*--------------------------------------------------------------------------*/
/*	 This function implements the escape velocity calculation.	Note that	*/
/*	it appears that Fogg's eq.15 is incorrect.								*/
/*	The mass is in units of solar mass, the radius in kilometers, and the	*/
/*	velocity returned is in cm/sec.											*/
/*--------------------------------------------------------------------------*/

    double escape_vel(double mass, double radius)
    {
        double mass_in_grams, radius_in_cm;

        mass_in_grams = mass * Constants.SOLAR_MASS_IN_GRAMS;
        radius_in_cm = radius * Constants.CM_PER_KM;
        return(Math.sqrt(2.0 * Constants.GRAV_CONSTANT * mass_in_grams / radius_in_cm));
    }


/*--------------------------------------------------------------------------*/
/*	This is Fogg's eq.16.  The molecular weight (usually assumed to be N2)	*/
/*	is used as the basis of the Root Mean Square (RMS) velocity of the		*/
/*	molecule or atom.  The velocity returned is in cm/sec.					*/
/*	Orbital radius is in A.U.(ie: in units of the earth's orbital radius).	*/
/*--------------------------------------------------------------------------*/

    double rms_vel(double molecular_weight, double exospheric_temp)
    {
        return(Math.sqrt((3.0 * Constants.MOLAR_GAS_CONST * exospheric_temp) / molecular_weight)
                * Constants.CM_PER_METER);
    }


/*--------------------------------------------------------------------------*/
/*	 This function returns the smallest molecular weight retained by the	*/
/*	body, which is useful for determining the atmosphere composition.		*/
/*	Mass is in units of solar masses, and equatorial radius is in units of	*/
/*	kilometers.																*/
/*--------------------------------------------------------------------------*/

    double molecule_limit(double mass, double equat_radius, double exospheric_temp)
    {
        double esc_velocity = escape_vel(mass,equat_radius);

        return ((3.0 * Constants.MOLAR_GAS_CONST * exospheric_temp) /
                (Utils.pow2((esc_velocity/ Constants.GAS_RETENTION_THRESHOLD) / Constants.CM_PER_METER)));

    }

/*--------------------------------------------------------------------------*/
/*	 This function calculates the surface acceleration of a planet.	 The	*/
/*	mass is in units of solar masses, the radius in terms of km, and the	*/
/*	acceleration is returned in units of cm/sec2.							*/
/*--------------------------------------------------------------------------*/

    double acceleration(double mass, double radius)
    {
        return(Constants.GRAV_CONSTANT * (mass * Constants.SOLAR_MASS_IN_GRAMS) /
                Utils.pow2(radius * Constants.CM_PER_KM));
    }


/*--------------------------------------------------------------------------*/
/*	 This function calculates the surface gravity of a planet.	The			*/
/*	acceleration is in units of cm/sec2, and the gravity is returned in		*/
/*	units of Earth gravities.												*/
/*--------------------------------------------------------------------------*/

    double gravity(double acceleration)
    {
        return(acceleration / Constants.EARTH_ACCELERATION);
    }

/*--------------------------------------------------------------------------*/
/*	This implements Fogg's eq.17.  The 'inventory' returned is unitless.	*/
/*--------------------------------------------------------------------------*/

    double vol_inventory(double mass, double escape_vel, double rms_vel, double stellar_mass, int zone,
                              boolean greenhouse_effect, boolean accreted_gas)
    {
        double velocity_ratio, proportion_const, temp1, temp2, earth_units;

        velocity_ratio = escape_vel / rms_vel;
        if (velocity_ratio >=Constants. GAS_RETENTION_THRESHOLD)
        {
            switch (zone) {
                case 1:
                    proportion_const = 140000.0;	/* 100 . 140 JLB */
                    break;
                case 2:
                    proportion_const = 75000.0;
                    break;
                case 3:
                    proportion_const = 250.0;
                    break;
                default:
                    proportion_const = 0.0;
                    //printf("Error: orbital zone not initialized correctly!\n");
                    break;
            }
            earth_units = mass * Constants.SUN_MASS_IN_EARTH_MASSES;
            temp1 = (proportion_const * earth_units) / stellar_mass;
            temp2 = Utils.about(temp1,0.2);
            temp2 = temp1;
            if (greenhouse_effect || accreted_gas)
                return(temp2);
            else
                return(temp2 / 140.0);	/* 100 . 140 JLB */
        }
        else
            return(0.0);
    }


/*--------------------------------------------------------------------------*/
/*	This implements Fogg's eq.18.  The pressure returned is in units of		*/
/*	millibars (mb).	 The gravity is in units of Earth gravities, the radius */
/*	in units of kilometers.													*/
/*																			*/
/*  JLB: Aparently this assumed that earth pressure = 1000mb. I've added a	*/
/*	fudge factor (EARTH_SURF_PRES_IN_MILLIBARS / 1000.) to correct for that	*/
/*--------------------------------------------------------------------------*/

    double pressure(double volatile_gas_inventory, double equat_radius, double gravity)
    {
        equat_radius = Constants.KM_EARTH_RADIUS / equat_radius;
        return(volatile_gas_inventory * gravity *
                (Constants.EARTH_SURF_PRES_IN_MILLIBARS / 1000.) /
                Utils.pow2(equat_radius));
    }

/*--------------------------------------------------------------------------*/
/*	 This function returns the boiling point of water in an atmosphere of	*/
/*	 pressure 'surf_pressure', given in millibars.	The boiling point is	*/
/*	 returned in units of Kelvin.  This is Fogg's eq.21.					*/
/*--------------------------------------------------------------------------*/

    double boiling_point(double surf_pressure)
    {
        double surface_pressure_in_bars;

        surface_pressure_in_bars = surf_pressure / Constants.MILLIBARS_PER_BAR;
        return (1.0 / ((Math.log(surface_pressure_in_bars) / -5050.5) +
                (1.0 / 373.0) ));

    }


/*--------------------------------------------------------------------------*/
/*	 This function is Fogg's eq.22.	 Given the volatile gas inventory and	*/
/*	 planetary radius of a planet (in Km), this function returns the		*/
/*	 fraction of the planet covered with water.								*/
/*	 I have changed the function very slightly:	 the fraction of Earth's	*/
/*	 surface covered by water is 71%, not 75% as Fogg used.					*/
/*--------------------------------------------------------------------------*/

    double hydro_fraction(double volatile_gas_inventory, double planet_radius)
    {
        double temp;

        temp = (0.71 * volatile_gas_inventory / 1000.0)
                * Utils.pow2(Constants.KM_EARTH_RADIUS / planet_radius);
        if (temp >= 1.0)
            return(1.0);
        else
            return(temp);
    }


/*--------------------------------------------------------------------------*/
/*	 Given the surface temperature of a planet (in Kelvin), this function	*/
/*	 returns the fraction of cloud cover available.	 This is Fogg's eq.23.	*/
/*	 See Hart in "Icarus" (vol 33, pp23 - 39, 1978) for an explanation.		*/
/*	 This equation is Hart's eq.3.											*/
/*	 I have modified it slightly using constants and relationships from		*/
/*	 Glass's book "Introduction to Planetary Geology", p.46.				*/
/*	 The 'CLOUD_COVERAGE_FACTOR' is the amount of surface area on Earth		*/
/*	 covered by one Kg. of cloud.											*/
/*--------------------------------------------------------------------------*/

    double cloud_fraction(double surf_temp, double smallest_MW_retained, double equat_radius, double hydro_fraction)
    {
        double water_vapor_in_kg, fraction, surf_area, hydro_mass;

        if (smallest_MW_retained > Constants.WATER_VAPOR)
            return(0.0);
        else
        {
            surf_area = 4.0 * Math.PI * Utils.pow2(equat_radius);
            hydro_mass = hydro_fraction * surf_area * Constants.EARTH_WATER_MASS_PER_AREA;
            water_vapor_in_kg = (0.00000001 * hydro_mass) *
                    Math.exp(Constants.Q2_36 * (surf_temp - Constants.EARTH_AVERAGE_KELVIN));
            fraction = Constants.CLOUD_COVERAGE_FACTOR * water_vapor_in_kg / surf_area;
            if (fraction >= 1.0)
                return(1.0);
            else
                return(fraction);
        }
    }


/*--------------------------------------------------------------------------*/
/*	 Given the surface temperature of a planet (in Kelvin), this function	*/
/*	 returns the fraction of the planet's surface covered by ice.  This is	*/
/*	 Fogg's eq.24.	See Hart[24] in Icarus vol.33, p.28 for an explanation. */
/*	 I have changed a constant from 70 to 90 in order to bring it more in	*/
/*	 line with the fraction of the Earth's surface covered with ice, which	*/
/*	 is approximatly .016 (=1.6%).											*/
/*--------------------------------------------------------------------------*/

    double ice_fraction(double hydro_fraction, double surf_temp)
    {
        double temp;

        if (surf_temp > 328.0)
            surf_temp = 328.0;
        temp = Math.pow(((328.0 - surf_temp) / 90.0), 5.0);
        if (temp > (1.5 * hydro_fraction))
            temp = (1.5 * hydro_fraction);
        if (temp >= 1.0)
            return(1.0);
        else
            return(temp);
    }


/*--------------------------------------------------------------------------*/
/*	This is Fogg's eq.19.  The ecosphere radius is given in AU, the orbital */
/*	radius in AU, and the temperature returned is in Kelvin.				*/
/*--------------------------------------------------------------------------*/

    double eff_temp(double ecosphere_radius, double orb_radius, double albedo)
    {
        return(Math.sqrt(ecosphere_radius / orb_radius)
                * Utils.pow1_4((1.0 - albedo) / (1.0 - Constants.EARTH_ALBEDO))
                * Constants.EARTH_EFFECTIVE_TEMP);
    }


    double est_temp(double ecosphere_radius, double orb_radius, double albedo)
    {
        return(Math.sqrt(ecosphere_radius / orb_radius)
                * Utils.pow1_4((1.0 - albedo) / (1.0 - Constants.EARTH_ALBEDO))
                * Constants.EARTH_AVERAGE_KELVIN);
    }


/*--------------------------------------------------------------------------*/
/* Old grnhouse:                                                            */
/*	Note that if the orbital radius of the planet is greater than or equal	*/
/*	to R_inner, 99% of it's volatiles are assumed to have been deposited in */
/*	surface reservoirs (otherwise, it suffers from the greenhouse effect).	*/
/*--------------------------------------------------------------------------*/
/*	if ((orb_radius < r_greenhouse) && (zone == 1)) */

/*--------------------------------------------------------------------------*/
/*	The new definition is based on the inital surface temperature and what	*/
/*	state water is in. If it's too hot, the water will never condense out	*/
/*	of the atmosphere, rain down and form an ocean. The albedo used here	*/
/*	was chosen so that the boundary is about the same as the old method		*/
/*	Neither zone, nor r_greenhouse are used in this version				JLB	*/
/*--------------------------------------------------------------------------*/

    boolean grnhouse(double r_ecosphere, double orb_radius)
    {
        double	temp = eff_temp(r_ecosphere, orb_radius, Constants.GREENHOUSE_TRIGGER_ALBEDO);

        if (temp > Constants.FREEZING_POINT_OF_WATER)
            return(true);
        else
            return(false);
    }


/*--------------------------------------------------------------------------*/
/*	This is Fogg's eq.20, and is also Hart's eq.20 in his "Evolution of		*/
/*	Earth's Atmosphere" article.  The effective temperature given is in		*/
/*	units of Kelvin, as is the rise in temperature produced by the			*/
/*	greenhouse effect, which is returned.									*/
/*	I tuned this by changing a Math.pow(x,.25) to Math.pow(x,.4) to match Venus - JLB	*/
/*--------------------------------------------------------------------------*/

    double green_rise(double optical_depth, double effective_temp, double surf_pressure)
    {
        double convection_factor = Constants.EARTH_CONVECTION_FACTOR *
            Math.pow(surf_pressure /
                    Constants.EARTH_SURF_PRES_IN_MILLIBARS, 0.4);
        double rise = (Utils.pow1_4(1.0 + 0.75 * optical_depth) - 1.0) *
            effective_temp * convection_factor;

        if (rise < 0.0) rise = 0.0;

        return rise;
    }


/*--------------------------------------------------------------------------*/
/*	 The surface temperature passed in is in units of Kelvin.				*/
/*	 The cloud adjustment is the fraction of cloud cover obscuring each		*/
/*	 of the three major components of albedo that lie below the clouds.		*/
/*--------------------------------------------------------------------------*/

    double planet_albedo(double water_fraction, double cloud_fraction, double ice_fraction, double surf_pressure)
    {
        double rock_fraction, cloud_adjustment, components, cloud_part,
            rock_part, water_part, ice_part;

        rock_fraction = 1.0 - water_fraction - ice_fraction;
        components = 0.0;
        if (water_fraction > 0.0)
            components = components + 1.0;
        if (ice_fraction > 0.0)
            components = components + 1.0;
        if (rock_fraction > 0.0)
            components = components + 1.0;

        cloud_adjustment = cloud_fraction / components;

        if (rock_fraction >= cloud_adjustment)
            rock_fraction = rock_fraction - cloud_adjustment;
        else
            rock_fraction = 0.0;

        if (water_fraction > cloud_adjustment)
            water_fraction = water_fraction - cloud_adjustment;
        else
            water_fraction = 0.0;

        if (ice_fraction > cloud_adjustment)
            ice_fraction = ice_fraction - cloud_adjustment;
        else
            ice_fraction = 0.0;

        cloud_part = cloud_fraction * Constants.CLOUD_ALBEDO;		/* about(...,0.2); */

        if (surf_pressure == 0.0)
        {
            rock_part = rock_fraction * Constants.ROCKY_AIRLESS_ALBEDO;	/* about(...,0.3); */
            ice_part = ice_fraction * Constants.AIRLESS_ICE_ALBEDO;		/* about(...,0.4); */
            water_part = 0;
        }
        else
        {
            rock_part = rock_fraction * Constants.ROCKY_ALBEDO;	/* about(...,0.1); */
            water_part = water_fraction * Constants.WATER_ALBEDO;	/* about(...,0.2); */
            ice_part = ice_fraction * Constants.ICE_ALBEDO;		/* about(...,0.1); */
        }

        return(cloud_part + rock_part + water_part + ice_part);
    }


/*--------------------------------------------------------------------------*/
/*	 This function returns the dimensionless quantity of optical depth,		*/
/*	 which is useful in determining the amount of greenhouse effect on a	*/
/*	 planet.																*/
/*--------------------------------------------------------------------------*/

    double opacity(double molecular_weight, double surf_pressure)
    {
        double optical_depth;

        optical_depth = 0.0;
        if ((molecular_weight >= 0.0) && (molecular_weight < 10.0))
            optical_depth = optical_depth + 3.0;
        if ((molecular_weight >= 10.0) && (molecular_weight < 20.0))
            optical_depth = optical_depth + 2.34;
        if ((molecular_weight >= 20.0) && (molecular_weight < 30.0))
            optical_depth = optical_depth + 1.0;
        if ((molecular_weight >= 30.0) && (molecular_weight < 45.0))
            optical_depth = optical_depth + 0.15;
        if ((molecular_weight >= 45.0) && (molecular_weight < 100.0))
            optical_depth = optical_depth + 0.05;

        if (surf_pressure >= (70.0 * Constants.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 8.333;
        else
        if (surf_pressure >= (50.0 * Constants.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 6.666;
        else
        if (surf_pressure >= (30.0 * Constants.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 3.333;
        else
        if (surf_pressure >= (10.0 * Constants.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 2.0;
        else
        if (surf_pressure >= (5.0 * Constants.EARTH_SURF_PRES_IN_MILLIBARS))
            optical_depth = optical_depth * 1.5;

        return(optical_depth);
    }


/*
 *	calculates the number of years it takes for 1/e of a gas to escape
 *	from a planet's atmosphere.
 *	Taken from Dole p. 34. He cites Jeans (1916) & Jones (1923)
 */
    double gas_life(double molecular_weight, PlanetObj planet)
    {
        double v = rms_vel(molecular_weight, planet.exospheric_temp);
        double g = planet.surf_grav * Constants.EARTH_ACCELERATION;
        double r = (planet.radius * Constants.CM_PER_KM);
        double t = (Utils.pow3(v) / (2.0 * Utils.pow2(g) * r)) * Math.exp((3.0 * g * r) / Utils.pow2(v));
        double years = t / (Constants.SECONDS_PER_HOUR * 24.0 * Constants.DAYS_IN_A_YEAR);

//	double ve = planet.esc_velocity;
//	double k = 2;
//	double t2 = ((k * pow3(v) * r) / pow4(ve)) * exp((3.0 * pow2(ve)) / (2.0 * pow2(v)));
//	double years2 = t2 / (SECONDS_PER_HOUR * 24.0 * DAYS_IN_A_YEAR);

//	if (flag_verbose & 0x0040)
//		fprintf (stderr, "gas_life: %LGs, V ratio: %Lf\n",
//				years, ve / v);

        if (years > 2.0E10)
            years = Constants.INCREDIBLY_LARGE_NUMBER;

        return years;
    }

    double min_molec_weight (PlanetObj planet)
    {
        double mass    = planet.mass;
        double radius  = planet.radius;
        double temp    = planet.exospheric_temp;
        double target  = 5.0E9;

        double guess_1 = molecule_limit (mass, radius, temp);
        double	guess_2 = guess_1;

        double life = gas_life(guess_1, planet);

        int	loops = 0;

        if (null != planet.sun)
        {
            target = planet.sun.age;
        }

        if (life > target)
        {
            while ((life > target) && (loops++ < 25))
            {
                guess_1 = guess_1 / 2.0;
                life 	= gas_life(guess_1, planet);
            }
        }
        else
        {
            while ((life < target) && (loops++ < 25))
            {
                guess_2 = guess_2 * 2.0;
                life 	= gas_life(guess_2, planet);
            }
        }

        loops = 0;

        while (((guess_2 - guess_1) > 0.1) && (loops++ < 25))
        {
            double guess_3 = (guess_1 + guess_2) / 2.0;
            life			 	= gas_life(guess_3, planet);

            if (life < target)
                guess_1 = guess_3;
            else
                guess_2 = guess_3;
        }

        life = gas_life(guess_2, planet);

        return (guess_2);
    }


/*--------------------------------------------------------------------------*/
/*	 The temperature calculated is in degrees Kelvin.						*/
/*	 Quantities already known which are used in these calculations:			*/
/*		 planet.molec_weight												*/
/*		 planet.surf_pressure												*/
/*		 R_ecosphere														*/
/*		 planet.a															*/
/*		 planet.volatile_gas_inventory										*/
/*		 planet.radius														*/
/*		 planet.boil_point													*/
/*--------------------------------------------------------------------------*/

    void calculate_surface_temp(PlanetObj 	planet,
                                boolean				first,
                                double		last_water,
                                double 	last_clouds,
                                double 	last_ice,
                                double 	last_temp,
                                double 	last_albedo)
    {
        double effective_temp;
        double water_raw;
        double clouds_raw;
        double greenhouse_temp;
        boolean			boil_off = false;

        if (first)
        {
            planet.albedo = Constants.EARTH_ALBEDO;

            effective_temp 		= eff_temp(planet.sun.r_ecosphere, planet.a, planet.albedo);
            greenhouse_temp     = green_rise(opacity(planet.molec_weight,
                    planet.surf_pressure),
                    effective_temp,
                    planet.surf_pressure);
            planet.surf_temp   = effective_temp + greenhouse_temp;

            set_temp_range(planet);
        }

        if (planet.greenhouse_effect
                && planet.max_temp < planet.boil_point)
        {
            planet.greenhouse_effect = false;

            planet.volatile_gas_inventory 	= vol_inventory(planet.mass,
                    planet.esc_velocity,
                    planet.rms_velocity,
                    planet.sun.mass,
                    planet.orbit_zone,
                    planet.greenhouse_effect,
                    (planet.gas_mass
                            / planet.mass) > 0.000001);
            planet.surf_pressure 			= pressure(planet.volatile_gas_inventory,
                    planet.radius,
                    planet.surf_grav);

            planet.boil_point 			= boiling_point(planet.surf_pressure);
        }

        water_raw     			=
                planet.hydrosphere		= hydro_fraction(planet.volatile_gas_inventory,
                        planet.radius);
        clouds_raw     			=
                planet.cloud_cover 	= cloud_fraction(planet.surf_temp,
                        planet.molec_weight,
                        planet.radius,
                        planet.hydrosphere);
        planet.ice_cover   	= ice_fraction(planet.hydrosphere,
                planet.surf_temp);

        if ((planet.greenhouse_effect)
                && (planet.surf_pressure > 0.0))
            planet.cloud_cover	= 1.0;

        if ((planet.high_temp >= planet.boil_point)
                && (!first)
                && !((int)planet.day == (int)(planet.orb_period * 24.0) ||
                (planet.resonant_period)))
        {
            planet.hydrosphere	= 0.0;
            boil_off = true;

            if (planet.molec_weight > Constants.WATER_VAPOR)
                planet.cloud_cover = 0.0;
            else
                planet.cloud_cover = 1.0;
        }

        if (planet.surf_temp < (Constants.FREEZING_POINT_OF_WATER - 3.0))
            planet.hydrosphere	= 0.0;

        planet.albedo			= planet_albedo(planet.hydrosphere,
                planet.cloud_cover,
                planet.ice_cover,
                planet.surf_pressure);

        effective_temp 			= eff_temp(planet.sun.r_ecosphere, planet.a, planet.albedo);
        greenhouse_temp     	= green_rise(opacity(planet.molec_weight,
                planet.surf_pressure),
                effective_temp,
                planet.surf_pressure);
        planet.surf_temp   	= effective_temp + greenhouse_temp;

        if (!first)
        {
            if (!boil_off)
                planet.hydrosphere	= (planet.hydrosphere + (last_water * 2))  / 3;
            planet.cloud_cover	    = (planet.cloud_cover + (last_clouds * 2)) / 3;
            planet.ice_cover	    = (planet.ice_cover   + (last_ice * 2))    / 3;
            planet.albedo		    = (planet.albedo      + (last_albedo * 2)) / 3;
            planet.surf_temp	    = (planet.surf_temp   + (last_temp * 2))   / 3;
        }

        set_temp_range(planet);
    }

    void iterate_surface_temp(PlanetObj planet)
    {
        int			count = 0;
        double initial_temp = est_temp(planet.sun.r_ecosphere, planet.a, planet.albedo);

        double h2_life  = gas_life (Constants.MOL_HYDROGEN,    planet);
        double h2o_life = gas_life (Constants.WATER_VAPOR,     planet);
        double n2_life  = gas_life (Constants.MOL_NITROGEN,    planet);
        double n_life   = gas_life (Constants.ATOMIC_NITROGEN, planet);

        calculate_surface_temp(planet, true, 0, 0, 0, 0, 0);

        for (count = 0;
             count <= 25;
             count++)
        {
            double	last_water	= planet.hydrosphere;
            double last_clouds	= planet.cloud_cover;
            double last_ice	= planet.ice_cover;
            double last_temp	= planet.surf_temp;
            double last_albedo	= planet.albedo;

            calculate_surface_temp(planet, false,
                    last_water, last_clouds, last_ice,
                    last_temp, last_albedo);

            if (Math.abs(planet.surf_temp - last_temp) < 0.25)
                break;
        }

        planet.greenhs_rise = planet.surf_temp - initial_temp;

    }

/*--------------------------------------------------------------------------*/
/*	 Inspired partial pressure, taking into account humidification of the	*/
/*	 air in the nasal passage and throat This formula is on Dole's p. 14	*/
/*--------------------------------------------------------------------------*/

    double inspired_partial_pressure (double surf_pressure,
                                           double gas_pressure)
    {
        double pH2O = (Constants.H20_ASSUMED_PRESSURE);
        double fraction = gas_pressure / surf_pressure;

        return	(surf_pressure - pH2O) * fraction;
    }


/*--------------------------------------------------------------------------*/
/*	 This function uses figures on the maximum inspired partial pressures   */
/*   of Oxygen, other atmospheric and traces gases as laid out on pages 15, */
/*   16 and 18 of Dole's Habitable Planets for Man to derive breathability  */
/*   of the planet's atmosphere.                                       JLB  */
/*--------------------------------------------------------------------------*/

    PlanetObj.breathabilty_type breathability (PlanetObj planet)
    {
        boolean	oxygen_ok	= false;
        int index;

        if (planet.gases == 0)
            return PlanetObj.breathabilty_type.NONE;

        for (index = 0; index < planet.gases; index++)
        {
            int	n;
            int	gas_no = 0;

            double ipp = inspired_partial_pressure (planet.surf_pressure,
                planet.atmosphere.get(index).surf_pressure);

            for (n = 0; n < StarGen.gases.length; n++)
            {
                if (StarGen.gases[n].num == planet.atmosphere.get(index).num)
                    gas_no = n;
            }

            if (ipp > StarGen.gases[gas_no].max_ipp)
                return PlanetObj.breathabilty_type.POISONOUS;

            if (planet.atmosphere.get(index).num == Constants.AN_O)
                oxygen_ok = ((ipp >= Constants.MIN_O2_IPP) && (ipp <= Constants.MAX_O2_IPP));
        }

        if (oxygen_ok)
            return PlanetObj.breathabilty_type.BREATHABLE;
        else
            return PlanetObj.breathabilty_type.UNBREATHABLE;
    }

/* function for 'soft limiting' temperatures */

    double lim(double x)
    {
        return x / Math.sqrt(Math.sqrt(1 + x*x*x*x));
    }

    double soft(double v, double max, double min)
    {
        double dv = v - min;
        double dm = max - min;
        return (lim(2*dv/dm-1)+1)/2 * dm + min;
    }

    void set_temp_range(PlanetObj planet)
    {
        double pressmod = 1 / Math.sqrt(1 + 20 * planet.surf_pressure/1000.0);
        double ppmod    = 1 /  Math.sqrt(10 + 5 * planet.surf_pressure/1000.0);
        double tiltmod  =  Math.abs( Math.cos(planet.axial_tilt * Math.PI/180) * Math.pow(1 + planet.e, 2));
        double daymod   = 1 / (200/planet.day + 1);
        double mh = Math.pow(1 + daymod, pressmod);
        double ml = Math.pow(1 - daymod, pressmod);
        double hi = mh * planet.surf_temp;
        double lo = ml * planet.surf_temp;
        double sh = hi + Math.pow((100+hi) * tiltmod,  Math.sqrt(ppmod));
        double wl = lo - Math.pow((150+lo) * tiltmod,  Math.sqrt(ppmod));
        double max = planet.surf_temp +  Math.sqrt(planet.surf_temp) * 10;
        double min = planet.surf_temp /  Math.sqrt(planet.day + 24);

        if (lo < min) lo = min;
        if (wl < 0)   wl = 0;

        planet.high_temp = soft(hi, max, min);
        planet.low_temp  = soft(lo, max, min);
        planet.max_temp  = soft(sh, max, min);
        planet.min_temp  = soft(wl, max, min);
    }



}
