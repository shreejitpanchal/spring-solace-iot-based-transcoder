package com.solace.samples.spring.iot.iotshreejittranscoderdemo.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Reference Website //https://www.delftstack.com/howto/java/java-ffmpeg/
public class FFMPEG_Wrapper_Service {

    //main
    public String startFFMPEGTranscoding(String bitRate,String conversionBitRate, String fileName, String subFolderPath) {
        System.out.println("----------FFMPEG Start------------");
        Map<String, String> myBitRateMap = new HashMap<String, String>() {{
            put("144", "426x240");
            put("360", "640x360");
            put("480", "854x480");
            put("720", "1280x720");
            put("1080", "1920x1080");
        }};
        //try block
        try {
            //create process
            Runtime rt = Runtime.getRuntime();
            //save the location
            File folder = new File(subFolderPath);
            //save all files in an array that are retrieved from the specified folder
            //File[] file = folder.listFiles();

            /*
            for each filename, open the command prompt
            and execute the specified command.
             */

            System.out.println("Execute EXEC - Start");
            rt.exec("cmd.exe /c start " + "ffmpeg -i " + fileName
                    + " -c:a copy -c:v libx264 -s "+myBitRateMap.get(conversionBitRate)+" output_"+conversionBitRate+"_" + fileName + ".mp4", null, folder);
            //for (int i = 0; i < file.length; i++) {
//                rt.exec("cmd.exe /c start "
//                                + "ffmpeg -i " + file[i].getName()
//                                + " -c:a copy -c:v libx264 -s 426x240 output" + (i + 1) + ".mp4", null,
//                        folder);
            System.out.println("Execute EXEC - End");
        }//end for
        catch (IOException e) {
            System.out.println(e);
            return "Failure";
        }//end catch
        System.out.println("----------FFMPEG End------------");
        return "Success";

    } //end try

}//end main

