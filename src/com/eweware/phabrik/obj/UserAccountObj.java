package com.eweware.phabrik.obj;

import org.joda.time.DateTime;

/**
 * Created by davidvronay on 9/5/16.
 */
public class UserAccountObj {
    public long Id;
    public String U;    // username
    public String D;    // digest
    public String S;    // salt
    public DateTime c;      // account creation date
}
