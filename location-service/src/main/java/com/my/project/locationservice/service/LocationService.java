package com.my.project.locationservice.service;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.my.project.locationservice.dto.DriverLocationRequest;
import com.my.project.locationservice.dto.NearByDriverResponse;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private  final RedisTemplate<String,String> redisTemplate;
    private static final String DRIVER_GEO_KEY="drivers:location";
    
    /*
     * Driver calls this endpoint every 3 seconds to update its location
     * Maps to GEOADD redis command
     */
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest){
        log.info("Updating Location for Driver {}",driverLocationRequest.getDriverId());
        Point driverPoint=new Point(
            driverLocationRequest.getLongitude(),
            driverLocationRequest.getLatitude()
        );
        redisTemplate.opsForGeo().add(
            DRIVER_GEO_KEY,
            driverPoint,
            driverLocationRequest.getDriverId());

    }
    /*
     * Used to find nearby drivers with in given radius
     * Matching Service calls this method when a ride is requested 
     * Maps to GEORADIUS redis command
     */
    public List<NearByDriverResponse> getNearByDrivers(double longitude,double latitude,double radiusInKm){
        log.info("Finding Nearby Drivers for long {}, lat {}, radius {}",longitude,latitude,radiusInKm);
        Circle searchArea=new Circle(
            new Point(longitude,latitude),
            new Distance(radiusInKm,Metrics.KILOMETERS)
        );
      GeoResults<RedisGeoCommands.GeoLocation<String>> resultNearByDrivers =
        redisTemplate.opsForGeo().radius(
                DRIVER_GEO_KEY,
                searchArea,
                GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeCoordinates()
                        .includeDistance()
                        .sortAscending()
                        .limit(10)
        );
        if(resultNearByDrivers==null||resultNearByDrivers.getContent().isEmpty()){
            log.warn("No Drivers Found for long:{} lat {} within radius {} km",longitude,latitude,radiusInKm);
            return List.of();
        }
        log.debug("Found {} nearby drivers",resultNearByDrivers.getContent().size());
       return resultNearByDrivers.getContent()
        .stream()
        .map(driverLocation -> NearByDriverResponse.builder()
                .driverId(driverLocation.getContent().getName())
                .longitude(driverLocation.getContent().getPoint().getX())
                .latitude(driverLocation.getContent().getPoint().getY())
                .distanceInKm(driverLocation.getDistance().getValue())
                .build())
        .toList();
        
        
    }
    /*
        Used to remove driver who went offline by driverId
        Maps toRedis ZREM command
    */
    public void removeDriver(String driverId){
        log.debug("removing driver {} from redis",driverId);
        redisTemplate.opsForGeo()
                     .remove(
                      DRIVER_GEO_KEY,
                      driverId);
        
    }

}
