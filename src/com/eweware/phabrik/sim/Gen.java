package com.eweware.phabrik.sim;

import com.eweware.phabrik.obj.PlanetObj;

import java.util.List;

/**
 * Created by Dave on 9/2/2016.
 */
public class Gen {
    List<DustRecord>    dusts;
    PlanetObj     firstPlanet;
    List<Gen>           nextList;

    public Gen next() {
        int myLoc = nextList.indexOf(this);
        myLoc++;
        if (myLoc >= nextList.size())
            return null;
        else
            return nextList.get(myLoc);
    }
}
