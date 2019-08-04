package com.allibilli.recator.functional;


import com.allibilli.recator.model.EventModel;

/**
 * Created by Gopi K Kancharla
 * 7/23/18 2:22 PM
 */
@FunctionalInterface
public interface ErrorHandlerFunction {

    void errorHandlerCallback(EventModel eventRecord);
}
