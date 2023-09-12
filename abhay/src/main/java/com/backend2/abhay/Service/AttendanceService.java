package com.backend2.abhay.Service;

import com.backend2.abhay.Entity.Attendance;
import com.backend2.abhay.Repository.AttendanceRepository;
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

    private final AttendanceRepository attendanceRepository;

    String punchInMessage = "";


    // Define office start time and buffer time and office end time
    private final Time officeStartTime = Time.valueOf(LocalTime.of(9, 0));
    private final Time bufferEndTime = Time.valueOf(LocalTime.of(9, 15));

    private final Time officeEndTime = Time.valueOf(LocalTime.of(18, 0));

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> getAttendance() {
        return attendanceRepository.findAll();
    }

    public Attendance addAttendance(Attendance attendance) {
        Optional<Attendance> existingAttendance = attendanceRepository.findById(attendance.getId());
        if (existingAttendance.isPresent()) {
            throw new IllegalStateException("Attendance with id " + attendance.getId() + " already exists");
        }
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(int id) {
        boolean exists = attendanceRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Attendance with id " + id + " does not exists");
        }
        attendanceRepository.deleteById(id);
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
            return ("Punch for today already done at " + attendanceRepository.findAttendanceByAttendance_date(currentDate).get().getPunchin() + ".");
        } else {
            if (lastAttendance.getPunchout() == null && lastAttendance.getAttendance_date().getDayOfWeek() != DayOfWeek.SATURDAY && lastAttendance.getAttendance_date().getDayOfWeek() != DayOfWeek.SUNDAY) {
                // Punch out not done, cannot punch in
                punchInMessage = "You have missed punched out on " + lastAttendance.getAttendance_date() + ".";
            }

            if (currentTime.isBefore(bufferEndTime.toLocalTime())) {
                // Within buffer time, insert the record
                attendance.setPunchin(Time.valueOf(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
                attendance.setAttendance_date((currentDate));
                attendanceRepository.save(attendance);
                return punchInMessage + "Attendance recorded successfully.Punched In at " + currentTime + ".";
            } else if (currentTime.isAfter(officeStartTime.toLocalTime())){
//                    && currentTime.isBefore(officeEndTime.toLocalTime())) {
                // Late, insert the record but display a message
                attendance.setPunchin(Time.valueOf(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
                attendance.setAttendance_date(currentDate);
                attendanceRepository.save(attendance);
                return punchInMessage + "You are late. Attendance recorded.Punched In at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".";
            }
//            else {
//                // Beyond office start time, cannot punch in
//                return punchInMessage + "You are too late to punch in.";
//            }
            return punchInMessage + "You are too late to punch in.";
        }
    }


    public String punchOut() {

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Attendance attendance ;


        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        String punchOutMessage = "";

        if (attendanceRepository.findAttendanceByAttendance_date(currentDate).isPresent()) {
            attendance = attendanceRepository.findAttendanceByAttendance_date(currentDate).get();
        } else {
            attendance = new Attendance();
            punchOutMessage = "You have not punched in today.";
        }


        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return "It's a weekend. No attendance required.";
        }

        if (attendance.getPunchout() != null) {
            // Punch out already done
            return ("Punch for today already done at " + attendance.getPunchout() + ".");
        } else {
            if (attendance.getPunchin() == null) {
                // Punch in not done, cannot punch out
                punchOutMessage = "You have not punched in today.";
            }
            if (currentTime.isBefore(officeStartTime.toLocalTime())) {
                // Before office start time, do not allow punch out

                return punchOutMessage + "You are too early to punch out.";
            } else {
                // Punch out
                attendance.setPunchout(Time.valueOf(currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
                attendance.setAttendance_date(currentDate);
                attendanceRepository.save(attendance);
                return punchOutMessage + "Attendance recorded successfully.Punched Out at " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ".";
            }


        }
    }


}


