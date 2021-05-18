////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Garmin Canada Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2021 Garmin Canada Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 21.53Release
// Tag = production/akw/21.53.00-0-g1b82aa2b
////////////////////////////////////////////////////////////////////////////////


package com.garmin.fit;

import java.util.HashMap;
import java.util.Map;

public class SportBits1  {
    public static final short TENNIS = 0x01;
    public static final short AMERICAN_FOOTBALL = 0x02;
    public static final short TRAINING = 0x04;
    public static final short WALKING = 0x08;
    public static final short CROSS_COUNTRY_SKIING = 0x10;
    public static final short ALPINE_SKIING = 0x20;
    public static final short SNOWBOARDING = 0x40;
    public static final short ROWING = 0x80;
    public static final short INVALID = Fit.UINT8Z_INVALID;

    private static final Map<Short, String> stringMap;

    static {
        stringMap = new HashMap<Short, String>();
        stringMap.put(TENNIS, "TENNIS");
        stringMap.put(AMERICAN_FOOTBALL, "AMERICAN_FOOTBALL");
        stringMap.put(TRAINING, "TRAINING");
        stringMap.put(WALKING, "WALKING");
        stringMap.put(CROSS_COUNTRY_SKIING, "CROSS_COUNTRY_SKIING");
        stringMap.put(ALPINE_SKIING, "ALPINE_SKIING");
        stringMap.put(SNOWBOARDING, "SNOWBOARDING");
        stringMap.put(ROWING, "ROWING");
    }


    /**
     * Retrieves the String Representation of the Value
     * @return The string representation of the value, or empty if unknown
     */
    public static String getStringFromValue( Short value ) {
        if( stringMap.containsKey( value ) ) {
            return stringMap.get( value );
        }

        return "";
    }

    /**
     * Retrieves a value given a string representation
     * @return The value or INVALID if unkwown
     */
    public static Short getValueFromString( String value ) {
        for( Map.Entry<Short, String> entry : stringMap.entrySet() ) {
            if( entry.getValue().equals( value ) ) {
                return entry.getKey();
            }
        }

        return INVALID;
    }

}
