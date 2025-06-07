package com.lucio.erp_new_app_3.dtos.salary.slip;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalarySlipDetail {
    @JsonProperty("data")
    SalarySlip data;
}
