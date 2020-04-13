package com.filmguide;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class Utility {

    public static ResponseEntity<Map<String, Object>> buildErrorResponse(int status, String error, String message, String path, HttpStatus httpStatus){

        Map<String, Object> model = new LinkedHashMap<String, Object>();
        model.put("timestamp", new Date());
        model.put("status", status);
        model.put("error", error);
        model.put("message", message);
        model.put("path", path);

        return ResponseEntity.status(httpStatus).body(model);
    }

    public static ResponseEntity<Map<String, Object>> buildSuccessResponse(int status, String message, HttpStatus httpStatus){
       
        Map<String, Object> model = new LinkedHashMap<String, Object>();
        model.put("timestamp", new Date());
        model.put("status", status);
        model.put("message", message);

        return ResponseEntity.status(httpStatus).body(model);
    }
}
