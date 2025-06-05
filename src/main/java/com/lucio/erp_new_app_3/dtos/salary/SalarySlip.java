package com.lucio.erp_new_app_3.dtos.salary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SalarySlip {

    private String name;
    private String owner;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime creation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime modified;

    @JsonProperty("modified_by")
    private String modifiedBy;

    private int docstatus;
    private int idx;
    private String employee;

    @JsonProperty("employee_name")
    private String employeeName;

    private String company;
    private String department;
    private String designation;
    private String branch;

    @JsonProperty("posting_date")
    private LocalDate postingDate;

    @JsonProperty("letter_head")
    private String letterHead;

    private String status;

    @JsonProperty("salary_withholding")
    private String salaryWithholding;

    @JsonProperty("salary_withholding_cycle")
    private String salaryWithholdingCycle;

    private String currency;

    @JsonProperty("exchange_rate")
    private double exchangeRate;

    @JsonProperty("payroll_frequency")
    private String payrollFrequency;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("salary_structure")
    private String salaryStructure;

    @JsonProperty("payroll_entry")
    private String payrollEntry;

    @JsonProperty("mode_of_payment")
    private String modeOfPayment;

    @JsonProperty("salary_slip_based_on_timesheet")
    private int salarySlipBasedOnTimesheet;

    @JsonProperty("deduct_tax_for_unclaimed_employee_benefits")
    private int deductTaxForUnclaimedEmployeeBenefits;

    @JsonProperty("deduct_tax_for_unsubmitted_tax_exemption_proof")
    private int deductTaxForUnsubmittedTaxExemptionProof;

    @JsonProperty("total_working_days")
    private double totalWorkingDays;

    @JsonProperty("unmarked_days")
    private double unmarkedDays;

    @JsonProperty("leave_without_pay")
    private double leaveWithoutPay;

    @JsonProperty("absent_days")
    private double absentDays;

    @JsonProperty("payment_days")
    private double paymentDays;

    @JsonProperty("total_working_hours")
    private double totalWorkingHours;

    @JsonProperty("hour_rate")
    private double hourRate;

    @JsonProperty("base_hour_rate")
    private double baseHourRate;

    @JsonProperty("gross_pay")
    private double grossPay;

    @JsonProperty("base_gross_pay")
    private double baseGrossPay;

    @JsonProperty("gross_year_to_date")
    private double grossYearToDate;

    @JsonProperty("base_gross_year_to_date")
    private double baseGrossYearToDate;

    @JsonProperty("total_deduction")
    private double totalDeduction;

    @JsonProperty("base_total_deduction")
    private double baseTotalDeduction;

    @JsonProperty("net_pay")
    private double netPay;

    @JsonProperty("base_net_pay")
    private double baseNetPay;

    @JsonProperty("rounded_total")
    private double roundedTotal;

    @JsonProperty("base_rounded_total")
    private double baseRoundedTotal;

    @JsonProperty("year_to_date")
    private double yearToDate;

    @JsonProperty("base_year_to_date")
    private double baseYearToDate;

    @JsonProperty("month_to_date")
    private double monthToDate;

    @JsonProperty("base_month_to_date")
    private double baseMonthToDate;

    @JsonProperty("total_in_words")
    private String totalInWords;

    @JsonProperty("base_total_in_words")
    private String baseTotalInWords;

    private double ctc;

    @JsonProperty("income_from_other_sources")
    private double incomeFromOtherSources;

    @JsonProperty("total_earnings")
    private double totalEarnings;

    @JsonProperty("non_taxable_earnings")
    private double nonTaxableEarnings;

    @JsonProperty("standard_tax_exemption_amount")
    private double standardTaxExemptionAmount;

    @JsonProperty("tax_exemption_declaration")
    private double taxExemptionDeclaration;

    @JsonProperty("deductions_before_tax_calculation")
    private double deductionsBeforeTaxCalculation;

    @JsonProperty("annual_taxable_amount")
    private double annualTaxableAmount;

    @JsonProperty("income_tax_deducted_till_date")
    private double incomeTaxDeductedTillDate;

    @JsonProperty("current_month_income_tax")
    private double currentMonthIncomeTax;

    @JsonProperty("future_income_tax_deductions")
    private double futureIncomeTaxDeductions;

    @JsonProperty("total_income_tax")
    private double totalIncomeTax;

    @JsonProperty("journal_entry")
    private String journalEntry;

    @JsonProperty("amended_from")
    private String amendedFrom;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("bank_account_no")
    private String bankAccountNo;

    @JsonProperty("earnings")
    List<SalaryEarning> earnings;

    @JsonProperty("deductions")
    List<SalaryDeduction> deductions;

    List<Double> componentsDef=new ArrayList<>();
    String mois;
    String envoye;
}

