package com.spring.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.company.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

}
