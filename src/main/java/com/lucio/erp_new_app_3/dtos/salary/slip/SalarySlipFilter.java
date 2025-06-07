package com.lucio.erp_new_app_3.dtos.salary.slip;

import lombok.Data;

@Data
public class SalarySlipFilter {
    private String employee;
    private String startDate;  // format ISO "yyyy-MM-dd"
    private String endDate;    // format ISO "yyyy-MM-dd"
}
