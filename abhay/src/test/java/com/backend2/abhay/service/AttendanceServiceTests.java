package com.backend2.abhay.service;

import com.backend2.abhay.AbhayApplication;
import com.backend2.abhay.Entity.Attendance;
import com.backend2.abhay.Repository.AttendanceRepository;
import com.backend2.abhay.Service.AttendanceService;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest(classes = AbhayApplication.class)
@RunWith(SpringRunner.class)
public class AttendanceServiceTests {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;


    @Test
    public void attendanceService_saveAll_returnSavedAttendance() {
        //Arrange

        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .punchout(Time.valueOf("17:00:00"))
                .attendance_date(LocalDate.now())
                .build();

        Mockito.when(attendanceRepository.save(attendance1)).thenReturn(attendance1);


        // Act
        Attendance createdAttendance = attendanceService.addAttendance(attendance1);

        // Assert
        Assertions.assertThat(createdAttendance).isNotNull();
        Assertions.assertThat(Time.valueOf("09:30:00")== createdAttendance.getPunchin());
        Assertions.assertThat(Time.valueOf("17:00:00")== createdAttendance.getPunchout());
        Assertions.assertThat(LocalDate.now()== createdAttendance.getAttendance_date());
    }

    @Test
    public void attendanceService_getAttendanceById(){
//Arrange
        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .punchout(Time.valueOf("17:00:00"))
                .attendance_date(LocalDate.now())
                .build();
        Mockito.when(attendanceRepository.findById(1)).thenReturn(java.util.Optional.of(attendance1));
        //Act
        Attendance attendance = attendanceService.getAttendanceById(1);
        //Assert
        Assertions.assertThat(attendance).isNotNull();
        Assertions.assertThat(attendance.getPunchin()).isEqualTo(Time.valueOf("09:30:00"));
        Assertions.assertThat(attendance.getPunchout()).isEqualTo(Time.valueOf("17:00:00"));
        Assertions.assertThat(attendance.getAttendance_date()).isEqualTo(LocalDate.now());

    }

    @Test
    public void attendanceService_deleteAttendance(){
        //Arrange
        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .punchout(Time.valueOf("17:00:00"))
                .attendance_date(LocalDate.now())
                .build();
        Mockito.when(attendanceRepository.existsById(1)).thenReturn(true);
        //Act
        attendanceService.deleteAttendance(1);
        //Assert
        Mockito.verify(attendanceRepository,Mockito.times(1)).deleteById(1);
    }

    @Test
    public void attendanceService_totalHoursWorked(){
        //Arrange
        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .punchout(Time.valueOf("17:00:00"))
                .attendance_date(LocalDate.now())
                .build();
        Mockito.when(attendanceRepository.totalHoursWorked(LocalDate.now(),LocalDate.now())).thenReturn(7);
        //Act
        Integer totalHoursWorked = attendanceService.totalHoursWorked(LocalDate.now(),LocalDate.now());
        //Assert
        Assertions.assertThat(totalHoursWorked).isEqualTo(7);
    }

    @Test
    public void attendanceService_punchIn(){
        //Arrange
        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .punchout(Time.valueOf("17:00:00"))
                .attendance_date(LocalDate.now())
                .build();
        Mockito.when(attendanceRepository.findLastAttendance()).thenReturn(attendance1);
        //Act
        String punchIn = attendanceService.punchIn();
        //Assert
        if(LocalTime.now().isBefore(LocalTime.of(18,00))&& LocalTime.now().isAfter(LocalTime.of(9,15)))
        Assertions.assertThat(punchIn).isEqualTo("You are late. Attendance recorded.Punched In at "+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+".");
        else if(LocalTime.now().isBefore(LocalTime.of(9,15)))
            Assertions.assertThat(punchIn).isEqualTo("Attendance recorded.Punched In at "+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+".");
        else if (LocalDate.now().getDayOfWeek()==DayOfWeek.SATURDAY || LocalDate.now().getDayOfWeek()== DayOfWeek.SUNDAY)
            Assertions.assertThat(punchIn).isEqualTo("It's a weekend. No attendance required.");
        else
            Assertions.assertThat(punchIn).isEqualTo("You are too late to punch in.");
    }

