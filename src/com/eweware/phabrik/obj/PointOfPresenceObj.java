package com.eweware.phabrik.obj;

import org.joda.time.DateTime;

/**
 * Created by davidvronay on 8/23/16.
 */
public class PointOfPresenceObj {
    public long Id;
    public long playerId;   // player who owns this
    public long targetId;   //
    public DateTime created;
    public DateTime lastactive;
    public StructureObj structure;
}
