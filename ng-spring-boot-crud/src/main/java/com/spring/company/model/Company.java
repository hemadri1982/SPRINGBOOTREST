package com.spring.company.model;

import java.io.Serializable;

import javax.persistence.*;


@Entity
public class Company  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer companyId;
	@Column
	private String name;
	@Column
	private String adress;

	@Column
	private String city;

	@Column
	private String country;

	@Column
	private String phoneNumber;


	public Company(){

	}

	public Company(String name, String adress, String city,
			String country, String phoneNumber) {	
		this.name = name;
		this.adress = adress;
		this.city = city;
		this.country = country;
		this.phoneNumber = phoneNumber;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	} 



}
