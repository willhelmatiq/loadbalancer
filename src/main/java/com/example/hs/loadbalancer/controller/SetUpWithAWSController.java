package com.example.hs.loadbalancer.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.hs.loadbalancer.model.IpSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import static com.example.hs.loadbalancer.controller.SetUpController.*;

@Controller
@RequestMapping("/v1/setupaws")
public class SetUpWithAWSController {

    @Autowired
    private AmazonS3 amazonS3Client;

    @GetMapping()
    public ResponseEntity<String> setInstanceIpsWithAWS() throws IOException {
        List<Bucket> bucketList = listBuckets();
        S3Object s3object = amazonS3Client.getObject("my-bucket-hw", "ip_config.json");
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        ObjectMapper mapper = new ObjectMapper();
        IpSettings ipSettings = mapper.readValue(inputStream, IpSettings.class);
        instance_1Ip = ipSettings.getInstance_1();
        instance_2Ip = ipSettings.getInstance_2();
        instance_3Ip = ipSettings.getInstance_3();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public List<Bucket> listBuckets(){
        return amazonS3Client.listBuckets();
    }
}
