package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.util.Precision;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        /**
        int inHour = ticket.getInTime().getHours();
        int outHour = ticket.getOutTime().getHours();
        //TODO: Some tests are failing here. Need to check if this logic is correct
        int duration = outHour - inHour;
        */
        
        // int > double et transformer getHours and getTime > faire la soustraction en ms > convertir les ms en heures  
        
        Long inHourInMillis = ticket.getInTime().getTime();
        Long outHourInMillis = ticket.getOutTime().getTime();
       
        
        double durationMin = (double) TimeUnit.MILLISECONDS.toMinutes(outHourInMillis - inHourInMillis);
        double durationHour = Precision.round((durationMin/60), 2);
        
        
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
               // ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
            	ticket.setPrice(durationHour * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
               // ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
            	ticket.setPrice(durationHour * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}