package com.eweware.phabrik.sim;

import com.eweware.phabrik.obj.PlanetObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dave on 9/2/2016.
 */
public class Accrete {
    Boolean 	dust_left;
    double		r_inner;
    double		r_outer;
    double		reduced_mass;
    double		dust_density;
    double		cloud_eccentricity;
    List<DustRecord> dustList	= null;
    PlanetObj   curPlanet = null;
    List<Gen>		histList	= null;

    void set_initial_conditions(double inner_limit_of_dust,
                                double outer_limit_of_dust)
    {
        Gen hist;
        hist = new Gen();
        hist.dusts = dustList;
        hist.firstPlanet = curPlanet;
        histList = new ArrayList<Gen>();
        hist.nextList = histList;

        dustList = new ArrayList<DustRecord>();

        DustRecord firstDust = new DustRecord();
        firstDust.outer_edge = outer_limit_of_dust;
        firstDust.inner_edge = inner_limit_of_dust;
        firstDust.dust_present = true;
        firstDust.gas_present = true;
        dust_left = true;
        dustList.add(firstDust);
        firstDust.nextList = dustList;
        cloud_eccentricity = 0.2;
    }

    double stellar_dust_limit(double stell_mass_ratio)
    {
        return(200.0 * Math.pow(stell_mass_ratio,(1.0 / 3.0)));
    }

    double nearest_planet(double stell_mass_ratio)
    {
        return(0.3 * Math.pow(stell_mass_ratio,(1.0 / 3.0)));
    }

    double farthest_planet(double stell_mass_ratio)
    {
        return(50.0 * Math.pow(stell_mass_ratio,(1.0 / 3.0)));
    }

    double inner_effect_limit(double a, double e, double mass)
    {
        return (a * (1.0 - e) * (1.0 - mass) / (1.0 + cloud_eccentricity));
    }

    double outer_effect_limit(double a, double e, double mass)
    {
        return (a * (1.0 + e) * (1.0 + mass) / (1.0 - cloud_eccentricity));
    }

    boolean dust_available(double inside_range, double outside_range)
    {
        boolean dust_here = false;

        for (DustRecord curDust : dustList) {
            if ((curDust.outer_edge >= inside_range) &&
                    (curDust.inner_edge >= outside_range) &&
                    curDust.dust_present)
            {
                dust_here = true;
                break;
            }
        }

        return dust_here;
    }

