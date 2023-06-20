package com.example.hs.loadbalancer.controller;

import com.example.hs.loadbalancer.model.IpSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.hs.loadbalancer.controller.FavouriteDrinkController.AuthorizationHeader;

@RestController
@RequestMapping("/v1/setup")
public class SetUpController {

    public static String instance_1Ip;
    public static String instance_2Ip;

    @PostMapping("/setinstanceips")
    public ResponseEntity<IpSettings> setInstanceIps(@RequestHeader(name = "Authorization") String headerParam,
                                                 @RequestBody IpSettings ipSettings) {
        if (!headerParam.equals(AuthorizationHeader)) {
            return new ResponseEntity<>(null , HttpStatus.FORBIDDEN);
        }
        instance_1Ip = ipSettings.getInstance_1();
        instance_2Ip = ipSettings.getInstance_2();
        return new ResponseEntity<>(ipSettings, HttpStatus.OK);
    }
}
