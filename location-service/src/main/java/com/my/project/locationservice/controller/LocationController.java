package com.my.project.locationservice.controller;

import com.my.project.locationservice.service.LocationService;
import com.my.project.locationservice.dto.DriverLocationRequest;
import com.my.project.locationservice.dto.NearByDriverResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/*
 * Location Controller
 * Manages driver locations by updating locations,
 * finding nearby drivers, and removing offline drivers.
 */
@RestController
@RequestMapping("/api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;
    
    //Driver calls this endpoint every few seconds to update its location
    @PutMapping("/drivers/update")
    public ResponseEntity<String> updateDriverLocation(
        @Valid 
        @RequestBody 
        DriverLocationRequest driverLocationRequest){
            locationService.updateDriverLocation(driverLocationRequest);
            log.debug("updated location for Driver {}",driverLocationRequest.getDriverId());
            return ResponseEntity.ok("Driver Location Updated");
    }

    //Matching Service calls this endpoint to find nearby drivers i.e., when a ride is requested
    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriverResponse>> getNearByDrivers(
        @RequestParam double longitude,
        @RequestParam double latitude,
        @Min(1)
        @Max(50)
        @RequestParam(defaultValue="5.0") 
        double radius){
            List<NearByDriverResponse> nearByDrivers=locationService.getNearByDrivers(longitude,latitude,radius);
            log.debug("found {} nearby drivers",nearByDrivers.size());
            return ResponseEntity.ok(nearByDrivers);
    }
    //When the driver goes offline, the matching service calls this endpoint to remove the driver from the db
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId){
        locationService.removeDriver(driverId);
        log.debug("removed Driver {}",driverId);
        return ResponseEntity.ok("Driver Removed Sucessfully");
    }

    
}
