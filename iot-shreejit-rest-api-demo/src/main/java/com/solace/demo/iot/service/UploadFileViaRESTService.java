package com.solace.demo.iot.service;

import com.solace.demo.iot.model.uploadFileResponse;
import com.solace.demo.iot.service.genericServiceImpl.FileStorageService;
import com.solace.demo.iot.service.genericServiceImpl.SolaceTopicPublisherService;
import com.solace.demo.iot.service.genericServiceImpl.VideoAnalyserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class UploadFileViaRESTService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private VideoAnalyserService videoAnalyserService;

    @Autowired
    private SolaceTopicPublisherService solaceTopicPublisherService;

    @Value("${file.upload-dir}")
    private String fileUploadDir;

    public uploadFileResponse uploadFile(MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
        String fileUploadDir="/Development/EnterpriseSANStorage/uploads";
        String bitRate = videoAnalyserService.getBitRate(fileUploadDir+fileName);
        solaceTopicPublisherService.publishVideoToSolaceTopic(fileUploadDir+fileName,fileName, bitRate,null);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/v1/solace/iot/downloadFile/")
                .path(fileName)
                .toUriString();

        System.out.println("## Testing Starts ##");

        return new uploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize(), bitRate);
    }

}
