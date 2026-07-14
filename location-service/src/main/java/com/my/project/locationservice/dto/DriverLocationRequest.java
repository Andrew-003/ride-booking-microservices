package com.my.project.locationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

/*
    DriverRequest - which we Receive from Driver about Current Location Coordinates of the Driver
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverLocationRequest {
    @NotBlank
    private String driverId;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private double longitude;
}