    void update_dust_lanes(double min, double max, double mass,
                           double crit_mass, double body_inner_bound,
                           double body_outer_bound)
    {
        boolean 	gas;
        DustRecord	node2;
        DustRecord	node3;

        dust_left = false;
        if ((mass > crit_mass))
            gas = false;
        else
            gas = true;

        int curIndex = 0;
        DustRecord curDust = dustList.get(curIndex);

        while (curDust != null) {
            if (((curDust.inner_edge < min) && (curDust.outer_edge > max)))
            {
                node2 = new DustRecord();
                node2.inner_edge = min;
                node2.outer_edge = max;
                if ((curDust.gas_present == true))
                    node2.gas_present = gas;
                else
                    node2.gas_present = false;
                node2.dust_present = false;

                node3 = new DustRecord();
                node3.inner_edge = max;
                node3.outer_edge = curDust.outer_edge;
                node3.gas_present = curDust.gas_present;
                node3.dust_present = curDust.dust_present;
                curDust.outer_edge = min;
                dustList.add(curIndex + 1, node2);
                dustList.add(curIndex + 2, node3);
                curIndex += 3;
                if (curIndex < dustList.size())
                    curDust = dustList.get(curIndex);
                else
                    curDust = null;
            } else if (((curDust.inner_edge < max) && (curDust.outer_edge > max)))
            {
                node2 = new DustRecord();
                node2.dust_present = curDust.dust_present;
                node2.gas_present = curDust.gas_present;
                node2.outer_edge = curDust.outer_edge;
                node2.inner_edge = max;
                curDust.outer_edge = max;
                if ((curDust.gas_present == true))
                    curDust.gas_present = gas;
                else
                    curDust.gas_present = false;
                curDust.dust_present = false;

                dustList.add(curIndex + 1, node2);
                curIndex += 2;
                if (curIndex < dustList.size())
                    curDust = dustList.get(curIndex);
                else
                    curDust = null;
            } else if (((curDust.inner_edge < min) && (curDust.outer_edge > min))) {
                node2 = new DustRecord();

                node2.dust_present = false;
                if ((curDust.gas_present == true))
                    node2.gas_present = gas;
                else
                    node2.gas_present = false;
                node2.outer_edge = curDust.outer_edge;
                node2.inner_edge = min;
                curDust.outer_edge = min;
                dustList.add(curIndex + 1, node2);
                curIndex += 2;
                if (curIndex < dustList.size())
                    curDust = dustList.get(curIndex);
                else
                    curDust = null;
            }
            else if (((curDust.inner_edge >= min) && (curDust.outer_edge <= max)))
            {
                if ((curDust.gas_present == true))
                    curDust.gas_present = gas;
                curDust.dust_present = false;
                curIndex++;
                if (curIndex < dustList.size())
                    curDust = dustList.get(curIndex);
                else
                    curDust = null;
            } else if (((curDust.outer_edge < min) || (curDust.inner_edge > max))) {
                curIndex++;
                if (curIndex < dustList.size())
                    curDust = dustList.get(curIndex);
                else
                    curDust = null;
            }
        }



        curDust = dustList.get(0);
        while ((curDust != null))
        {
            if (((curDust.dust_present)
                    && (((curDust.outer_edge >= body_inner_bound)
                    && (curDust.inner_edge <= body_outer_bound)))))
                dust_left = true;
            node2 = curDust.next();
            if ((node2 != null))
            {
                if (((curDust.dust_present == node2.dust_present)
                        && (curDust.gas_present == node2.gas_present)))
                {
                    curDust.outer_edge = node2.outer_edge;
                    dustList.remove(node2);
                }
            }
            curDust = curDust.next();
        }
    }

    class MassDustGasRecord {
        double dust;
        double gas;
        double mass;
    }

    MassDustGasRecord collect_dust(double last_mass, double new_dust,
                                   double new_gas,
                                   double a, double e,
                                   double crit_mass, DustRecord dust_band)
    {
        double	mass_density;
        double	temp1;
        double	temp2;
        double	temp;
        double	temp_density;
        double	bandwidth;
        double	width;
        double	volume;
        double	gas_density = 0.0;
        double	new_mass;
        double	next_dust = 0;
        double	next_gas = 0;
        MassDustGasRecord result = new MassDustGasRecord();

        result.mass = 0;
        result.dust = new_dust;
        result.gas = new_gas;

        temp = last_mass / (1.0 + last_mass);
        reduced_mass = Math.pow(temp,(1.0 / 4.0));
        r_inner = inner_effect_limit(a, e, reduced_mass);
        r_outer = outer_effect_limit(a, e, reduced_mass);

        if ((r_inner < 0.0))
            r_inner = 0.0;

        if ((dust_band == null))
            return result;
        else
        {
            if ((dust_band.dust_present == false))
                temp_density = 0.0;
            else
                temp_density = dust_density;

            if (((last_mass < crit_mass) || (dust_band.gas_present == false)))
                mass_density = temp_density;
            else
            {
                mass_density = Constants.K * temp_density / (1.0 + Math.sqrt(crit_mass / last_mass)
                        * (Constants.K - 1.0));
                gas_density = mass_density - temp_density;
            }

            if (((dust_band.outer_edge <= r_inner)
                    || (dust_band.inner_edge >= r_outer)))
            {
                return(collect_dust(last_mass, new_dust, new_gas,
                        a,e,crit_mass, dust_band.next()));
            }
            else
            {
                bandwidth = (r_outer - r_inner);

                temp1 = r_outer - dust_band.outer_edge;
                if (temp1 < 0.0)
                    temp1 = 0.0;
                width = bandwidth - temp1;

                temp2 = dust_band.inner_edge - r_inner;
                if (temp2 < 0.0)
                    temp2 = 0.0;
                width = width - temp2;

                temp = 4.0 * Math.PI * Math.pow(a,2.0) * reduced_mass
                        * (1.0 - e * (temp1 - temp2) / bandwidth);
                volume = temp * width;

                new_mass  = volume * mass_density;
                result.gas  = volume * gas_density;
                result.dust = new_mass - new_gas;

                MassDustGasRecord nextData = collect_dust(last_mass, next_dust, next_gas,
                    a,e,crit_mass, dust_band.next());

                result.gas  += nextData.gas;
                result.dust += nextData.dust;
                result.mass = new_mass + nextData.mass;

                return result;
            }
        }
    }


/*--------------------------------------------------------------------------*/
/*	 Orbital radius is in AU, eccentricity is unitless, and the stellar		*/
/*	luminosity ratio is with respect to the sun.  The value returned is the */
/*	mass at which the planet begins to accrete gas as well as dust, and is	*/
/*	in units of solar masses.												*/
/*--------------------------------------------------------------------------*/

