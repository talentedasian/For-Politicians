package com.example.demo.domain.domainModel;

import com.example.demo.domain.politicians.PoliticianNumber;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.politicians.Politicians;
import com.example.demo.domain.politicians.Politicians.PoliticiansBuilder;
import com.example.demo.domain.politicians.PoliticianTypes;
import com.example.demo.domain.politicians.PoliticianTypes.PresidentialPolitician.PresidentialBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.example.demo.baseClasses.NumberTestFactory.POL_NUMBER;
import static org.junit.jupiter.api.Assertions.*;

public class PresidentialPoliticianTest {

	final PoliticianNumber POLITICIAN_NUMBER = POL_NUMBER();
	
	PoliticiansBuilder politicianBuilder = new Politicians.PoliticiansBuilder(POLITICIAN_NUMBER)
			.setFirstName("Test")
			.setLastName("Name");
	
	PresidentialBuilder presidentialBuilder = new PoliticianTypes.PresidentialPolitician.PresidentialBuilder(politicianBuilder);

	/*
		basically testing the hashcode functionality
	 */
	@Test
	public void shouldReturnObjectInHashMap() {
		var actualPolitician = presidentialBuilder.build();

		Map<Politicians,Politicians> map = new HashMap<>();
		map.put(actualPolitician,actualPolitician);

		var samePoliticianNumber = presidentialBuilder.setBuilder(politicianBuilder.setPoliticianNumber(POLITICIAN_NUMBER.politicianNumber()))
				.build();

		assertEquals(actualPolitician, map.get(samePoliticianNumber));
	}

	@Test
	public void keyShouldNotBeInMapEvenWithSamePoliticianNumberWithDifferentType() {
		var actualPolitician = presidentialBuilder.build();

		Map<Politicians,Politicians> map = new HashMap<>();
		map.put(actualPolitician,actualPolitician);

		var differentPoliticianType = new PoliticianTypes.SenatorialPolitician.SenatorialBuilder(politicianBuilder)
				.setTotalMonthsOfService(89)
				.build();

		assertFalse(map.containsKey(differentPoliticianType));
	}
	
	@Test
	public void shouldReturnFalseWithDifferentPoliticianType() {
		var differentPoliticianType = new PoliticianTypes.SenatorialPolitician.SenatorialBuilder(politicianBuilder)
				.setTotalMonthsOfService(89)
				.build();
		
		assertFalse(presidentialBuilder.build().equals(differentPoliticianType));
	}
	
}