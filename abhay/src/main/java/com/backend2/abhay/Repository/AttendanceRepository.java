package com.backend2.abhay.Repository;

import com.backend2.abhay.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    @Query("SELECT a FROM Attendance a WHERE a.attendance_date = ?1")
    Optional<Attendance> findAttendanceByAttendance_date(LocalDate currentDate);


    @Query(value = "SELECT * FROM attendance ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Attendance findLastAttendance();

    @Query("SELECT (SUM(FUNCTION('TIME_TO_SEC', a.punchout) - FUNCTION('TIME_TO_SEC', a.punchin)) / 3600) FROM Attendance a WHERE a.attendance_date BETWEEN ?1 AND ?2")

    Integer totalHoursWorked(LocalDate startDate, LocalDate endDate);
}
