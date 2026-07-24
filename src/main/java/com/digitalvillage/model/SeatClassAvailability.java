package com.digitalvillage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatClassAvailability {
    private String className;
    private int fare;
    private int availableSeats;
}
