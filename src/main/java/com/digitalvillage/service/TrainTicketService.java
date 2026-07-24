package com.digitalvillage.service;

import com.digitalvillage.model.SeatClassAvailability;
import com.digitalvillage.model.TrainOption;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TrainTicketService {

    public List<TrainOption> searchTrains(String fromStation, String toStation, String dateStr, String seatClass) {
        List<TrainOption> options = new ArrayList<>();
        
        if (fromStation.equalsIgnoreCase("Dhaka") && toStation.equalsIgnoreCase("Chattogram")) {
            options.add(new TrainOption(UUID.randomUUID().toString(), "Suborno Express (702)", "04:30 PM", "10:10 PM", generateClasses(380, 805)));
            options.add(new TrainOption(UUID.randomUUID().toString(), "Sonar Bangla Express (788)", "07:00 AM", "12:15 PM", generateClasses(405, 850)));
            options.add(new TrainOption(UUID.randomUUID().toString(), "Turna Express (742)", "11:30 PM", "06:20 AM", generateClasses(345, 730)));
        } else if (fromStation.equalsIgnoreCase("Dhaka") && toStation.equalsIgnoreCase("Sylhet")) {
            options.add(new TrainOption(UUID.randomUUID().toString(), "Parabat Express (709)", "06:20 AM", "01:00 PM", generateClasses(320, 610)));
            options.add(new TrainOption(UUID.randomUUID().toString(), "Kalni Express (773)", "03:00 PM", "09:30 PM", generateClasses(320, 610)));
        } else if (fromStation.equalsIgnoreCase("Dhaka") && toStation.equalsIgnoreCase("Tangail")) {
            options.add(new TrainOption(UUID.randomUUID().toString(), "Silk City Express (753)", "02:45 PM", "05:15 PM", generateClasses(125, 240)));
            options.add(new TrainOption(UUID.randomUUID().toString(), "Sirajganj Express (775)", "05:00 PM", "07:30 PM", generateClasses(125, 240)));
        } else if (fromStation.equalsIgnoreCase("Tangail") && toStation.equalsIgnoreCase("Dhaka")) {
            options.add(new TrainOption(UUID.randomUUID().toString(), "Silk City Express (754)", "11:30 AM", "02:00 PM", generateClasses(125, 240)));
            options.add(new TrainOption(UUID.randomUUID().toString(), "Sirajganj Express (776)", "08:15 AM", "10:45 AM", generateClasses(125, 240)));
        } else {
            options.add(new TrainOption(UUID.randomUUID().toString(), "Mahanagar Express", "08:00 AM", "02:30 PM", generateClasses(350, 700)));
            options.add(new TrainOption(UUID.randomUUID().toString(), "Upaban Express", "10:00 PM", "05:00 AM", generateClasses(350, 700)));
        }
        
        // Filter out trains that have already departed if the date is today
        try {
            java.time.LocalDate searchDate = java.time.LocalDate.parse(dateStr);
            if (searchDate.equals(java.time.LocalDate.now())) {
                java.time.LocalTime currentTime = java.time.LocalTime.now();
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
                
                options.removeIf(train -> {
                    try {
                        java.time.LocalTime departureTime = java.time.LocalTime.parse(train.getDepartureTime(), formatter);
                        return departureTime.isBefore(currentTime);
                    } catch (Exception e) {
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }

        return options;
    }

    private List<SeatClassAvailability> generateClasses(int baseShovon, int baseSnigdha) {
        List<SeatClassAvailability> classes = new ArrayList<>();
        classes.add(new SeatClassAvailability("S_CHAIR", baseShovon, (int)(Math.random() * 30)));
        classes.add(new SeatClassAvailability("SNIGDHA", baseSnigdha, (int)(Math.random() * 85)));
        classes.add(new SeatClassAvailability("AC_SEAT", (int)(baseSnigdha * 1.2), 0)); // Mock sold out
        return classes;
    }
}
