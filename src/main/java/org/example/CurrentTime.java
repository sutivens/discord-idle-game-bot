package org.example;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CurrentTime {
     protected static String date;
     protected static String time;

    public static String getCurrentTime() {
        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        return "[" + date + "] " + "[" + time + "] ";
    }
}
