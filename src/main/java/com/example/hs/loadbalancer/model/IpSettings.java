package com.example.hs.loadbalancer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpSettings {

    private String instance_1;
    private String instance_2;
    private String instance_3;
}
