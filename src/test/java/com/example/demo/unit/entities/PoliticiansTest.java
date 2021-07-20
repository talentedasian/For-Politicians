package com.example.demo.unit.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.model.entities.Politicians;

public class PoliticiansTest {

	Politicians politician = new Politicians.PoliticiansBuilder("123polNumber")
			.setFirstName("Test")
			.setLastName("Name")
			.build();
	
	@Test
	public void assertCustomEqualsMethod() {
		var pol = new Politicians.PoliticiansBuilder("123polNumber").build();
		
		var polWrongNumber = new Politicians.PoliticiansBuilder("999polNumber").build();
		
		assertTrue(politician.equals(pol));
		assertFalse(politician.equals(polWrongNumber));
	}
	
	@Test
	public void testNullLastNameInBuilder() {
		var pol = new Politicians.PoliticiansBuilder("123polNumber")
				.setFirstName("Test")
				.setFullName()
				.build();
		
		assertEquals("Test", pol.getFullName());
	}
	
	@Test
	public void testFullNameInBuilder() {
		var pol = new Politicians.PoliticiansBuilder("123polNumber")
				.setFirstName("Test")
				.setLastName("Name")
				.setFullName()
				.build();
		
		assertEquals("Test Name", pol.getFullName());
	}
	
	@Test
	public void testNullFirstAndLastNameInBuilder() {
		assertThrows(IllegalArgumentException.class, 
				() -> new Politicians.PoliticiansBuilder("123polNumber")
				.setFullName());
	}
	
}