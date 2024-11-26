package com.example.clientapp.user;

import static org.junit.jupiter.api.Assertions.*;

import com.example.clientapp.util.CommonTypes.Availability;
import com.example.clientapp.util.CommonTypes.Day;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeSlotTest {

  private TimeSlot timeSlot;

  @BeforeEach
  void setUp() {
    timeSlot = new TimeSlot(
            LocalTime.of(10, 10),
            LocalTime.of(11, 25),
            Day.Monday,
            Day.Tuesday,
            Availability.available,
            1
    );
    timeSlot.setUid(10);
  }

  @Test
  void testTimeSlotInitialization() {
    assertNotNull(timeSlot);
    assertEquals(LocalTime.of(10, 10), timeSlot.getStartTime());
    assertEquals(LocalTime.of(11, 25), timeSlot.getEndTime());
    assertEquals(Day.Monday, timeSlot.getStartDay());
    assertEquals(Day.Tuesday, timeSlot.getEndDay());
    assertEquals(Availability.available, timeSlot.getAvailability());
    assertEquals(1, timeSlot.getTid());
    assertEquals(10, timeSlot.getUid());
  }

  @Test
  void testSetAndGetStartTime() {
    timeSlot.setStartTime(LocalTime.of(12, 0));
    assertEquals(LocalTime.of(12, 0), timeSlot.getStartTime());
  }

  @Test
  void testSetAndGetEndTime() {
    timeSlot.setEndTime(LocalTime.of(14, 0));
    assertEquals(LocalTime.of(14, 0), timeSlot.getEndTime());
  }

  @Test
  void testSetAndGetStartDay() {
    timeSlot.setStartDay(Day.Friday);
    assertEquals(Day.Friday, timeSlot.getStartDay());
  }

  @Test
  void testSetAndGetEndDay() {
    timeSlot.setEndDay(Day.Sunday);
    assertEquals(Day.Sunday, timeSlot.getEndDay());
  }

  @Test
  void testSetAndGetAvailability() {
    timeSlot.setAvailability(Availability.busy);
    assertEquals(Availability.busy, timeSlot.getAvailability());
  }

  @Test
  void testToString() {
    String expectedString = "TimeSlot{startTime=10:10, endTime=11:25, startDay=Monday, endDay=Tuesday, availability=available}";
    assertEquals(expectedString, timeSlot.toString());
  }
}
