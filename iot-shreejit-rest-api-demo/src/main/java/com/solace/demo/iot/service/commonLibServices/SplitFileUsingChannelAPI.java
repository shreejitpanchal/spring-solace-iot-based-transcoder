package com.solace.demo.iot.service.commonLibServices;

import com.solace.demo.iot.service.commonLibServices.MergeSplitFilesUsingChannelAPI;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.nio.file.Files.size;

public class SplitFileUsingChannelAPI {

    // version 3.0
    private static final String dir = "/temp/";
    private static final String suffix = ".splitPart";

     //* @param fileName   Name of file to be split.
     //* @param mBperSplit maximum number of MB per file.

    public void invokesplitFile(String splitFilePath,int mBperSplit)
    {
        try{
            //System.out.println("Total Splits :"+ splitFile(splitFilePath,mBperSplit));
            MergeSplitFilesUsingChannelAPI testInvoke=new MergeSplitFilesUsingChannelAPI();
            testInvoke.mergeFiles(splitFile(splitFilePath,mBperSplit));

        }catch(Exception e)
        {
            System.out.println("Caught : "+e.getMessage());
        }
    }
    public static List<Path> splitFile(final String fileName, final int mBperSplit) throws IOException {
        int positionCounter=1;
        if (mBperSplit <= 0) {
            throw new IllegalArgumentException("mBperSplit must be more than zero");
        }

        List<Path> partFiles = new ArrayList<>();
        final String splitFileNamePrefix = UUID.randomUUID().toString().substring(0, 4);
        final long sourceSize = size(Paths.get(fileName));
        final long bytesPerSplit = 1024L * 1024L * mBperSplit;
        final long numSplits = sourceSize / bytesPerSplit;
        final long remainingBytes = sourceSize % bytesPerSplit;
        int position = 0;

        try (RandomAccessFile sourceFile = new RandomAccessFile(fileName, "r");
             FileChannel sourceChannel = sourceFile.getChannel()) {

            for (; position < numSplits; position++) {
                //write multipart files.
                writePartToFile(bytesPerSplit, position * bytesPerSplit, sourceChannel, partFiles,splitFileNamePrefix,positionCounter);
                positionCounter++;
            }

            if (remainingBytes > 0) {
                writePartToFile(remainingBytes, position * bytesPerSplit, sourceChannel, partFiles,splitFileNamePrefix, positionCounter);
            }
        }
        return partFiles;
    }

    private static void writePartToFile(long byteSize, long position, FileChannel sourceChannel, List<Path> partFiles,String splitFileNamePrefix, int positionCounter) throws IOException {
        Path fileName = Paths.get(dir + splitFileNamePrefix + suffix + "-"+ positionCounter);
        try (RandomAccessFile toFile = new RandomAccessFile(fileName.toFile(), "rw");
             FileChannel toChannel = toFile.getChannel()) {
            sourceChannel.position(position);
            toChannel.transferFrom(sourceChannel, 0, byteSize);
        }
        partFiles.add(fileName);
    }
}
