package com.eweware.phabrik.obj;

import org.joda.time.DateTime;

import java.awt.*;
import java.util.List;

/**
 * Created by davidvronay on 8/23/16.
 */
public class PlayerObj {
    public long Id;
    public List<PointOfPresenceObj> popList;
    public boolean isAdmin;
    public String playerName;
    public DateTime lastlogin;

}
