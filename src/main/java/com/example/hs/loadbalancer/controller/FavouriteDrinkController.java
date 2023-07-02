package com.example.hs.loadbalancer.controller;

import com.example.hs.loadbalancer.model.Drink;
import com.example.hs.loadbalancer.service.BucketService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/v1/coffee/**")
public class FavouriteDrinkController {

    static final String AuthorizationHeader = "my_header";

    @Autowired
    private BucketService bucketService;
    @Autowired
    private ControllerUtils controllerUtils;


    @GetMapping()
    public ResponseEntity<Drink>  getFavouriteDrink(@RequestHeader(name = "Authorization") String headerParam,
            @RequestHeader(value = "X-api-key") String apiKey, HttpServletRequest request) {
        if (!headerParam.equals(AuthorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        String partUrl = request.getServletPath();
        if (partUrl.contains("leaderboard")) {
            apiKey += "LEADERBOARD";
        }
        controllerUtils.processRequest(responseHeaders, partUrl);
        System.out.println(responseHeaders.getLocation());
        Bucket bucket = bucketService.resolveBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            responseHeaders.add("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
            return new ResponseEntity<>(responseHeaders, HttpStatusCode.valueOf(302));
        }
        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill))
                .build();
    }

    @PostMapping()
    public ResponseEntity<Drink> createNewDrink(@RequestBody Drink drink, @RequestHeader(name = "Authorization") String headerParam,
                                                HttpServletRequest request) {
        if (!headerParam.equals(AuthorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        String partUrl = request.getServletPath();
        String apiKey = request.getRemoteAddr();
        if (partUrl.contains("/v1/coffee/favourite")) {
            apiKey += "SET_UP_FAVOURITE_DRINK";
        }
        controllerUtils.processRequest(responseHeaders, partUrl);
        System.out.println(responseHeaders.getLocation());
        Bucket bucket = bucketService.resolveBucket(apiKey);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            responseHeaders.add("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()));
            return new ResponseEntity<>(drink, responseHeaders, HttpStatusCode.valueOf(307));
        }
        long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill))
                .build();

    }

}
