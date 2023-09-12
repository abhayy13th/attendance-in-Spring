package com.backend2.abhay.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Getter
@Setter
public class TotalWorkHoursDTO {
    private LocalDate startDate;
    private LocalDate endDate;
}
