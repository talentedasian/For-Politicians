package com.example.demo.domain.entities;

import com.example.demo.baseClasses.NumberTestFactory;
import com.example.demo.domain.AverageRating;
import com.example.demo.domain.Score;
import com.example.demo.domain.entities.Politicians.PoliticiansBuilder;
import com.example.demo.exceptions.UserRateLimitedOnPoliticianException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.example.demo.baseClasses.BuilderFactory.createPolRating;
import static com.example.demo.baseClasses.BuilderFactory.createRater;
import static com.example.demo.baseClasses.NumberTestFactory.POL_NUMBER;
import static com.example.demo.domain.AverageRating.NO_RATING_YET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("Domain")
public class PoliticiansTest {

	final PoliticianNumber POLITICIAN_NUMBER = POL_NUMBER();
	
	PoliticiansBuilder politicianBuilder;

	@BeforeEach
	public void setup() {
		politicianBuilder = new PoliticiansBuilder(POLITICIAN_NUMBER)
				.setFirstName("Test")
				.setLastName("Name");
	}

	@Test
	public void testNullLastNameInBuilder() {
		var politicianWithNoLastName = new PoliticiansBuilder(POLITICIAN_NUMBER)
				.setFirstName("Test")
				.build();

		assertEquals("Test", politicianWithNoLastName.fullName());
	}

	@Test
	public void testFullNameInBuilderWithNoFirstName() throws Exception{
		var politicianWithNullFirstName = new PoliticiansBuilder(POLITICIAN_NUMBER)
				.setLastName("Name")
				.build();

		assertThrows(IllegalStateException.class, () -> politicianWithNullFirstName.fullName());
	}

	@Test
	public void testFullNameInBuilder() {
		var politicianWithFirstAndLastName = politicianBuilder
				.build();

		assertEquals("Test Name", politicianWithFirstAndLastName.fullName());
	}

	@Test
	public void testEquality() throws Exception{
		String FIRST_NAME = "Random";
		var politicianThatsGoingToBeCompared = new PoliticiansBuilder(POL_NUMBER())
				.setFirstName(FIRST_NAME)
				.build();

		var politicianToCompare = new PoliticiansBuilder(POL_NUMBER())
				.setAverageRating(NO_RATING_YET)
				.setFirstName(FIRST_NAME)
				.setLastName("Any")
				.build();

		assertThat(politicianThatsGoingToBeCompared)
				.isEqualTo(politicianToCompare);
	}

	@Test
	public void politicianShouldHaveNoAverageRatingWhenJustCreatedAndNoAverageRatingIsSpecified() throws Exception{
		Politicians politician = politicianBuilder.build();

		AverageRating averageRating = politician.average();

		assertThat(averageRating)
				.isEqualTo(NO_RATING_YET);
	}

	@Test
	public void shouldReturnAverageWithValueOfTheScoreToAddAsAverageRatingWhenPoliticianHasNoAverageRatingYet() throws Exception{
		Politicians politician = politicianBuilder.build();

		Score score = Score.of("2.232");

		assertThat(politician.calculateAverageRating(score))
				.isEqualTo(AverageRating.of(score.ratingExact()));
	}

	@Test
	public void countsOfRatingsShouldDecreaseWhenADeleteOfRatingHappens() throws UserRateLimitedOnPoliticianException {
		var rater = createRater(NumberTestFactory.ACC_NUMBER().accountNumber());

		Politicians politician = politicianBuilder.build();
		var rating = createPolRating(Score.of("2.243"), rater, politician);

		politician.rate(rating);
		politician.rate(rating);

		politician.deleteRate(rating);

		assertThat(politician.countsOfRatings())
				.isEqualTo(1);
	}

	@Test
	public void totalCountsOfRatingsShouldStillBe2WhenRaterDeletesPolitician() throws UserRateLimitedOnPoliticianException {
		int EXPECTED_NUMBER_OF_RATINGS = 2;

		var rater = createRater(NumberTestFactory.ACC_NUMBER().accountNumber());

		Politicians politician = politicianBuilder
				.setAverageRating(NO_RATING_YET).build();
		var rating = createPolRating(Score.of("2.243"), rater, politician);

		politician.rate(rating);
		politician.rate(rating);

		politician.deleteRate(rating);

		assertThat(politician.totalCountsOfRatings())
				.isEqualTo(EXPECTED_NUMBER_OF_RATINGS);
	}

	@Test
	public void testCalculateAverageRatingMethodThatAlreadyHasARatingBeforeHand() throws Exception{
		String EXPECTED_AVERAGE_RATING = "1.821";

		Politicians politician = politicianBuilder
				.setAverageRating(AverageRating.of("2.231"))
				.setTotalCount(2)
				.build();

		assertThat(politician.calculateAverageRating(Score.of("1")))
				.isEqualTo(AverageRating.of(EXPECTED_AVERAGE_RATING));
	}

	@Test
	public void testAverageRating() throws Exception{
		String EXPECTED_AVERAGE_RATING = "2.430";

		var rater = createRater(NumberTestFactory.ACC_NUMBER().accountNumber());

		Politicians politician = politicianBuilder
				.setAverageRating(AverageRating.of("2.231"))
				.setTotalCount(4)
				.build();

		var rating = createPolRating(Score.of("3.2232"), rater, politician);

		politician.rate(rating);

		assertThat(politician.averageRating())
				.isEqualTo(EXPECTED_AVERAGE_RATING);
	}

	@Test
	public void ratePoliticianShouldIncreaseTotalCountsOfRatings() throws Exception{
		int EXPECTED_NUMBER_OF_TOTAL_RATINGS = 4;

		var rater = createRater(NumberTestFactory.ACC_NUMBER().accountNumber());

		Politicians politician = politicianBuilder
				.setAverageRating(NO_RATING_YET).build();
		var rating = createPolRating(Score.of("2.243"), rater, politician);

		politician.rate(rating);
		politician.rate(rating);
		politician.rate(rating);
		politician.rate(rating);

		assertThat(politician.totalCountsOfRatings())
				.isEqualTo(EXPECTED_NUMBER_OF_TOTAL_RATINGS);
	}

}