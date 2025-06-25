package com.lucio.erp_new_app_3.repository;

import org.springframework.stereotype.Repository;

import com.lucio.erp_new_app_3.models.salary.assignment.SalaryStructureAssignment;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface StructureAssignmentRepository extends JpaRepository<SalaryStructureAssignment, String> {

}
