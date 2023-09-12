package com.backend2.abhay.Service;

import com.backend2.abhay.Entity.Attendance;
import com.backend2.abhay.Repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

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

    public String punchIn() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Define office start time and buffer time as Time
        Time officeStartTime = Time.valueOf(LocalTime.of(9, 0));
        Time bufferEndTime = Time.valueOf(LocalTime.of(9, 15));

        if (currentTime.isBefore(bufferEndTime.toLocalTime())) {
            // Within buffer time, insert the record
            Attendance attendance = new Attendance();
            attendance.setPunchin(Time.valueOf(currentTime));
            attendance.setAttendance_date((currentDate));
            attendanceRepository.save(attendance);
            return "Attendance recorded successfully";
        } else if (currentTime.isBefore(officeStartTime.toLocalTime())) {
            // Late, insert the record but display a message
            Attendance attendance = new Attendance();
            attendance.setPunchin(Time.valueOf(currentTime));
            attendance.setAttendance_date(currentDate);
            attendanceRepository.save(attendance);
            return "You are late. Attendance recorded.";
        } else {
            // Beyond office start time, cannot punch in
            return "You are too late to punch in.";
        }
    }
}
