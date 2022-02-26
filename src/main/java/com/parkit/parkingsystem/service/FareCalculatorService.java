package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.util.Precision;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	
    public void calculateFare(Ticket ticket) {
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ) {
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        
        Long inHourInMillis = ticket.getInTime().getTime();
        Long outHourInMillis = ticket.getOutTime().getTime();
         
        // We convert duration from milliseconds to minutes and then to hours (rounded with 2 decimals)
        double durationMin = (double) TimeUnit.MILLISECONDS.toMinutes(outHourInMillis - inHourInMillis);
        double durationHour = Precision.round((durationMin / 60), 2);
             
        TicketDAO ticketDAO = new TicketDAO();
        int visit = ticketDAO.getRecurrence(ticket.getVehicleRegNumber());
        
        //Apply normal rate depending on vehicle if parking time is greater than 30 minutes
        if (durationMin >30) {
        	switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	ticket.setPrice(durationHour * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
            	ticket.setPrice(durationHour * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type"); 
         }
        	
        // Apply discount to recurring user (more than 1 visit) 
        if (visit >1) {
        double discountedPrice = Precision.round((ticket.getPrice() * Fare.RECURRING_USER_DISCOUNT), 2);
        ticket.setPrice(discountedPrice);
        }
        	
        }
      
        //Free parking for 30 minutes or less users
        else ticket.setPrice(0); 
    }
}