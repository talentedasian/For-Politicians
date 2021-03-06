package com.example.demo.model.averageCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.demo.model.enums.Rating;

public class DecentSatisfactionAverageCalculator extends AverageCalculator{

	private final Rating satisfactionRating = Rating.DECENT;
	
	public Rating getSatisfactionRating() {
		return satisfactionRating;
	}

	public DecentSatisfactionAverageCalculator(double totalRating, double count) {
		super(totalRating, count);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateAverage() {
		return calculateUtil();
	}
	
	private double calculateUtil() {
		double averageRating = BigDecimal.valueOf(getTotalRating() / (getCount() + 1D))
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		
		return averageRating;
	}

}
