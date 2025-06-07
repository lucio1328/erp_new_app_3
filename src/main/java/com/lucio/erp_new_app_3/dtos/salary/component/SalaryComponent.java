package com.lucio.erp_new_app_3.dtos.salary.component;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalaryComponent {

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

    @JsonProperty("salary_component")
    private String salaryComponent;

    @JsonProperty("salary_component_abbr")
    private String salaryComponentAbbr;

    private String type;
    private String description;

    @JsonProperty("depends_on_payment_days")
    private int dependsOnPaymentDays;

    @JsonProperty("is_tax_applicable")
    private int isTaxApplicable;

    @JsonProperty("deduct_full_tax_on_selected_payroll_date")
    private int deductFullTaxOnSelectedPayrollDate;

    @JsonProperty("variable_based_on_taxable_salary")
    private int variableBasedOnTaxableSalary;

    @JsonProperty("is_income_tax_component")
    private int isIncomeTaxComponent;

    @JsonProperty("exempted_from_income_tax")
    private int exemptedFromIncomeTax;

    @JsonProperty("round_to_the_nearest_integer")
    private int roundToTheNearestInteger;

    @JsonProperty("statistical_component")
    private int statisticalComponent;

    @JsonProperty("do_not_include_in_total")
    private int doNotIncludeInTotal;

    @JsonProperty("remove_if_zero_valued")
    private int removeIfZeroValued;

    private int disabled;
    private String condition;
    private double amount;

    @JsonProperty("amount_based_on_formula")
    private int amountBasedOnFormula;

    private String formula;

    @JsonProperty("is_flexible_benefit")
    private int isFlexibleBenefit;

    @JsonProperty("max_benefit_amount")
    private double maxBenefitAmount;

    @JsonProperty("pay_against_benefit_claim")
    private int payAgainstBenefitClaim;

    @JsonProperty("only_tax_impact")
    private int onlyTaxImpact;

    @JsonProperty("create_separate_payment_entry_against_benefit_claim")
    private int createSeparatePaymentEntryAgainstBenefitClaim;
}
