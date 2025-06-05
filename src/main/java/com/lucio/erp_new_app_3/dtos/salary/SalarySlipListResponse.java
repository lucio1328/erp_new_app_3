package com.lucio.erp_new_app_3.dtos.salary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SalarySlipListResponse {
    @JsonProperty("data")
    List<SalarySlip> data;
}
