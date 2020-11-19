package Appointments.Booking.Interface;

import java.util.List;

import Appointments.Booking.Model.TimeSlot;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);

    void onTimeSlotLoadFailure(String message);

    void onTimeSlotLoadEmpty();
}