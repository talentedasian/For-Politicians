package com.example.demo.adapter.dto;

import com.example.demo.domain.entities.RateLimit;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class RateLimitJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String accountNumber, politicianNumber;

	@Column(nullable = false)
	private LocalDate dateCreated;

	public Long getId() {
		return id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getPoliticianNumber() {
		return politicianNumber;
	}

	protected RateLimitJpaEntity() {}

	protected RateLimitJpaEntity(LocalDate dateCreated, String accountNumber, String politicianNumber) {
		super();
		this.dateCreated = dateCreated;
		this.accountNumber = accountNumber;
		this.politicianNumber = politicianNumber;
	}

	public RateLimitJpaEntity(String accountNumber, String politicianNumber) {
		this.accountNumber = accountNumber;
		this.politicianNumber = politicianNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RateLimitJpaEntity that = (RateLimitJpaEntity) o;

		if (!accountNumber.equals(that.accountNumber)) return false;
		if (!politicianNumber.equals(that.politicianNumber)) return false;
		return dateCreated.equals(that.dateCreated);
	}

	@Override
	public int hashCode() {
		int result = accountNumber.hashCode();
		result = 31 * result + politicianNumber.hashCode();
		result = 31 * result + dateCreated.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "RateLimitJpaEntity{" +
				"accountNumber='" + accountNumber + '\'' +
				", politicianNumber='" + politicianNumber + '\'' +
				", dateCreated=" + dateCreated +
				'}';
	}

	public static RateLimitJpaEntity of(RateLimit rateLimit) {
		return new RateLimitJpaEntity(rateLimit.dateCreated(), rateLimit.id(), rateLimit.politicianNumber());
	}

	public RateLimit toRateLimit() {
		return new RateLimit(accountNumber, politicianNumber, dateCreated);
	}

}
