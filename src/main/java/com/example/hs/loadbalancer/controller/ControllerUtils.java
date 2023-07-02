package com.example.hs.loadbalancer.controller;

import com.example.hs.loadbalancer.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static com.example.hs.loadbalancer.controller.SetUpController.*;

@Component
public class ControllerUtils {


    static int requestNum = 0;

    @Autowired
    private RestService restService;


    public void processRequest(HttpHeaders responseHeaders, String partUrl) {
        if (requestNum % 3 == 0) {
            requestNum++;
            if (doHealthCheck(instance_1Ip)) {
                responseHeaders.setLocation(URI.create(instance_1Ip.replace("host.docker.internal", "localhost") + partUrl));
            } else {
                if (doHealthCheck(instance_2Ip)) {
                    responseHeaders.set("Location", instance_2Ip.replace("host.docker.internal", "localhost") + partUrl);
                } else {
                    if (doHealthCheck(instance_3Ip)) {
                        responseHeaders.set("Location", instance_3Ip.replace("host.docker.internal", "localhost") + partUrl);
                    } else {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
            }
        } else if (requestNum % 3 == 1){
            requestNum++;
            if (doHealthCheck(instance_2Ip)) {
                responseHeaders.set("Location", instance_2Ip + partUrl);
            } else {
                if (doHealthCheck(instance_1Ip)) {
                    responseHeaders.set("Location", instance_1Ip + partUrl);
                } else {
                    if (doHealthCheck(instance_3Ip)) {
                        responseHeaders.set("Location", instance_3Ip.replace("host.docker.internal", "localhost") + partUrl);
                    } else {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                }
            }
        } else {
            requestNum = 0;
            if (doHealthCheck(instance_3Ip)) {
                responseHeaders.set("Location", instance_3Ip + partUrl);
            } else {
                if (doHealthCheck(instance_1Ip)) {
                    responseHeaders.set("Location", instance_1Ip + partUrl);
                } else {
                    if (doHealthCheck(instance_2Ip)) {
                        responseHeaders.set("Location", instance_2Ip.replace("host.docker.internal", "localhost") + partUrl);
                    } else {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                    }

                }
            }
        }
    }

    private boolean doHealthCheck(String instanceIp) {
        String url = instanceIp + "/v1/healthcheck";
        System.out.println(url);
        String response = restService.getStatus(url);
        return "I'm alive!!!".equals(response);
    }
}
