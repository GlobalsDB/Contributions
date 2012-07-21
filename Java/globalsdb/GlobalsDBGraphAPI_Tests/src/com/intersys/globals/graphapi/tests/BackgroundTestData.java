package com.intersys.globals.graphapi.tests;

import java.util.Random;

public class BackgroundTestData {
    public BackgroundTestData() {
        super();
    }
    
    public static final String invalidGlobalNodeNames[] = {
        "tstGraphSimple-20-09:" ,
        "tstGraphSimple-20-09"
    };
    
    public static Random randomIz = new Random();
    public static final String rndChars = "abcdefghijklmNOPQRSTUVWXYZ";
    
    public static String rndString() {
        int stringLen = 0;
        StringBuffer newRstr = new StringBuffer();
        while (stringLen++ < 20) {
            newRstr.append(rndChars.charAt(randomIz.nextInt(rndChars.length())));
        }
        return newRstr.toString();
    }


    
}
