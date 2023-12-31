package com.backend2.abhay.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

@Builder
@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Attendance{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @DateTimeFormat (pattern = "HH:mm:ss")
    private Time punchin;
    @DateTimeFormat (pattern = "HH:mm:ss")
    private Time punchout;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendance_date;




}
