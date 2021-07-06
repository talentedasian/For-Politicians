package com.example.demo.unit.rateLimit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.example.demo.model.entities.RateLimit;

public class RateLimitTest {

	@Test
	public void shouldNotBeRateLimited() {
		var rate = new RateLimit();
		rate.setId("1");
		rate.setPoliticianNumber("2");
		rate.setDateCreated(LocalDate.now().minusDays(7L));
		
		assertTrue(rate.isNotRateLimited());
	}
	
	@Test
	public void shouldBeRateLimited() {
		var rate = new RateLimit();
		rate.setId("1");
		rate.setPoliticianNumber("2");
		rate.setDateCreated(LocalDate.now().minusDays(5L));
		
		assertTrue(!rate.isNotRateLimited());
	}
	
}