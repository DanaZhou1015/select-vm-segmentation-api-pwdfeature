package com.acxiom.ams.util;

/**
 * Created by dchen on 24/05/2017.
 */
public class UUID {

    public static String GetTaxonomyID(){
        return java.util.UUID.randomUUID().toString();
    }
}
