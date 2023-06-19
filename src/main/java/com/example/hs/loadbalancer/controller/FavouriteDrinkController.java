package com.example.hs.loadbalancer.controller;

import com.example.hs.loadbalancer.model.Drink;
import com.example.hs.loadbalancer.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/v1/coffee/favourite")
public class FavouriteDrinkController {

    static final String AuthorizationHeader = "my_header";
    static final List<Drink> drinks = new ArrayList<>();
    static final Set<Drink> drinkSet = new HashSet<>();

    static int requestNum = 0;

    @Autowired
    private RestService restService;


    static final String instance_1Ip = "54.93.225.248";
    static final String instance_2Ip = "3.122.127.112";

    static {
        Drink favouriteDrink = new Drink("espresso");
        drinks.add(favouriteDrink);
        drinkSet.add(favouriteDrink);
    }


    @GetMapping()
    public ResponseEntity<Drink> getFavouriteDrink(@RequestHeader(name = "Authorization") String headerParam) {
        if (!headerParam.equals(AuthorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (requestNum % 2 == 0) {
            requestNum++;
            if (doHealthCheck(instance_1Ip)) {
                return restService.getFavouriteDrink("http://" + instance_1Ip + ":8080/v1/coffee/favourite", headerParam);
            } else {
                if (doHealthCheck(instance_2Ip)) {
                    return restService.getFavouriteDrink("http://" + instance_2Ip + ":8080/v1/coffee/favourite", headerParam);
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            requestNum = 0;
            if (doHealthCheck(instance_2Ip)) {
                return restService.getFavouriteDrink("http://" + instance_2Ip + ":8080/v1/coffee/favourite", headerParam);
            } else {
                if (doHealthCheck(instance_1Ip)) {
                    return restService.getFavouriteDrink("http://" + instance_1Ip + ":8080/v1/coffee/favourite", headerParam);
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
    }

//    @GetMapping("/leaderboard")
//    public List<Drink> getFavouriteDrinkLeaderboard(@RequestHeader(name = "Authorization") String headerParam) {
//        if (!headerParam.equals(AuthorizationHeader)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//        }
//        return drinks.stream().limit(3).collect(Collectors.toList());
//    }
//
//    @PostMapping
//    public Drink addFavouriteDrink(@RequestHeader(name = "Authorization") String headerParam, @RequestBody Drink drink) {
//        if (!headerParam.equals(AuthorizationHeader)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//        }
//        if (!drinkSet.contains(drink)) {
//            drinks.add(drink);
//            drinkSet.add(drink);
//            return drink;
//        }
//        throw new ResponseStatusException(HttpStatus.CONFLICT);
//    }

    private boolean doHealthCheck(String instanceIp) {
        String response = restService.getStatus("http://" + instanceIp + ":8080/v1/healthcheck");
        return "I'm alive!!!".equals(response);
    }

}
