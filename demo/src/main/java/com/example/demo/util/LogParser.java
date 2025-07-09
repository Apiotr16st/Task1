package com.example.demo.util;

public class LogParser {

    public static String encodeEmail(String email){
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        return username.charAt(0) + "XXXX" + "@" + domain;
    }
}