    @Test
    public void attendanceService_punchIn2(){
        //Arrange
        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .punchout(Time.valueOf("17:00:00"))
                .attendance_date(LocalDate.now())
                .build();
        Mockito.when(attendanceRepository.findAttendanceByAttendance_date(LocalDate.now())).thenReturn(java.util.Optional.of(attendance1));
        Mockito.when(attendanceRepository.findLastAttendance()).thenReturn(attendance1);
        //Act
        String punchIn = attendanceService.punchIn();

        //Assert

        Assertions.assertThat(punchIn).isEqualTo("Punch for today already done at "+ attendance1.getPunchin()+".");


    }

    //Null Punch out case
    @Test
    public void attendanceService_punchin3(){

        //Arrange
        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .attendance_date(LocalDate.now())
                .build();
        Mockito.when(attendanceRepository.findLastAttendance()).thenReturn(attendance1);
        //Act
        String punchIn = attendanceService.punchIn();

        //Assert
        if (LocalTime.now().isBefore(LocalTime.of(18,00))&& LocalTime.now().isAfter(LocalTime.of(9,15)))
            Assertions.assertThat(punchIn).isEqualTo("You have missed punched out on "+ attendance1.getAttendance_date()+".You are late. Attendance recorded.Punched In at "+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+".");
        else if (LocalTime.now().isBefore(LocalTime.of(9,15)))
            Assertions.assertThat(punchIn).isEqualTo("You have missed punched out on "+ attendance1.getAttendance_date()+".Attendance recorded.Punched In at "+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+".");
        else if (LocalDate.now().getDayOfWeek()==DayOfWeek.SATURDAY || LocalDate.now().getDayOfWeek()== DayOfWeek.SUNDAY)
            Assertions.assertThat(punchIn).isEqualTo("You have missed punched out on "+ attendance1.getAttendance_date()+".It's a weekend. No attendance required.");
        else
            Assertions.assertThat(punchIn).isEqualTo("You have missed punched out on "+ attendance1.getAttendance_date()+".You are too late to punch in.");
    }


    @Test
    public void attendanceService_punchout(){
        //Arrange
        Attendance attendance1 = Attendance.builder()
                .punchin(Time.valueOf("09:30:00"))
                .punchout(Time.valueOf("17:00:00"))
                .attendance_date(LocalDate.now())
                .build();

        Mockito.when(attendanceRepository.findAttendanceByAttendance_date(LocalDate.now())).thenReturn(java.util.Optional.of(attendance1));

        //Act
        String punchOut = attendanceService.punchOut();

        //Assert
        if(attendance1.getPunchout() != null)
            Assertions.assertThat(punchOut).isEqualTo("Punch for today already done at "+ attendance1.getPunchout()+".");
        else if (LocalTime.now().isBefore(LocalTime.of(18,00))&& LocalTime.now().isAfter(LocalTime.of(9,15)))
            Assertions.assertThat(punchOut).isEqualTo("Attendance recorded.Punched Out at "+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+".");
        else if (LocalTime.now().isBefore(LocalTime.of(9,15)))
            Assertions.assertThat(punchOut).isEqualTo("Attendance recorded.Punched Out at "+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+".");
        else if (LocalDate.now().getDayOfWeek()==DayOfWeek.SATURDAY || LocalDate.now().getDayOfWeek()== DayOfWeek.SUNDAY)
            Assertions.assertThat(punchOut).isEqualTo("It's a weekend. No attendance required.");
        else
            Assertions.assertThat(punchOut).isEqualTo("You are too early to punch out.");


    }


}