    double critical_limit(double orb_radius, double eccentricity,
                               double stell_luminosity_ratio)
    {
        double	temp;
        double	perihelion_dist;

        perihelion_dist = (orb_radius - orb_radius * eccentricity);
        temp = perihelion_dist * Math.sqrt(stell_luminosity_ratio);
        return(Constants.B * Math.pow(temp,-0.75));
    }




    MassDustGasRecord accrete_dust(double seed_mass, double new_dust, double new_gas,
                      double a, double e, double crit_mass,
                      double body_inner_bound, double body_outer_bound)
    {
        double	new_mass = (seed_mass);
        double	temp_mass;
        MassDustGasRecord resultRec = new MassDustGasRecord();


        do
        {
            temp_mass = new_mass;
            MassDustGasRecord newVal = collect_dust(new_mass, new_dust, new_gas,
                    a,e,crit_mass, dustList.get(0));
            new_mass = newVal.mass;
            new_dust = newVal.dust;
            new_gas = newVal.gas;
        }
        while (!(((new_mass - temp_mass) < (0.0001 * temp_mass))));

        seed_mass = seed_mass + new_mass;
        update_dust_lanes(r_inner,r_outer,seed_mass,crit_mass,body_inner_bound,body_outer_bound);

        resultRec.mass =  seed_mass;
        resultRec.dust = new_dust;
        resultRec.gas = new_gas;

        return resultRec;
    }



