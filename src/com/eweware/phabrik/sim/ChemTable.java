package com.eweware.phabrik.sim;

/**
 * Created by Dave on 9/2/2016.
 */
public class ChemTable {
    public int         	num;
    public String  symbol;
    public String name;
    public double	weight;
    public double	melt;
    public double	boil;
    public double	density;
    public double	abunde;
    public double	abunds;
    public double	reactivity;
    public double	max_ipp;	// Max inspired partial pressure im millibars


    public ChemTable(int theNum, String theSym, String theName, double aw, double meltPoint, double boilPoint, double dens, double ae, double ab, double react, double max) {
        num = theNum;
        symbol = theSym;
        name = theName;
        weight = aw;
        melt = meltPoint;
        boil = boilPoint;
        density = dens;
        abunde = ae;
        abunds = ab;
        reactivity = react;
        max_ipp = max;


    }
}
