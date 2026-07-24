package com.digitalvillage.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class PassengerForm {
    // Hidden fields carried over from selected train
    private String trainId;
    private String trainName;
    private String fromStation;
    private String toStation;
    private String date;
    private String seatClass;
    private int fare;
    private String selectedSeats;

    // User input fields
    @NotBlank(message = "{train.error.passenger_name}")
    private String passengerName;
    
    @NotBlank(message = "{train.error.phone}")
    private String phoneNumber;
}
