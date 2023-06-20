package com.example.hs.loadbalancer.controller;

import com.example.hs.loadbalancer.model.Drink;
import com.example.hs.loadbalancer.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.hs.loadbalancer.controller.SetUpController.instance_1Ip;
import static com.example.hs.loadbalancer.controller.SetUpController.instance_2Ip;


@RestController
@RequestMapping("/v1/coffee/favourite")
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
    public ResponseEntity<Drink>  getFavouriteDrink(@RequestHeader(name = "Authorization") String headerParam) {
        if (!headerParam.equals(AuthorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        if (requestNum % 2 == 0) {
            requestNum++;
            if (doHealthCheck(instance_1Ip)) {
                responseHeaders.setLocation(URI.create("http://" + instance_1Ip + ":8080/v1/coffee/favourite"));
            } else {
                if (doHealthCheck(instance_2Ip)) {
                    responseHeaders.set("Location", "http://" + instance_2Ip + ":8080/v1/coffee/favourite");
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            requestNum = 0;
            if (doHealthCheck(instance_2Ip)) {
                responseHeaders.set("Location", "http://" + instance_2Ip + ":8080/v1/coffee/favourite");
            } else {
                if (doHealthCheck(instance_1Ip)) {
                    responseHeaders.set("Location", "http://" + instance_1Ip + ":8080/v1/coffee/favourite");
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity<>(responseHeaders, HttpStatusCode.valueOf(302));
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
