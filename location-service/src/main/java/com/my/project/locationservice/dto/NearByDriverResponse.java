package com.my.project.locationservice.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    NearByDriverResponse - which we Send to MatchingService about NearBy Driver Location Coordinates along with nearby Distance in Km
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {
    @NotBlank
    private String driverId;

    @DecimalMin("-180")
    @DecimalMax("180")
    private double longtitude;

    @DecimalMin("-90")
    @DecimalMax("90")
    private double latitude;

    @Min(1)
    @Max(50)
    private double distanceInKm;
}
