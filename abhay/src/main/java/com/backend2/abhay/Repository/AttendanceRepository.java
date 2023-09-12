package com.backend2.abhay.Repository;

import com.backend2.abhay.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer>{
    @Query("SELECT a FROM Attendance a WHERE a.attendance_date = :currentDate")
    Optional<Object> findAttendanceByAttendance_date(LocalDate currentDate);

    @Query(value = "SELECT * FROM attendance ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Attendance findLastAttendance();
}
