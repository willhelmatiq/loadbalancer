package com.example.hs.loadbalancer.service;

import com.example.hs.loadbalancer.model.Drink;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofMillis(500)).build();
    }


    public ResponseEntity<Drink> getFavouriteDrink(String url, String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);
        HttpEntity request = new HttpEntity(headers);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, Drink.class);
    }
    public String getStatus(String url) {
        return this.restTemplate.getForObject(url, String.class);
    }
}