    void coalesce_planetesimals(double a, double e, double mass, double crit_mass,
                                double dust_mass, double gas_mass,
                                double stell_luminosity_ratio,
                                double body_inner_bound, double body_outer_bound,
                                boolean			do_moons)
    {
        PlanetObj	the_planet;
        PlanetObj	next_planet;
        PlanetObj	prev_planet;
        boolean		finished;
        double 	temp;
        double 	diff;
        double 	dist1;
        double 	dist2;

        finished = false;
        prev_planet = null;

// First we try to find an existing planet with an over-lapping orbit.

        for (the_planet = curPlanet;
             the_planet != null;
             the_planet = the_planet.next_planet)
        {
            diff = the_planet.a - a;

            if ((diff > 0.0))
            {
                dist1 = (a * (1.0 + e) * (1.0 + reduced_mass)) - a;
			/* x aphelion	 */
                reduced_mass = Math.pow((the_planet.mass / (1.0 + the_planet.mass)),(1.0 / 4.0));
                dist2 = the_planet.a
                        - (the_planet.a * (1.0 - the_planet.e) * (1.0 - reduced_mass));
            }
            else
            {
                dist1 = a - (a * (1.0 - e) * (1.0 - reduced_mass));
			/* x perihelion */
                reduced_mass = Math.pow((the_planet.mass / (1.0 + the_planet.mass)),(1.0 / 4.0));
                dist2 = (the_planet.a * (1.0 + the_planet.e) * (1.0 + reduced_mass))
                        - the_planet.a;
            }

            if (((Math.abs(diff) <= Math.abs(dist1)) || (Math.abs(diff) <= Math.abs(dist2))))
            {
                double new_dust = 0;
                double	new_gas = 0;
                double new_a = (the_planet.mass + mass) /
                        ((the_planet.mass / the_planet.a) + (mass / a));

                temp = the_planet.mass * Math.sqrt(the_planet.a) * Math.sqrt(1.0 - Math.pow(the_planet.e,2.0));
                temp = temp + (mass * Math.sqrt(a) * Math.sqrt(Math.sqrt(1.0 - Math.pow(e,2.0))));
                temp = temp / ((the_planet.mass + mass) * Math.sqrt(new_a));
                temp = 1.0 - Math.pow(temp,2.0);
                if (((temp < 0.0) || (temp >= 1.0)))
                    temp = 0.0;
                e = Math.sqrt(temp);

                if (do_moons)
                {
                    double existing_mass = 0.0;

                    if (the_planet.first_moon != null)
                    {
                        PlanetObj	m;

                        for (m = the_planet.first_moon;
                             m != null;
                             m = m.next_planet)
                        {
                            existing_mass += m.mass;
                        }
                    }

                    if (mass < crit_mass)
                    {
                        if ((mass * Constants.SUN_MASS_IN_EARTH_MASSES) < 2.5
                                && (mass * Constants.SUN_MASS_IN_EARTH_MASSES) > .0001
                                && existing_mass < (the_planet.mass * .05)
                                )
                        {
                            PlanetObj	the_moon = new PlanetObj();

                            the_moon.planetType 			= PlanetObj.planet_type.tUnknown;
	/* 					the_moon.a 			= a; */
	/* 					the_moon.e 			= e; */
                            the_moon.mass 			= mass;
                            the_moon.dust_mass 	= dust_mass;
                            the_moon.gas_mass 		= gas_mass;
                            the_moon.atmosphere 	= null;
                            the_moon.next_planet 	= null;
                            the_moon.first_moon 	= null;
                            the_moon.gas_giant 	= false;
                            the_moon.atmosphere	= null;
                            the_moon.albedo		= 0;
                            //todo the_moon.gases			= 0;
                            the_moon.surf_temp		= 0;
                            the_moon.high_temp		= 0;
                            the_moon.low_temp		= 0;
                            the_moon.max_temp		= 0;
                            the_moon.min_temp		= 0;
                            the_moon.greenhs_rise	= 0;
                            //the_moon.minor_moons 	= 0;

                            if ((the_moon.dust_mass + the_moon.gas_mass)
                                    > (the_planet.dust_mass + the_planet.gas_mass))
                            {
                                double	temp_dust = the_planet.dust_mass;
                                double temp_gas  = the_planet.gas_mass;
                                double temp_mass = the_planet.mass;

                                the_planet.dust_mass = the_moon.dust_mass;
                                the_planet.gas_mass  = the_moon.gas_mass;
                                the_planet.mass      = the_moon.mass;

                                the_moon.dust_mass   = temp_dust;
                                the_moon.gas_mass    = temp_gas;
                                the_moon.mass        = temp_mass;
                            }

                            if (the_planet.first_moon == null)
                                the_planet.first_moon = the_moon;
                            else
                            {
                                the_moon.next_planet = the_planet.first_moon;
                                the_planet.first_moon = the_moon;
                            }

                            finished = true;

                            // moon captured!
                        }
                        else
                        {
                            // moon escaped!
                        }
                    }
                }

                if (!finished)
                {
                    // planets have collided!

                    temp = the_planet.mass + mass;
                    MassDustGasRecord theRec = accrete_dust(temp, new_dust, new_gas,
                        new_a,e,stell_luminosity_ratio,
                        body_inner_bound,body_outer_bound);

                    temp = theRec.mass;
                    new_dust = theRec.dust;
                    new_gas = theRec.gas;

                    the_planet.a = new_a;
                    the_planet.e = e;
                    the_planet.mass = temp;
                    the_planet.dust_mass += dust_mass + new_dust;
                    the_planet.gas_mass += gas_mass + new_gas;
                    if (temp >= crit_mass)
                        the_planet.gas_giant = true;

                    while (the_planet.next_planet != null && the_planet.next_planet.a < new_a)
                    {
                        next_planet = the_planet.next_planet;

                        if (the_planet == curPlanet)
                            curPlanet = next_planet;
                        else
                            prev_planet.next_planet = next_planet;

                        the_planet.next_planet = next_planet.next_planet;
                        next_planet.next_planet = the_planet;
                        prev_planet = next_planet;
                    }
                }

                finished = true;
                break;
            }
            else
            {
                prev_planet = the_planet;
            }
        }

        if (!(finished))			// Planetesimals didn't collide. Make it a planet.
        {
            the_planet = new PlanetObj();

            the_planet.planetType 			= PlanetObj.planet_type.tUnknown;
            the_planet.a 				= a;
            the_planet.e 				= e;
            the_planet.mass 			= mass;
            the_planet.dust_mass 		= dust_mass;
            the_planet.gas_mass 		= gas_mass;
            the_planet.atmosphere 		= null;
            the_planet.first_moon 		= null;
            the_planet.atmosphere		= null;
            the_planet.albedo			= 0;
            // todo the_planet.gases			= 0;
            the_planet.surf_temp		= 0;
            the_planet.high_temp		= 0;
            the_planet.low_temp		= 0;
            the_planet.max_temp		= 0;
            the_planet.min_temp		= 0;
            the_planet.greenhs_rise	= 0;

            if ((mass >= crit_mass))
                the_planet.gas_giant = true;
            else
                the_planet.gas_giant = false;

            if ((curPlanet == null))
            {
                curPlanet = the_planet;
                the_planet.next_planet = null;
            }
            else if ((a < curPlanet.a))
            {
                the_planet.next_planet = curPlanet;
                curPlanet = the_planet;
            }
            else if ((curPlanet.next_planet == null))
            {
                curPlanet.next_planet = the_planet;
                the_planet.next_planet = null;
            }
            else
            {
                next_planet = curPlanet;
                while (((next_planet != null) && (next_planet.a < a)))
                {
                    prev_planet = next_planet;
                    next_planet = next_planet.next_planet;
                }
                the_planet.next_planet = next_planet;
                prev_planet.next_planet = the_planet;
            }
        }
    }


