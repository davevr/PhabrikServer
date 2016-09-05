package com.eweware.phabrik.sim;

import java.util.List;

/**
 * Created by Dave on 9/2/2016.
 */
public class DustRecord {
     public double      inner_edge;
    public double       outer_edge;
    public Boolean          dust_present;
    public Boolean          gas_present;
    public List<DustRecord> nextList;

    public DustRecord next () {
        int myLoc = nextList.indexOf(this);
        myLoc++;
        if (myLoc >= nextList.size())
            return null;
        else
            return nextList.get(myLoc);
    }
}
