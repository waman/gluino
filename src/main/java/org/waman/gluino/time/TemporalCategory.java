package org.waman.gluino.time;

import java.time.Duration;
import java.time.Period;

public class TemporalCategory {

    public static Period getYears(Integer i){
        return Period.ofYears(i);
    }

    public static Period getMonths(Integer i){
        return Period.ofMonths(i);
    }

    public static Period getWeeks(Integer i){
        return Period.ofWeeks(i);
    }

    public static Period getDays(Integer i){
        return Period.ofDays(i);
    }

    public static Duration getHours(Integer i){
        return Duration.ofHours(i);
    }

    public static Duration getMinutes(Integer i){
        return Duration.ofMinutes(i);
    }

    public static Duration getSeconds(Integer i){
        return Duration.ofSeconds(i);
    }

    public static Duration getMillis(Integer i){
        return Duration.ofMillis(i);
    }

    public static Duration getNanos(Integer i){
        return Duration.ofNanos(i);
    }
}
