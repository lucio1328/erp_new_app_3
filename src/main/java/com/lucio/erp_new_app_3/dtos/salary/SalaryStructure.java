package com.lucio.erp_new_app_3.dtos.salary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucio.erp_new_app_3.dtos.salary.component.SalaryComponent;

import lombok.Data;

@Data
public class SalaryStructure {
    @JsonProperty("name")
    private String name;

    @JsonProperty("employee")
    private String employee;

    @JsonProperty("company")
    private String company;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("from_date")
    private String fromDate;

    @JsonProperty("to_date")
    private String toDate;

    @JsonProperty("payment_frequency")
    private String paymentFrequency;

    @JsonProperty("daily_leave_amount")
    private Double dailyLeaveAmount;

    @JsonProperty("max_social_benefits")
    private Double maxSocialBenefits;

    @JsonProperty("is_based_on_timesheet")
    private boolean isBasedOnTimesheet;

    @JsonProperty("is_active")
    private String isActive;

    @JsonProperty("earnings")
    private List<SalaryComponent> earnings;

    @JsonProperty("deductions")
    private List<SalaryComponent> deductions;

    public void addEarning(SalaryComponent earning) {
        this.earnings.add(earning);
    }

    public void addDeduction(SalaryComponent deduction) {
        this.deductions.add(deduction);
    }
}
