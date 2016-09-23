package com.eweware.phabrik.sim;

import java.util.*;

import com.eweware.phabrik.api.RomanNumeral;
import com.eweware.phabrik.obj.PlanetObj;
import com.eweware.phabrik.obj.SunObj;

/**
 * Created by Dave on 9/2/2016.
 */
public class StarGen {


    /*  These are the global variables used during accretion:  */
    PlanetObj innermost_planet;
    double dust_density_coeff = Constants.DUST_DENSITY_COEFF;


    int flag_verbose = 0;
    // 0x0001			Earthlike count
    // 0x0002			Trace Min/max
    // 0x0004			List habitable
    // 0x0008			List Earth-like (and Sphinx-line)

    // 0x0010			List Gases
    // 0x0020			Trace temp iterations
    // 0x0040			Gas lifetimes
    // 0x0080			List loss of accreted gas mass

    // 0x0100			Injecting, collision
    // 0x0200			Checking..., Failed...
    // 0x0400			List binary info
    // 0x0800			List Gas Dwarfs etc.

    // 0x1000			Moons
    // 0x2000			Oxygen poisoned
    // 0x4000			Trace gas %ages (whoops)
    // 0x8000			Jovians in habitable zone

    // 0x10000			List type diversity
    // 0x20000			Trace Surface temp interations
    // 0x40000			Lunar orbits

    long flag_seed = 0;

    int earthlike = 0;
    int total_earthlike = 0;
    int habitable = 0;
    int habitable_jovians = 0;
    int total_habitable = 0;

    double min_breathable_terrestrial_g = 1000.0;
    double min_breathable_g = 1000.0;
    double max_breathable_terrestrial_g = 0.0;
    double max_breathable_g = 0.0;
    double min_breathable_temp = 1000.0;
    double max_breathable_temp = 0.0;
    double min_breathable_p = 100000.0;
    double max_breathable_p = 0.0;
    double min_breathable_terrestrial_l = 1000.0;
    double min_breathable_l = 1000.0;
    double max_breathable_terrestrial_l = 0.0;
    double max_breathable_l = 0.0;
    double max_moon_mass = 0.0;


