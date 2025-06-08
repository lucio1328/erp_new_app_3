package com.lucio.erp_new_app_3.dtos.salary;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucio.erp_new_app_3.dtos.salary.component.SalaryComponent;

import lombok.Data;

@Data
public class SalaryStructure {
    private String name;
    private String company;

    @JsonProperty("is_active")
    private String isActive;
    private String currency;

    @JsonProperty("pay_frequency")
    private String payFrequency;

    @JsonProperty("daily_leave_amount")
    private Double dailyLeaveAmount;

    @JsonProperty("max_social_benefits")
    private Double maxSocialBenefits;

    @JsonProperty("is_based_on_timesheet")
    private boolean isBasedOnTimesheet;

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
