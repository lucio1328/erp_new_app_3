package com.lucio.erp_new_app_3.dtos.salary;

import java.util.List;

import lombok.Data;

@Data
public class SalaryElement {
    List<SalaryEarning> salaryEarnings;
    List<SalaryDeduction> salaryDeductions;
}
