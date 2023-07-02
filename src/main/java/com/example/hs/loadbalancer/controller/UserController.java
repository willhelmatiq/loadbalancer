package com.example.hs.loadbalancer.controller;

import com.example.hs.loadbalancer.model.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/user/**")
public class UserController {

    static final String AuthorizationHeader = "my_header";

    @Autowired
    private ControllerUtils controllerUtils;



    @PostMapping()
    public ResponseEntity<AppUser> createNewUser(@RequestBody AppUser appUser,
                                                 @RequestHeader(name = "Authorization") String headerParam,
                                                 HttpServletRequest request) {
        if (!headerParam.equals(AuthorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        String partUrl = request.getServletPath();
        controllerUtils.processRequest(responseHeaders, partUrl);
        System.out.println(responseHeaders.getLocation());
        return new ResponseEntity<>(appUser, responseHeaders, HttpStatusCode.valueOf(307));

    }
}
