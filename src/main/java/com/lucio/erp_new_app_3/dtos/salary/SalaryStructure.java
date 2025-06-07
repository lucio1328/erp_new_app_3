package com.lucio.erp_new_app_3.dtos.salary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalaryStructure {
    private String name;
    private String company;

    @JsonProperty("is_active")
    private String isActive;
    private String currency;

    @JsonProperty("earnings")
    List<SalaryEarning> earnings;

    @JsonProperty("deductions")
    List<SalaryDeduction> deductions;
}
