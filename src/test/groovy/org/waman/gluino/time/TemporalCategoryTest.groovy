package org.waman.gluino.time

import org.junit.Test

import java.time.LocalDate
import java.time.LocalTime
import java.time.Month

import static org.junit.Assert.assertEquals

class TemporalCategoryTest {

    //***** plus TemporalAmount *****
    @Test
    void testPlusYears(){
        def date = LocalDate.of(2014, Month.APRIL, 1)

        use(TemporalCategory){
            assertEquals( date + 2.years, LocalDate.of(2016, Month.APRIL, 1))
        }
    }

    @Test
    void testPlusMonths(){
        def date = LocalDate.of(2014, Month.APRIL, 1)

        use(TemporalCategory){
            assertEquals( date + 2.months, LocalDate.of(2014, Month.JUNE, 1))
        }
    }

    @Test
    void testPlusWeeks(){
        def date = LocalDate.of(2014, Month.APRIL, 1)

        use(TemporalCategory){
            assertEquals( date + 2.weeks, LocalDate.of(2014, Month.APRIL, 15))
        }
    }

    @Test
    void testPlusDays(){
        def date = LocalDate.of(2014, Month.APRIL, 1)


        use(TemporalCategory){
            assertEquals( date + 2.days, LocalDate.of(2014, Month.APRIL, 3))
        }
    }

    @Test
    void testPlusHours(){
        def time = LocalTime.of(1, 23, 45, 678000000)

        use(TemporalCategory){
            assertEquals( time + 2.hours, LocalTime.of(3, 23, 45, 678_000_000))
        }
    }

    @Test
    void testPlusMinutes(){
        def time = LocalTime.of(1, 23, 45, 678000000)

        use(TemporalCategory){
            assertEquals( time + 2.minutes, LocalTime.of(1, 25, 45, 678_000_000))
        }
    }

    @Test
    void testPlusSeconds(){
        def time = LocalTime.of(1, 23, 45, 678000000)

        use(TemporalCategory){
            assertEquals( time + 2.seconds, LocalTime.of(1, 23, 47, 678_000_000))
        }
    }

    @Test
    void testPlusMillis(){
        def time = LocalTime.of(1, 23, 45, 678000000)

        use(TemporalCategory){
            assertEquals( time + 2.millis, LocalTime.of(1, 23, 45, 680_000_000))
        }
    }

    @Test
    void testPlusNanos(){
        def time = LocalTime.of(1, 23, 45, 678000000)

        use(TemporalCategory){
            assertEquals( time + 2.nanos, LocalTime.of(1, 23, 45, 678_000_002))
        }
    }
}