    int type_counts[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int type_count = 0;
    int max_type_count = 0;

    static double EM(double x) {
        return x / Constants.SUN_MASS_IN_EARTH_MASSES;
    }

    static double AVE(double x, double y) {
        return ((x + y) / 2);
    }


    /*	No Orbit Eccen. Tilt   Mass    Gas Giant? Dust Mass   Gas */
    public static PlanetObj smallest = new PlanetObj(0, 0.0, 0.0, 20.0, EM(0.4), false, EM(0.4), 0.0);
    public static PlanetObj average = new PlanetObj(0, 0.0, 0.0, 20.0, EM(1.0), false, EM(1.0), 0.0);
    public static PlanetObj largest = new PlanetObj(0, 0.0, 0.0, 20.0, EM(1.6), false, EM(1.6), 0.0);


    public static ChemTable gases[] =
            {
                    //   An   sym   HTML symbol                      name                 Aw      melt    boil    dens       ABUNDe       ABUNDs         Rea	Max inspired pp
                    new ChemTable(Constants.AN_H, "H", "Hydrogen", 1.0079, 14.06, 20.40, 8.99e-05, 0.00125893, 27925.4, 1, 0.0),
                    new ChemTable(Constants.AN_HE, "He", "Helium", 4.0026, 3.46, 4.20, 0.0001787, 7.94328e-09, 2722.7, 0, Constants.MAX_HE_IPP),
                    new ChemTable(Constants.AN_N, "N", "Nitrogen", 14.0067, 63.34, 77.40, 0.0012506, 1.99526e-05, 3.13329, 0, Constants.MAX_N2_IPP),
                    new ChemTable(Constants.AN_O, "O", "Oxygen", 15.9994, 54.80, 90.20, 0.001429, 0.501187, 23.8232, 10, Constants.MAX_O2_IPP),
                    new ChemTable(Constants.AN_NE, "Ne", "Neon", 20.1700, 24.53, 27.10, 0.0009, 5.01187e-09, 3.4435e-5, 0, Constants.MAX_NE_IPP),
                    new ChemTable(Constants.AN_AR, "Ar", "Argon", 39.9480, 84.00, 87.30, 0.0017824, 3.16228e-06, 0.100925, 0, Constants.MAX_AR_IPP),
                    new ChemTable(Constants.AN_KR, "Kr", "Krypton", 83.8000, 116.60, 119.70, 0.003708, 1e-10, 4.4978e-05, 0, Constants.MAX_KR_IPP),
                    new ChemTable(Constants.AN_XE, "Xe", "Xenon", 131.3000, 161.30, 165.00, 0.00588, 3.16228e-11, 4.69894e-06, 0, Constants.MAX_XE_IPP),
                    //                                                                     from here down, these columns were originally: 0.001,         0
                    new ChemTable(Constants.AN_NH3, "NH3", "Ammonia", 17.0000, 195.46, 239.66, 0.001, 0.002, 0.0001, 1, Constants.MAX_NH3_IPP),
                    new ChemTable(Constants.AN_H2O, "H2O", "Water", 18.0000, 273.16, 373.16, 1.000, 0.03, 0.001, 0, 0.0),
                    new ChemTable(Constants.AN_CO2, "CO2", "CarbonDioxide", 44.0000, 194.66, 194.66, 0.001, 0.01, 0.0005, 0, Constants.MAX_CO2_IPP),
                    new ChemTable(Constants.AN_O3, "O3", "Ozone", 48.0000, 80.16, 161.16, 0.001, 0.001, 0.000001, 2, Constants.MAX_O3_IPP),
                    new ChemTable(Constants.AN_CH4, "CH4", "Methane", 16.0000, 90.16, 109.16, 0.010, 0.005, 0.0001, 1, Constants.MAX_CH4_IPP),
                    new ChemTable(0, "", "", 0, 0, 0, 0, 0, 0, 0, 0)
            };


    int max_gas = gases.length;
    Random theRnd = new Random();
    Accrete accrete = new Accrete();
    Enviro enviro = new Enviro();


    void init() {
        if (flag_seed == 0) {
            flag_seed = System.nanoTime();
        }

        theRnd.setSeed(flag_seed);

    }

    SunObj generate_stellar_system(SunObj sun,
                                   PlanetObj seed_system,
                                   String system_name,
                                   double outer_planet_limit,
                                   boolean do_gases,
                                   boolean do_moons) {
        double outer_dust_limit;

        if ((sun.mass < 0.2) || (sun.mass > 1.5))
            sun.mass = Utils.random_number(0.7, 1.4);

        outer_dust_limit = accrete.stellar_dust_limit(sun.mass);

        if (sun.luminosity == 0)
            sun.luminosity = enviro.luminosity(sun.mass);

        sun.r_ecosphere = Math.sqrt(sun.luminosity);
        sun.life = 1.0E10 * (sun.mass / sun.luminosity);


        double min_age = 1.0E9;
        double max_age = 6.0E9;

        if (sun.life < max_age)
            max_age = sun.life;

        innermost_planet = accrete.dist_planetary_masses(sun.mass,
                sun.luminosity,
                0.0, outer_dust_limit,
                outer_planet_limit,
                dust_density_coeff,
                seed_system,
                do_moons);

        sun.age = Utils.random_number(min_age, max_age);


        generate_planets(sun,
                system_name,
                do_gases,
                do_moons);

        return sun;
    }

    void calculate_gases(SunObj sun, PlanetObj planet, String planet_id) {
        if (planet.surf_pressure > 0) {
            double[] amount = new double[max_gas + 1];
            double totamount = 0;
            double pressure = planet.surf_pressure / Constants.MILLIBARS_PER_BAR;
            int n = 0;
            int i;

            for (i = 0; i < max_gas; i++) {
                double yp = gases[i].boil /
                        (373. * ((Math.log((pressure) + 0.001) / -5050.5) +
                                (1.0 / 373.)));

                if ((yp >= 0 && yp < planet.low_temp)
                        && (gases[i].weight >= planet.molec_weight)) {
                    double vrms = enviro.rms_vel(gases[i].weight, planet.exospheric_temp);
                    double pvrms = Math.pow(1 / (1 + vrms / planet.esc_velocity), sun.age / 1e9);
                    double abund = gases[i].abunds; 				/* gases[i].abunde */
                    double react = 1.0;
                    double fract = 1.0;
                    double pres2 = 1.0;

                    if (gases[i].symbol.compareTo("Ar") == 0) {
                        react = .15 * sun.age / 4e9;
                    } else if (gases[i].symbol.compareTo("He") == 0) {
                        abund = abund * (0.001 + (planet.gas_mass / planet.mass));
                        pres2 = (0.75 + pressure);
                        react = Math.pow(1 / (1 + gases[i].reactivity),
                                sun.age / 2e9 * pres2);
                    } else if ((gases[i].symbol.compareTo("O") == 0 ||
                            gases[i].symbol.compareTo("O2") == 0) &&
                            sun.age > 2e9 &&
                            planet.surf_temp > 270 && planet.surf_temp < 400) {
                /*	pres2 = (0.65 + pressure/2);			Breathable - M: .55-1.4 	*/
                        pres2 = (0.89 + pressure / 4);		/*	Breathable - M: .6 -1.8 	*/
                        react = Math.pow(1 / (1 + gases[i].reactivity),
                                Math.pow(sun.age / 2e9, 0.25) * pres2);
                    } else if (gases[i].symbol.compareTo("CO2") == 0 &&
                            sun.age > 2e9 &&
                            planet.surf_temp > 270 && planet.surf_temp < 400) {
                        pres2 = (0.75 + pressure);
                        react = Math.pow(1 / (1 + gases[i].reactivity),
                                Math.pow(sun.age / 2e9, 0.5) * pres2);
                        react *= 1.5;
                    } else {
                        pres2 = (0.75 + pressure);
                        react = Math.pow(1 / (1 + gases[i].reactivity),
                                sun.age / 2e9 * pres2);
                    }

                    fract = (1 - (planet.molec_weight / gases[i].weight));

                    amount[i] = abund * pvrms * react * fract;

                    totamount += amount[i];
                    if (amount[i] > 0.0)
                        n++;
                } else
                    amount[i] = 0.0;
            }

            if (n > 0) {
                planet.gases = n;
                planet.atmosphere = new ArrayList<Gas>();

                for (i = 0, n = 0; i < max_gas; i++) {
                    if (amount[i] > 0.0) {
                        Gas newGas = new Gas();
                        newGas.num = gases[i].num;
                        newGas.surf_pressure = planet.surf_pressure * amount[i] / totamount;
                        planet.atmosphere.add(newGas);


                        n++;
                    }
                }

                Collections.sort(planet.atmosphere, new Comparator<Gas>() {
                    @Override
                    public int compare(Gas o1, Gas o2) {
                        return diminishing_pressure(o1, o2);
                    }
                });


            }

        }
    }

    void generate_planet(PlanetObj planet,
                         int planet_no,
                         SunObj sun,
                         String planet_id,
                         boolean do_gases,
                         boolean do_moons,
                         boolean is_moon) {
        planet.atmosphere = null;
        planet.gases = 0;
        planet.surf_temp = 0;
        planet.high_temp = 0;
        planet.low_temp = 0;
        planet.max_temp = 0;
        planet.min_temp = 0;
        planet.greenhs_rise = 0;
        planet.planet_no = planet_no;
        planet.sun = sun;
        planet.resonant_period = false;

        planet.orbit_zone = enviro.orb_zone(sun.luminosity, planet.a);

        planet.orb_period = enviro.period(planet.a, planet.mass, sun.mass);
        planet.axial_tilt = enviro.inclination(planet.a);

        planet.exospheric_temp = Constants.EARTH_EXOSPHERE_TEMP / Utils.pow2(planet.a / sun.r_ecosphere);
        planet.rms_velocity = enviro.rms_vel(Constants.MOL_NITROGEN, planet.exospheric_temp);
        planet.core_radius = enviro.kothari_radius(planet.dust_mass, false, planet.orbit_zone);

        // Calculate the radius as a gas giant, to verify it will retain gas.
        // Then if mass > Earth, it's at least 5% gas and retains He, it's
        // some flavor of gas giant.

        planet.density = enviro.empirical_density(planet.mass, planet.a, sun.r_ecosphere, true);
        planet.radius = enviro.volume_radius(planet.mass, planet.density);

        planet.surf_accel = enviro.acceleration(planet.mass, planet.radius);
        planet.surf_grav = enviro.gravity(planet.surf_accel);

        planet.molec_weight = enviro.min_molec_weight(planet);

        if (((planet.mass * Constants.SUN_MASS_IN_EARTH_MASSES) > 1.0)
                && ((planet.gas_mass / planet.mass) > 0.05)
                && (enviro.min_molec_weight(planet) <= 4.0)) {
            if ((planet.gas_mass / planet.mass) < 0.20)
                planet.planetType = PlanetObj.planet_type.tSubSubGasGiant;
            else if ((planet.mass * Constants.SUN_MASS_IN_EARTH_MASSES) < 20.0)
                planet.planetType = PlanetObj.planet_type.tSubGasGiant;
            else
                planet.planetType = PlanetObj.planet_type.tGasGiant;
        } else { // If not, it's rocky.
            planet.radius = enviro.kothari_radius(planet.mass, false, planet.orbit_zone);
            planet.density = enviro.volume_density(planet.mass, planet.radius);

            planet.surf_accel = enviro.acceleration(planet.mass, planet.radius);
            planet.surf_grav = enviro.gravity(planet.surf_accel);

            if ((planet.gas_mass / planet.mass) > 0.000001) {
                double h2_mass = planet.gas_mass * 0.85;
                double he_mass = (planet.gas_mass - h2_mass) * 0.999;

                double h2_loss = 0.0;
                double he_loss = 0.0;


                double h2_life = enviro.gas_life(Constants.MOL_HYDROGEN, planet);
                double he_life = enviro.gas_life(Constants.HELIUM, planet);

                if (h2_life < sun.age) {
                    h2_loss = ((1.0 - (1.0 / Math.exp(sun.age / h2_life))) * h2_mass);

                    planet.gas_mass -= h2_loss;
                    planet.mass -= h2_loss;

                    planet.surf_accel = enviro.acceleration(planet.mass, planet.radius);
                    planet.surf_grav = enviro.gravity(planet.surf_accel);
                }

                if (he_life < sun.age) {
                    he_loss = ((1.0 - (1.0 / Math.exp(sun.age / he_life))) * he_mass);

                    planet.gas_mass -= he_loss;
                    planet.mass -= he_loss;

                    planet.surf_accel = enviro.acceleration(planet.mass, planet.radius);
                    planet.surf_grav = enviro.gravity(planet.surf_accel);
                }
            }
        }


        planet.day = enviro.day_length(planet);	/* Modifies planet.resonant_period */
        planet.esc_velocity = enviro.escape_vel(planet.mass, planet.radius);

        if ((planet.planetType == PlanetObj.planet_type.tGasGiant)
                || (planet.planetType == PlanetObj.planet_type.tSubGasGiant)
                || (planet.planetType == PlanetObj.planet_type.tSubSubGasGiant)) {
            planet.greenhouse_effect = false;
            planet.volatile_gas_inventory = Constants.INCREDIBLY_LARGE_NUMBER;
            planet.surf_pressure = Constants.INCREDIBLY_LARGE_NUMBER;

            planet.boil_point = Constants.INCREDIBLY_LARGE_NUMBER;

            planet.surf_temp = Constants.INCREDIBLY_LARGE_NUMBER;
            planet.greenhs_rise = 0;
            planet.albedo = Utils.about(Constants.GAS_GIANT_ALBEDO, 0.1);
            planet.hydrosphere = 1.0;
            planet.cloud_cover = 1.0;
            planet.ice_cover = 0.0;
            planet.surf_grav = enviro.gravity(planet.surf_accel);
            planet.molec_weight = enviro.min_molec_weight(planet);
            planet.surf_grav = Constants.INCREDIBLY_LARGE_NUMBER;
            planet.estimated_temp = enviro.est_temp(sun.r_ecosphere, planet.a, planet.albedo);
            planet.estimated_terr_temp = enviro.est_temp(sun.r_ecosphere, planet.a, Constants.EARTH_ALBEDO);

            double temp = planet.estimated_terr_temp;

            if ((temp >= Constants.FREEZING_POINT_OF_WATER)
                    && (temp <= Constants.EARTH_AVERAGE_KELVIN + 10.)
                    && (sun.age > 2.0E9)) {
                planet.habitable_jovian = true;
                habitable_jovians++;

            }
        } else {
            planet.estimated_temp = enviro.est_temp(sun.r_ecosphere, planet.a, Constants.EARTH_ALBEDO);
            planet.estimated_terr_temp = enviro.est_temp(sun.r_ecosphere, planet.a, Constants.EARTH_ALBEDO);

            planet.surf_grav = enviro.gravity(planet.surf_accel);
            planet.molec_weight = enviro.min_molec_weight(planet);

            planet.greenhouse_effect = enviro.grnhouse(sun.r_ecosphere, planet.a);
            planet.volatile_gas_inventory = enviro.vol_inventory(planet.mass,
                    planet.esc_velocity,
                    planet.rms_velocity,
                    sun.mass,
                    planet.orbit_zone,
                    planet.greenhouse_effect,
                    (planet.gas_mass
                            / planet.mass) > 0.000001);
            planet.surf_pressure = enviro.pressure(planet.volatile_gas_inventory,
                    planet.radius,
                    planet.surf_grav);

            if ((planet.surf_pressure == 0.0))
                planet.boil_point = 0.0;
            else
                planet.boil_point = enviro.boiling_point(planet.surf_pressure);

            enviro.iterate_surface_temp(planet);		/*	Sets:
                                                 *		planet.surf_temp
												 *		planet.greenhs_rise
												 *		planet.albedo
												 *		planet.hydrosphere
												 *		planet.cloud_cover
												 *		planet.ice_cover
												 */

            if (do_gases &&
                    (planet.max_temp >= Constants.FREEZING_POINT_OF_WATER) &&
                    (planet.min_temp <= planet.boil_point))
                calculate_gases(sun, planet, planet_id);

			/*
             *	Next we assign a type to the planet.
			 */

            if (planet.surf_pressure < 1.0) {
                if (!is_moon
                        && ((planet.mass * Constants.SUN_MASS_IN_EARTH_MASSES) < Constants.ASTEROID_MASS_LIMIT))
                    planet.planetType = PlanetObj.planet_type.tAsteroids;
                else
                    planet.planetType = PlanetObj.planet_type.tRock;
            } else if ((planet.surf_pressure > 6000.0) &&
                    (planet.molec_weight <= 2.0))    // Retains Hydrogen
            {
                planet.planetType = PlanetObj.planet_type.tSubSubGasGiant;
                planet.gases = 0;
                planet.atmosphere = null;
            } else {                                        // Atmospheres:
                if (((int) planet.day == (int) (planet.orb_period * 24.0) ||
                        (planet.resonant_period)))
                    planet.planetType = PlanetObj.planet_type.t1Face;
                else if (planet.hydrosphere >= 0.95)
                    planet.planetType = PlanetObj.planet_type.tWater;                // >95% water
                else if (planet.ice_cover >= 0.95)
                    planet.planetType = PlanetObj.planet_type.tIce;                // >95% ice
                else if (planet.hydrosphere > 0.05)
                    planet.planetType = PlanetObj.planet_type.tTerrestrial;        // Terrestrial
                    // else <5% water
                else if (planet.max_temp > planet.boil_point)
                    planet.planetType = PlanetObj.planet_type.tVenusian;            // Hot = Venusian
                else if ((planet.gas_mass / planet.mass) > 0.0001) {                                        // Accreted gas
                    planet.planetType = PlanetObj.planet_type.tIce;                // But no Greenhouse
                    planet.ice_cover = 1.0;            // or liquid water
                }                                        // Make it an Ice World
                else if (planet.surf_pressure <= 250.0)// Thin air = Martian
                    planet.planetType = PlanetObj.planet_type.tMartian;
                else if (planet.surf_temp < Constants.FREEZING_POINT_OF_WATER)
                    planet.planetType = PlanetObj.planet_type.tIce;
                else {
                    planet.planetType = PlanetObj.planet_type.tUnknown;


                }
            }
        }

        if (do_moons && !is_moon) {
            if (planet.first_moon != null) {
                int n;
                PlanetObj ptr;

                for (n = 0, ptr = planet.first_moon;
                     ptr != null;
                     ptr = ptr.next_planet) {
                    if (ptr.mass * Constants.SUN_MASS_IN_EARTH_MASSES > .000001) {
                        String moon_id = "";
                        double roche_limit = 0.0;
                        double hill_sphere = 0.0;

                        ptr.a = planet.a;
                        ptr.e = planet.e;

                        n++;


                        generate_planet(ptr, n,
                                sun,
                                moon_id,
                                do_gases,
                                do_moons, true);    // Adjusts ptr.density

                        roche_limit = 2.44 * planet.radius * Math.pow((planet.density / ptr.density), (1.0 / 3.0));
                        hill_sphere = planet.a * Constants.KM_PER_AU * Math.pow((planet.mass / (3.0 * sun.mass)), (1.0 / 3.0));

                        if ((roche_limit * 3.0) < hill_sphere) {
                            ptr.moon_a = Utils.random_number(roche_limit * 1.5, hill_sphere / 2.0) / Constants.KM_PER_AU;
                            ptr.moon_e = Utils.random_eccentricity();
                        } else {
                            ptr.moon_a = 0;
                            ptr.moon_e = 0;
                        }


                    }
                }
            }
        }

    }

    void check_planet(PlanetObj planet, String planet_id, boolean is_moon) {

        int tIndex = 0;

        switch (planet.planetType) {
            case tUnknown:
                tIndex = 0;
                break;
            case tRock:
                tIndex = 1;
                break;
            case tVenusian:
                tIndex = 2;
                break;
            case tTerrestrial:
                tIndex = 3;
                break;
            case tSubSubGasGiant:
                tIndex = 4;
                break;
            case tSubGasGiant:
                tIndex = 5;
                break;
            case tGasGiant:
                tIndex = 6;
                break;
            case tMartian:
                tIndex = 7;
                break;
            case tWater:
                tIndex = 8;
                break;
            case tIce:
                tIndex = 9;
                break;
            case tAsteroids:
                tIndex = 10;
                break;
            case t1Face:
                tIndex = 11;
                break;
        }

        if (type_counts[tIndex] == 0)
            ++type_count;

        ++type_counts[tIndex];



	/* Check for and list planets with breathable atmospheres */
        PlanetObj.breathabilty_type breathe = enviro.breathability(planet);

        if ((breathe == PlanetObj.breathabilty_type.BREATHABLE) &&
                (!planet.resonant_period) &&        // Option needed?
                true //((int) planet.day != (int) (planet.orb_period * 24.0))
                ) {
            double illumination = Utils.pow2(1.0 / planet.a) * (planet.sun).luminosity;
            planet.habitable = true;

            habitable++;

            if (min_breathable_temp > planet.surf_temp)
                min_breathable_temp = planet.surf_temp;

            if (max_breathable_temp < planet.surf_temp)
                max_breathable_temp = planet.surf_temp;

            if (min_breathable_g > planet.surf_grav)
                min_breathable_g = planet.surf_grav;

            if (max_breathable_g < planet.surf_grav)
                max_breathable_g = planet.surf_grav;

            if (min_breathable_l > illumination)
                min_breathable_l = illumination;


            if (max_breathable_l < illumination)
                max_breathable_l = illumination;


            if (planet.planetType == PlanetObj.planet_type.tTerrestrial) {
                if (min_breathable_terrestrial_g > planet.surf_grav)
                    min_breathable_terrestrial_g = planet.surf_grav;


                if (max_breathable_terrestrial_g < planet.surf_grav)
                    max_breathable_terrestrial_g = planet.surf_grav;


                if (min_breathable_terrestrial_l > illumination)
                    min_breathable_terrestrial_l = illumination;


                if (max_breathable_terrestrial_l < illumination)
                    max_breathable_terrestrial_l = illumination;
            }

            if (min_breathable_p > planet.surf_pressure)
                min_breathable_p = planet.surf_pressure;


            if (max_breathable_p < planet.surf_pressure)
                max_breathable_p = planet.surf_pressure;

        }


        if (is_moon && max_moon_mass < planet.mass) {
            max_moon_mass = planet.mass;

        }


        double rel_temp = (planet.surf_temp - Constants.FREEZING_POINT_OF_WATER) -
                Constants.EARTH_AVERAGE_CELSIUS;
        double seas = (planet.hydrosphere * 100.0);
        double clouds = (planet.cloud_cover * 100.0);
        double pressure = (planet.surf_pressure /
                Constants.EARTH_SURF_PRES_IN_MILLIBARS);
        double ice = (planet.ice_cover * 100.0);
        double gravity = planet.surf_grav;
        PlanetObj.breathabilty_type breathe2 = enviro.breathability(planet);

        if ((gravity >= .8) &&
                (gravity <= 1.2) &&
                (rel_temp >= -2.0) &&
                (rel_temp <= 3.0) &&
                (ice <= 10.) &&
                (pressure >= 0.5) &&
                (pressure <= 2.0) &&
                (clouds >= 40.) &&
                (clouds <= 80.) &&
                (seas >= 50.) &&
                (seas <= 80.) &&
                (planet.planetType != PlanetObj.planet_type.tWater) &&
                (breathe2 == PlanetObj.breathabilty_type.BREATHABLE)) {
            planet.earthlike = true;
            earthlike++;

        }
    }

    void generate_planets(SunObj sun,
                          String system_name,
                          boolean do_gases,
                          boolean do_moons) {
        PlanetObj planet;
        int planet_no = 0;
        PlanetObj moon;
        int moons = 0;

        for (planet = innermost_planet, planet_no = 1;
             planet != null;
             planet = planet.next_planet, planet_no++) {
            String planet_id;

            planet_id = String.format("%s %d %d",
                    system_name, flag_seed, planet_no);

            generate_planet(planet, planet_no,
                    sun,
                    planet_id,
                    do_gases, do_moons, false);

		/*
		 *	Now we're ready to test for habitable planets,
		 *	so we can count and log them and such
		 */

            check_planet(planet, planet_id, false);

            for (moon = planet.first_moon, moons = 1;
                 moon != null;
                 moon = moon.next_planet, moons++) {
                String moon_id;
                moon_id = String.format("%s.%d", planet_id, moons);
                check_planet(moon, moon_id, true);
            }
        }
    }

/*
 *  Sort a ChemTable by decreasing abundance.
 */

    static int diminishing_abundance(ChemTable x, ChemTable y) {

        double xx = x.abunds * x.abunde;
        double yy = y.abunds * y.abunde;

        if (xx < yy)
            return +1;
        else
            return (xx > yy ? -1 : 0);
    }

/*
 *  Sort a ChemTable by decreasing pressure.
 */

    static int diminishing_pressure(Gas x, Gas y) {
        if (x.surf_pressure < y.surf_pressure)
            return +1;
        return (x.surf_pressure > y.surf_pressure ? -1 : 0);
    }

    public SunObj GenerateSystem(String sys_name_arg,
                               double mass_arg

    ) {
        SunObj sun = new SunObj();

        int index = 0;
        boolean do_gases = true;

        boolean do_moons = true;


        for (index = 0; index < max_gas; index++) {
            if (gases[index].max_ipp == 0.0)
                gases[index].max_ipp = Constants.INCREDIBLY_LARGE_NUMBER;
        }


        Arrays.sort(gases, new Comparator<ChemTable>() {
            @Override
            public int compare(ChemTable o1, ChemTable o2) {
                return diminishing_abundance(o1, o2);
            }
        });

        sun.mass = mass_arg;



        String system_name;
        double outer_limit = 0.0;
        int sys_no = 0;
        PlanetObj seed_planets = null;

        init();

        if (sys_name_arg != null) {
            system_name = String.format("%s", sys_name_arg);
        } else {
            system_name = String.format("Unknown System %ld-%LG", flag_seed, sun.mass);
        }

        outer_limit = 0;


        sun.name = system_name;

        earthlike = 0;
        habitable = 0;
        habitable_jovians = 0;

        for (int i = 0; i < 12; i++)
            type_counts[i] = 0;

        type_count = 0;


        sun = generate_stellar_system(sun,
                seed_planets,    // solar_system
                system_name,
                outer_limit,
                do_gases,
                do_moons);


        PlanetObj planet;
        int counter;
        int wt_type_count = type_count;
        int norm_type_count = 0;

        if (type_counts[3] > 0) wt_type_count += 20;    // Terrestrial
        if (type_counts[8] > 0) wt_type_count += 18;    // Water
        if (type_counts[2] > 0) wt_type_count += 16;    // Venusian
        if (type_counts[7] > 0) wt_type_count += 15;    // Martian
        if (type_counts[9] > 0) wt_type_count += 14;    // Ice
        if (type_counts[10] > 0) wt_type_count += 13;    // Asteroids
        if (type_counts[4] > 0) wt_type_count += 12;    // Gas Dwarf
        if (type_counts[5] > 0) wt_type_count += 11;    // Sub_Jovian
        if (type_counts[11] > 0) wt_type_count += 10;    // 1-Face
        if (type_counts[1] > 0) wt_type_count += 3;        // Rock
        if (type_counts[6] > 0) wt_type_count += 2;        // Jovian
        if (type_counts[0] > 0) wt_type_count += 1;        // Unknown

        for (planet = innermost_planet, counter = 0;
             planet != null;
             planet = planet.next_planet, counter++)
            ;

        norm_type_count = wt_type_count - (counter - type_count);

        if (max_type_count < norm_type_count) {
            max_type_count = norm_type_count;
        }


        total_habitable += habitable;
        total_earthlike += earthlike;

        sun.planets = new ArrayList<PlanetObj>();
        PlanetObj curPlanet = innermost_planet;
        counter = 1;
        while (curPlanet != null) {
            sun.planets.add(curPlanet);
            curPlanet.planetName = sun.name + " " + RomanNumeral.toRoman(counter++);

            // listify the moons
            if (curPlanet.first_moon != null) {
                int moonCounter = 1;
                curPlanet.moonList = new ArrayList<PlanetObj>();
                PlanetObj curMoon = curPlanet.first_moon;
                 while (curMoon != null) {
                     curPlanet.moonList.add(curMoon);
                     curMoon.planetName = curPlanet.planetName + " moon " + RomanNumeral.toRoman(moonCounter++);
                     curMoon = curMoon.next_planet;
                 }
            }

            curPlanet = curPlanet.next_planet;
        }

        // fill in some quick stats
        sun.earthlike = earthlike;
        sun.habitable = habitable;
        sun.habitable_jovians = habitable_jovians;

        return sun;
    }
}
