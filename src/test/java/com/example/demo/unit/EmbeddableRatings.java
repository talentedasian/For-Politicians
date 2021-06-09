package com.example.demo.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.model.averageCalculator.AverageCalculator;
import com.example.demo.model.entities.Politicians;
import com.example.demo.model.entities.Rating;
import com.example.demo.repository.RatingRepository;

@ExtendWith(MockitoExtension.class)
public class EmbeddableRatings {

	@Mock
	public RatingRepository repo;
	@Mock
	public AverageCalculator calculator; 
	
	public Politicians politician;
	
	@BeforeEach
	public void setup() {
		politician = new Politicians();
		politician.setRepo(repo);
		politician.setId(1);
		politician.setFirstName("Mirriam");
		politician.setLastName("Defensor");
		politician.setPoliticiansRating(new ArrayList<>());
		politician.setRating(new Rating
				(0.012D,
				2.022D, 
				calculator));
	}
	
	@Test
	public void testLogicOfAverage() {
		when(repo.countByPolitician_Id(1)).thenReturn(0L);
		
		politician.calculateTotalAmountOfRating(9.8822D);
		
		assertThat(politician.getRating().calculateAverage(), 
				equalTo(9.9D));
	}
	
	@Test
	public void testLogicOfTotalAmount() {
		when(repo.countByPolitician_Id(1)).thenReturn(0L);
		
		assertThat(politician.getRating().calculateTotalAmountOfRating(8.8876D, convertLongToDouble(repo.countByPolitician_Id(1))), 
				equalTo(8.9D));
	}
	
	private Double convertLongToDouble(long longValue) {
		return Double.valueOf(String.valueOf(longValue));
	}
	
}
