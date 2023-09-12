package com.backend2.abhay.Controller;

import com.backend2.abhay.Entity.Attendance;
import com.backend2.abhay.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping(path = "/")
    public List<Attendance> getAttendance() {
        return attendanceService.getAttendance();

    }

    @PostMapping(path = "/add")
    public Attendance addAttendance(@RequestBody Attendance attendance) {
        return attendanceService.addAttendance(attendance);
    }

    @PostMapping(path = "/punchin")
    public String punchIn() {
        return attendanceService.punchIn();
    }

}
