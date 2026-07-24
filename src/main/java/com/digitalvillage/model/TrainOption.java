package com.digitalvillage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainOption {
    private String id;
    private String name;
    private String departureTime;
    private String arrivalTime;
    private List<SeatClassAvailability> seatClasses;
}
