package com.backend2.abhay.Controller;

import com.backend2.abhay.DTO.TotalWorkHoursDTO;
import com.backend2.abhay.Entity.Attendance;
import com.backend2.abhay.Service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/attendance")
public class AttendanceController {
    private AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {

        this.attendanceService = attendanceService;
    }

    @GetMapping(path = "/")
    public List<Attendance> getAttendance() {
        return this.attendanceService.getAttendance();

    }

    @GetMapping(path = "/{id}")
    public Attendance getAttendanceById(@PathVariable("id") int id) {
        return this.attendanceService.getAttendanceById(id);
    }

    @PostMapping(path = "/add")
    public Attendance addAttendance(@RequestBody Attendance attendance) {
        return this.attendanceService.addAttendance(attendance);
    }

    @PostMapping(path = "/punchin")
    public String punchIn() {
        return this.attendanceService.punchIn();
    }

    @PostMapping(path = "/punchout")
    public String punchOut() {
        return this.attendanceService.punchOut();
    }

    @DeleteMapping(path = "/delete/{id}")
    public String deleteAttendance(@PathVariable("id") int id) {
        return this.attendanceService.deleteAttendance(id);
    }

    @GetMapping(path = "/totalHoursWorked")
    public String totalHoursWorked(@RequestBody TotalWorkHoursDTO totalWorkHoursDTO) {
        Integer workHours = this.attendanceService.totalHoursWorked(totalWorkHoursDTO.getStartDate(), totalWorkHoursDTO.getEndDate());
        if (workHours == -1)
            return "Error: Start Date Cannot be greater than End Date";
        return "Total Hours Worked: " + workHours;
    }
}
