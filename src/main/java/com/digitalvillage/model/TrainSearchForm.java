package com.digitalvillage.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TrainSearchForm {
    @NotBlank(message = "{train.error.from}")
    private String fromStation;
    
    @NotBlank(message = "{train.error.to}")
    private String toStation;
    
    @NotBlank(message = "{train.error.date}")
    private String date;
    
    @NotBlank(message = "{train.error.class}")
    private String seatClass;
}
