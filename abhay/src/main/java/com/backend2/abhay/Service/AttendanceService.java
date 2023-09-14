package com.backend2.abhay.Service;

import com.backend2.abhay.Entity.Attendance;
import com.backend2.abhay.Repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private AttendanceRepository attendanceRepository;

    String punchInMessage = "";


    // Define office start time and buffer time and office end time
    private final Time officeStartTime = Time.valueOf(LocalTime.of(9, 0));
    private final Time bufferEndTime = Time.valueOf(LocalTime.of(9, 15));

    private final Time officeEndTime = Time.valueOf(LocalTime.of(18, 0));

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> getAttendance() {
        return this.attendanceRepository.findAll();
    }

    public Attendance addAttendance(Attendance attendance) {
        Optional<Attendance> existingAttendance = this.attendanceRepository.findById(attendance.getId());
        if (existingAttendance.isPresent()) {
            throw new IllegalStateException("Attendance with id " + attendance.getId() + " already exists");
        }
        return this.attendanceRepository.save(attendance);
    }

    public String deleteAttendance(int id) {
        boolean exists = this.attendanceRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Attendance with id " + id + " does not exists");
        }
        this.attendanceRepository.deleteById(id);
        return "Attendance with id " + id + " deleted successfully";
    }

    public String punchIn() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Attendance attendance = new Attendance();
        Attendance lastAttendance = attendanceRepository.findLastAttendance();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return "It's a weekend. No attendance required.";
        }

        if (attendanceRepository.findAttendanceByAttendance_date(currentDate).isPresent() && lastAttendance.getPunchin() != null) {
            // Punch in already done
            return ("Punch in for today already done at " + attendanceRepository.findAttendanceByAttendance_date(currentDate).get().getPunchin() + ".");
        } else {
            if (lastAttendance.getPunchout() == null && lastAttendance.getAttendance_date().getDayOfWeek() != DayOfWeek.SATURDAY && lastAttendance.getAttendance_date().getDayOfWeek() != DayOfWeek.SUNDAY) {
                // Punch out not done, cannot punch in
                punchInMessage = "You have missed punched out on " + lastAttendance.getAttendance_date() + ".";
            }

            if (currentTime.isBefore(bufferEndTime.toLocalTime())) {

                // Within buffer time, insert the record
                attendance.setPunchin(Time.valueOf(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
                attendance.setAttendance_date((currentDate));
                this.attendanceRepository.save(attendance);
                return punchInMessage + "Attendance recorded successfully.Punched In at " + currentTime + ".";
            } else if (currentTime.isAfter(officeStartTime.toLocalTime())  && currentTime.isBefore(officeEndTime.toLocalTime())) {
                // Late, insert the record but display a message
                attendance.setPunchin(Time.valueOf(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
                attendance.setAttendance_date(currentDate);
                this.attendanceRepository.save(attendance);
                return punchInMessage + "You are late. Attendance recorded.Punched In at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".";
            }
            else {
                // Beyond office start time, cannot punch in
                return punchInMessage + "You are too late to punch in.";
            }

        }
    }


    public String punchOut() {

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.of(15,30);
        Attendance attendance ;


        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        String punchOutMessage = "";

        if (this.attendanceRepository.findAttendanceByAttendance_date(currentDate).isPresent()) {
            attendance = this.attendanceRepository.findAttendanceByAttendance_date(currentDate).get();
        } else {
            //No punch in case
            attendance = new Attendance();
            if (LocalTime.now().isBefore(LocalTime.of(13, 0))) {
                attendance.setPunchin(Time.valueOf(LocalTime.of(10, 00, 00)));
            } else {
                attendance.setPunchin(Time.valueOf(LocalTime.of(13, 00, 00)));
            }
            punchOutMessage = "You have not punched in today.";
        }


        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            //Weekend no attendance required
            return "It's a weekend. No attendance required.";
        }

        if (attendance.getPunchout() != null) {
            // Punch out already done
            return ("Punch out for today already done at " + attendance.getPunchout() + ".");
        } else {
            if (attendance.getPunchin() == null) {
                // Punch in not done, cannot punch out
                punchOutMessage = "You have not punched in today. ";
            }
            if (currentTime.isBefore(officeStartTime.toLocalTime())) {
                // Before office start time, do not allow punch out

                return punchOutMessage + "You are too early to punch out.";
            } else if (currentTime.isAfter(officeStartTime.toLocalTime()) && currentTime.isBefore(officeEndTime.toLocalTime())) {
                // Within office hours, punch out
                Time remainingTime = Time.valueOf(LocalTime.of(18, 0).minusHours(currentTime.getHour()).minusMinutes(currentTime.getMinute()).minusSeconds(currentTime.getSecond()));
                attendance.setPunchout(Time.valueOf(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
                attendance.setAttendance_date(currentDate);
                this.attendanceRepository.save(attendance);
                return punchOutMessage + "Attendance recorded successfully.Punched Out at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".\n"+"You still have "+ remainingTime +" hours time remaining for today.";

            } else  {
                // Punch out
                attendance.setPunchout(Time.valueOf(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
                attendance.setAttendance_date(currentDate);
                this.attendanceRepository.save(attendance);
                return punchOutMessage + "Attendance recorded successfully.Punched Out at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".";
            }



        }
    }


    public Integer totalHoursWorked(LocalDate startDate, LocalDate endDate) {
        try{
            if (startDate.isAfter(endDate)) {
                throw new IllegalStateException("Start date cannot be after end date");
            }
            Integer workHours = this.attendanceRepository.totalHoursWorked(startDate, endDate);
            if (workHours == null) {
                return 0;
            }
            return workHours;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return -1;
        }


    }

    public Attendance getAttendanceById(int id) {
        Attendance attendance = this.attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Attendance with id " + id + " does not exists"));
        return attendance;

    }
}


