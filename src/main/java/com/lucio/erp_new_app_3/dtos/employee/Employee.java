package com.lucio.erp_new_app_3.dtos.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class Employee {

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

    @JsonProperty("naming_series")
    private String namingSeries;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("employee_name")
    private String employeeName;

    private String gender;

    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    private String salutation;

    @JsonProperty("date_of_joining")
    private LocalDate dateOfJoining;

    private String image;
    private String status;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("create_user_permission")
    private int createUserPermission;

    private String company;
    private String department;

    @JsonProperty("employment_type")
    private String employmentType;

    @JsonProperty("employee_number")
    private String employeeNumber;

    private String designation;

    @JsonProperty("reports_to")
    private String reportsTo;

    private String branch;
    private String grade;

    @JsonProperty("job_applicant")
    private String jobApplicant;

    @JsonProperty("scheduled_confirmation_date")
    private LocalDate scheduledConfirmationDate;

    @JsonProperty("final_confirmation_date")
    private LocalDate finalConfirmationDate;

    @JsonProperty("contract_end_date")
    private LocalDate contractEndDate;

    @JsonProperty("notice_number_of_days")
    private int noticeNumberOfDays;

    @JsonProperty("date_of_retirement")
    private LocalDate dateOfRetirement;

    @JsonProperty("cell_number")
    private String cellNumber;

    @JsonProperty("personal_email")
    private String personalEmail;

    @JsonProperty("company_email")
    private String companyEmail;

    @JsonProperty("prefered_contact_email")
    private String preferedContactEmail;

    @JsonProperty("prefered_email")
    private String preferedEmail;

    private int unsubscribed;

    @JsonProperty("current_address")
    private String currentAddress;

    @JsonProperty("current_accommodation_type")
    private String currentAccommodationType;

    @JsonProperty("permanent_address")
    private String permanentAddress;

    @JsonProperty("permanent_accommodation_type")
    private String permanentAccommodationType;

    @JsonProperty("person_to_be_contacted")
    private String personToBeContacted;

    @JsonProperty("emergency_phone_number")
    private String emergencyPhoneNumber;

    private String relation;

    @JsonProperty("attendance_device_id")
    private String attendanceDeviceId;

    @JsonProperty("holiday_list")
    private String holidayList;

    @JsonProperty("default_shift")
    private String defaultShift;

    @JsonProperty("expense_approver")
    private String expenseApprover;

    @JsonProperty("leave_approver")
    private String leaveApprover;

    @JsonProperty("shift_request_approver")
    private String shiftRequestApprover;

    private double ctc;

    @JsonProperty("salary_currency")
    private String salaryCurrency;

    @JsonProperty("salary_mode")
    private String salaryMode;

    @JsonProperty("payroll_cost_center")
    private String payrollCostCenter;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("bank_ac_no")
    private String bankAcNo;

    private String iban;

    @JsonProperty("marital_status")
    private String maritalStatus;

    @JsonProperty("family_background")
    private String familyBackground;

    @JsonProperty("blood_group")
    private String bloodGroup;

    @JsonProperty("health_details")
    private String healthDetails;

    @JsonProperty("health_insurance_provider")
    private String healthInsuranceProvider;

    @JsonProperty("health_insurance_no")
    private String healthInsuranceNo;

    @JsonProperty("passport_number")
    private String passportNumber;

    @JsonProperty("valid_upto")
    private LocalDate validUpto;

    @JsonProperty("date_of_issue")
    private LocalDate dateOfIssue;

    @JsonProperty("place_of_issue")
    private String placeOfIssue;

    private String bio;

    @JsonProperty("resignation_letter_date")
    private LocalDate resignationLetterDate;

    @JsonProperty("relieving_date")
    private LocalDate relievingDate;

    @JsonProperty("held_on")
    private LocalDate heldOn;

    @JsonProperty("new_workplace")
    private String newWorkplace;

    @JsonProperty("leave_encashed")
    private String leaveEncashed;

    @JsonProperty("encashment_date")
    private LocalDate encashmentDate;

    @JsonProperty("reason_for_leaving")
    private String reasonForLeaving;

    private String feedback;
    private int lft;
    private int rgt;

    @JsonProperty("old_parent")
    private String oldParent;
}

