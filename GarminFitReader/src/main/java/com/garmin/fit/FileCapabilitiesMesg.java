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



public class FileCapabilitiesMesg extends Mesg   {

    
    public static final int MessageIndexFieldNum = 254;
    
    public static final int TypeFieldNum = 0;
    
    public static final int FlagsFieldNum = 1;
    
    public static final int DirectoryFieldNum = 2;
    
    public static final int MaxCountFieldNum = 3;
    
    public static final int MaxSizeFieldNum = 4;
    

    protected static final  Mesg fileCapabilitiesMesg;
    static {
        // file_capabilities
        fileCapabilitiesMesg = new Mesg("file_capabilities", MesgNum.FILE_CAPABILITIES);
        fileCapabilitiesMesg.addField(new Field("message_index", MessageIndexFieldNum, 132, 1, 0, "", false, Profile.Type.MESSAGE_INDEX));
        
        fileCapabilitiesMesg.addField(new Field("type", TypeFieldNum, 0, 1, 0, "", false, Profile.Type.FILE));
        
        fileCapabilitiesMesg.addField(new Field("flags", FlagsFieldNum, 10, 1, 0, "", false, Profile.Type.FILE_FLAGS));
        
        fileCapabilitiesMesg.addField(new Field("directory", DirectoryFieldNum, 7, 1, 0, "", false, Profile.Type.STRING));
        
        fileCapabilitiesMesg.addField(new Field("max_count", MaxCountFieldNum, 132, 1, 0, "", false, Profile.Type.UINT16));
        
        fileCapabilitiesMesg.addField(new Field("max_size", MaxSizeFieldNum, 134, 1, 0, "bytes", false, Profile.Type.UINT32));
        
    }

    public FileCapabilitiesMesg() {
        super(Factory.createMesg(MesgNum.FILE_CAPABILITIES));
    }

    public FileCapabilitiesMesg(final Mesg mesg) {
        super(mesg);
    }


    /**
     * Get message_index field
     *
     * @return message_index
     */
    public Integer getMessageIndex() {
        return getFieldIntegerValue(254, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set message_index field
     *
     * @param messageIndex
     */
    public void setMessageIndex(Integer messageIndex) {
        setFieldValue(254, 0, messageIndex, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get type field
     *
     * @return type
     */
    public File getType() {
        Short value = getFieldShortValue(0, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
        if (value == null) {
            return null;
        }
        return File.getByValue(value);
    }

    /**
     * Set type field
     *
     * @param type
     */
    public void setType(File type) {
        setFieldValue(0, 0, type.value, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get flags field
     *
     * @return flags
     */
    public Short getFlags() {
        return getFieldShortValue(1, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set flags field
     *
     * @param flags
     */
    public void setFlags(Short flags) {
        setFieldValue(1, 0, flags, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get directory field
     *
     * @return directory
     */
    public String getDirectory() {
        return getFieldStringValue(2, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set directory field
     *
     * @param directory
     */
    public void setDirectory(String directory) {
        setFieldValue(2, 0, directory, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get max_count field
     *
     * @return max_count
     */
    public Integer getMaxCount() {
        return getFieldIntegerValue(3, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set max_count field
     *
     * @param maxCount
     */
    public void setMaxCount(Integer maxCount) {
        setFieldValue(3, 0, maxCount, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Get max_size field
     * Units: bytes
     *
     * @return max_size
     */
    public Long getMaxSize() {
        return getFieldLongValue(4, 0, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

    /**
     * Set max_size field
     * Units: bytes
     *
     * @param maxSize
     */
    public void setMaxSize(Long maxSize) {
        setFieldValue(4, 0, maxSize, Fit.SUBFIELD_INDEX_MAIN_FIELD);
    }

}
