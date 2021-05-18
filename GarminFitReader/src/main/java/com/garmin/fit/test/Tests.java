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


package com.garmin.fit.test;

import com.garmin.fit.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;


public class Tests {
   private MesgBroadcaster broadcaster;
   private ArrayList<Test> tests;
   
   public Tests() {
      broadcaster = new MesgBroadcaster();
      tests = new ArrayList<Test>();

      {
         FileIdTest testFileId = new FileIdTest();
         MesgIndexTest testMesgIndex = new MesgIndexTest();
         ActivityFileTimeStampTest testActivityFileTimeStamp = new ActivityFileTimeStampTest();
         
         tests.add(testFileId);
         tests.add(testMesgIndex);
         tests.add(testActivityFileTimeStamp);
         
         broadcaster.addListener(testFileId);
         broadcaster.addListener(testMesgIndex);
         broadcaster.addListener(testActivityFileTimeStamp);
      }
      
   }

   public boolean run(String fileName) {
      boolean pass = true;
      
      try {
         broadcaster.run((InputStream) new FileInputStream(fileName));
         
         for (int i=0; i<tests.size(); i++)
         {
            String error = tests.get(i).getError();
            
            if ((error != null) && (error.compareTo("") != 0))
            {
               pass = false;
               System.err.println(error);
            }
         }
      } catch (java.io.IOException e) {
         throw new RuntimeException(e);
      }
      
      return pass;
   }
}
