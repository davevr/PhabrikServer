package com.eweware.phabrik.obj;

import java.util.List;

/**
 * Created by davidvronay on 8/23/16.
 */
public class SolSysObj {
    public long Id;
    public int xLoc;
    public int yLoc;
    public int zLoc;
    public long discovererId;
    public String systemName;
    public boolean underProtection;

    public List<Long>   sunIdList;
    public List<Long>   planetIdList;
    public List<Long>   anomalyIdList;
    public List<Long>   starbaseIdList;

}
