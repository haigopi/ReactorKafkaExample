package com.allibilli.recator.model;

import lombok.Data;

import java.util.UUID;

/**
 * Created by Gopi K Kancharla
 * 7/23/18 2:22 PM
 */
@Data
public class EventModel {
    UUID stockId;
    String stockName;
    float price;
}
