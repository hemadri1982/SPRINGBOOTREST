package com.spring.company.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.spring.company.model.Company;
import com.spring.company.repository.CompanyRepository;

@RestController
@RequestMapping("/companies")
public class CompanyController {
	@Autowired
	private CompanyRepository companyRepo;

	@RequestMapping(method = RequestMethod.GET)
	public List<Company> findCompanies() {
		return companyRepo.findAll();
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public Company findCompany(@PathVariable Integer id) {
		return companyRepo.findOne(id);
	}

	@RequestMapping(value = "/add/", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public @ResponseBody Company addCompany(@RequestBody Company company) {
		return companyRepo.saveAndFlush(company);
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, consumes = "application/json")
	public Company updateCompany(@RequestBody Company updatedCompany,
			@PathVariable Integer id) {
		updatedCompany.setCompanyId(id);
		return companyRepo.saveAndFlush(updatedCompany);
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public void deleteCompany(@PathVariable Integer id) {
		companyRepo.delete(id);
	}
}
