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


public enum TissueModelType  {
    ZHL_16C((short)0),
    INVALID((short)255);

    protected short value;

    private TissueModelType(short value) {
        this.value = value;
    }

    public static TissueModelType getByValue(final Short value) {
        for (final TissueModelType type : TissueModelType.values()) {
            if (value == type.value)
                return type;
        }

        return TissueModelType.INVALID;
    }

    /**
     * Retrieves the String Representation of the Value
     * @return The string representation of the value
     */
    public static String getStringFromValue( TissueModelType value ) {
        return value.name();
    }

    public short getValue() {
        return value;
    }


}