    PlanetObj dist_planetary_masses(double stell_mass_ratio,
                                         double stell_luminosity_ratio,
                                         double inner_dust,
                                         double outer_dust,
                                         double outer_planet_limit,
                                         double dust_density_coeff,
                                         PlanetObj seed_system,
                                         boolean		 do_moons)
    {
        double 	a;
        double 	e;
        double 	mass;
        double		dust_mass;
        double		gas_mass;
        double 	crit_mass;
        double 	planet_inner_bound;
        double 	planet_outer_bound;
        PlanetObj 	seeds = seed_system;

        set_initial_conditions(inner_dust,outer_dust);
        planet_inner_bound = nearest_planet(stell_mass_ratio);

        if (outer_planet_limit == 0)
            planet_outer_bound = farthest_planet(stell_mass_ratio);
        else
            planet_outer_bound = outer_planet_limit;

        while (dust_left)
        {
            if (seeds != null)
            {
                a = seeds.a;
                e = seeds.e;
                seeds = seeds.next_planet;
            }
            else
            {
                a = Utils.random_number(planet_inner_bound,planet_outer_bound);
                e = Utils.random_eccentricity( );
            }

            mass      = Constants.PROTOPLANET_MASS;
            dust_mass = 0;
            gas_mass  = 0;

            if (dust_available(inner_effect_limit(a, e, mass),
                    outer_effect_limit(a, e, mass)))
            {


                dust_density = dust_density_coeff * Math.sqrt(stell_mass_ratio)
                        * Math.exp(-Constants.ALPHA * Math.pow(a,(1.0 / Constants.N)));
                crit_mass = critical_limit(a,e,stell_luminosity_ratio);
                MassDustGasRecord theRec = accrete_dust(mass, dust_mass, gas_mass,
                    a,e,crit_mass,
                    planet_inner_bound,
                    planet_outer_bound);
                mass = theRec.mass;
                dust_mass = theRec.dust;
                gas_mass = theRec.gas;

                dust_mass += Constants.PROTOPLANET_MASS;

                if (mass > Constants.PROTOPLANET_MASS)
                    coalesce_planetesimals(a,e,mass,crit_mass,
                            dust_mass, gas_mass,
                            stell_luminosity_ratio,
                            planet_inner_bound,planet_outer_bound,
                            do_moons);
                else {
                    //fprintf(stderr, ".. failed due to large neighbor.\n");
                }
            }
            else {

                //fprintf (stderr, ".. failed.\n");
            }
        }
        return curPlanet;
    }



}
