package com.example.hs.loadbalancer.controller;

import com.example.hs.loadbalancer.model.Drink;
import com.example.hs.loadbalancer.service.RestService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.hs.loadbalancer.controller.SetUpController.*;


@RestController
@RequestMapping("/v1/**")
public class FavouriteDrinkController {

    static final String AuthorizationHeader = "my_header";
    static final List<Drink> drinks = new ArrayList<>();
    static final Set<Drink> drinkSet = new HashSet<>();

    static int requestNum = 0;

    @Autowired
    private RestService restService;


    static {
        Drink favouriteDrink = new Drink("espresso");
        drinks.add(favouriteDrink);
        drinkSet.add(favouriteDrink);
    }


    @GetMapping()
    public ResponseEntity<Drink>  getFavouriteDrink(@RequestHeader(name = "Authorization") String headerParam, HttpServletRequest request) {
        if (!headerParam.equals(AuthorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        String partUrl = request.getServletPath();
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
        System.out.println(responseHeaders.getLocation());
        return new ResponseEntity<>(responseHeaders, HttpStatusCode.valueOf(302));
    }

    @PostMapping
    public ResponseEntity<Drink> createNewDrink(@RequestBody Drink drink,
                                                @RequestHeader(name = "Authorization") String headerParam,
                                                HttpServletRequest request) {
        if (!headerParam.equals(AuthorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        String partUrl = request.getServletPath();
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
        System.out.println(responseHeaders.getLocation());
        return new ResponseEntity<>(responseHeaders, HttpStatusCode.valueOf(302));

    }

    private boolean doHealthCheck(String instanceIp) {
        String url = instanceIp + "/v1/healthcheck";
        System.out.println(url);
        String response = restService.getStatus(url);
        return "I'm alive!!!".equals(response);
    }

}
