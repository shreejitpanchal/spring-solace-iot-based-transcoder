package com.solace.demo.iot.service.commonLibServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.List;

public class MergeSplitFilesUsingChannelAPI
{
    public static void main(String[] argv) throws Exception
    {
        MergeSplitFilesUsingChannelAPI a=new MergeSplitFilesUsingChannelAPI();
        //a.mergeFiles();
    }
    
    public void mergeFiles(List<Path> partFiles)
    {
        try {


            //Input files
            //String[] inputFiles = new String[]{"/temp/test.splitPart-1", "/temp/test.splitPart-2"};

            //Files contents will be written in these files
            String outputFile = "/temp/test.merge.txt";

            //Get channel for output file
            FileOutputStream fos = new FileOutputStream(new File(outputFile));
            WritableByteChannel targetChannel = fos.getChannel();

            //for (int i = 0; i < inputFiles.length; i++) {
            for (int i = 0; i < partFiles.size(); i++) {
                //Get channel for input files
                FileInputStream fis = new FileInputStream(partFiles.get(i).toFile());
                FileChannel inputChannel = fis.getChannel();

                //Transfer data from input channel to output channel
                inputChannel.transferTo(0, inputChannel.size(), targetChannel);

                //close the input channel
                inputChannel.close();
                fis.close();
            }

            //finally close the target channel
            targetChannel.close();
            fos.close();
        }
        catch(Exception e)
        {
            System.out.println("Caught inside MergeSplitClass :"+e.getMessage());
        }
    }
}
