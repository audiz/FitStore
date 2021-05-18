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


package com.garmin.fit.plugins.examples;

import com.garmin.fit.*;
import com.garmin.fit.plugins.*;
import com.garmin.fit.csv.MesgCSVWriter;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Example demonstrating usage of BufferedMesgBroadcaster and
 * HrToRecordMesgBroadcaster plugin.
 * <p>
 * The example outputs all
 * record message and HR messages to a CSV file with the record
 * messages to match the HR data if their times align
 *
 */
public class HrToRecordReaderExample implements RecordMesgListener, HrMesgListener {
    private MesgCSVWriter mesgWriter;

    public static void main(String[] args) {
        System.out.printf("FIT Hr Record Reader Example Application - Protocol %d.%d Profile %.2f %s\n", Fit.PROTOCOL_VERSION_MAJOR, Fit.PROTOCOL_VERSION_MINOR, Fit.PROFILE_VERSION / 100.0, Fit.PROFILE_TYPE);

        FileInputStream in;

        HrToRecordReaderExample example = new HrToRecordReaderExample();
        Decode decode = new Decode();
        BufferedMesgBroadcaster mesgBroadcaster = new BufferedMesgBroadcaster(decode);

        if (args.length != 1) {
            System.out .println("Usage: java -jar FitHrRecordReaderExample.jar <input file>.fit");
            return;
        }

        try {
            in = new FileInputStream(args[0]);
        }
        catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + args[0]);
        }

        try {
            if (!decode.checkFileIntegrity((InputStream) in)) {
                throw new RuntimeException("FIT file integrity failed.");
            }
        }  catch (RuntimeException e) {
            System.err.print("Exception Checking File Integrity: ");
            System.err.println(e.getMessage());
        }
        finally {
            try {
                in.close();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            in = new FileInputStream(args[0]);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error opening file " + args[0] + " [2]");
        }

        String outputFile = args[0] + "-HrRecordExampleProcessed.csv";
        mesgBroadcaster.addListener((RecordMesgListener) example);
        mesgBroadcaster.addListener((HrMesgListener) example);

        try {
            example.mesgWriter = new MesgCSVWriter(outputFile);

            // Create plugin and register with mesgBroadcaster
            MesgBroadcastPlugin plugin = new HrToRecordMesgBroadcastPlugin();
            mesgBroadcaster.registerMesgBroadcastPlugin(plugin);

            mesgBroadcaster.run(in);      // Run decoder
            mesgBroadcaster.broadcast();  // End of file so flush pending data.
            example.mesgWriter.close();
        }
        catch (FitRuntimeException e) {
            System.err.print("Exception decoding file: ");
            System.err.println(e.getMessage());
        }

        System.out.println("Decoded Record and Hr data from " + args[0] + " to " + outputFile);
    }

    public void onMesg(RecordMesg mesg) {
        mesgWriter.onMesg(mesg);
    }

    public void onMesg(HrMesg mesg) {
        mesgWriter.onMesg(mesg);
    }
}
