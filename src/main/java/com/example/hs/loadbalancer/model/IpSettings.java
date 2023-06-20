package com.example.hs.loadbalancer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpSettings {

    private String instanse_1;
    private String instanse_2;
}
