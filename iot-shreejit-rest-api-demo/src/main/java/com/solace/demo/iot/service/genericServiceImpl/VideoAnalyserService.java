package com.solace.demo.iot.service.genericServiceImpl;

import com.solace.demo.iot.common.movieParser.MovieMetadataParser;
import com.solace.demo.iot.common.movieParser.domain.MovieMetadata;
import com.solace.demo.iot.common.movieParser.exception.FileStorageException;
import com.solace.demo.iot.common.movieParser.domain.General;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class VideoAnalyserService {

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    public String getBitRate(String filePathName) {
        System.out.println("coming to getBitRate::"+filePathName);
        try {
            MovieMetadata video = MovieMetadataParser.getInstance().parseFile(filePathName);
            String bitRate = video.get(General.OVERALLBITRATE_STRING).get();
            bitRate = bitRate.substring(0, bitRate.lastIndexOf("K")).replaceAll(" ", "");

            System.out.println("##### Video MetaData Start #####");
            System.out.println("FileName = " + filePathName);
            System.out.println("Video width = " + video.getVideoWidth().get());
            System.out.println("Video height = " + video.getVideoHeight().get());
            System.out.println("Video bitRate = " + bitRate);
            //System.out.println("Video Complete MetaData" + video.toString());
            //System.out.println(video.getGeneralKeys());
            System.out.println("##### Video MetaData End #####");

            return bitRate;
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " + StringUtils.cleanPath(filePathName) + ". Please try again!", ex);
        }
    }

}
