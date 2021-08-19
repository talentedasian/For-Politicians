package com.example.demo.domain;

import com.example.demo.domain.entities.PoliticiansRating;
import com.example.demo.domain.entities.Rating;
import com.example.demo.domain.entities.UserRater;
import com.example.demo.domain.enums.PoliticalParty;
import com.example.demo.domain.politicians.PoliticianTypes;
import com.example.demo.domain.politicians.Politicians;
import com.example.demo.domain.politicians.Politicians.PoliticiansBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/*
    Citizens refer to basically the users who rate politicians
 */
@ExtendWith(SpringExtension.class)
public class CitizensRatingPoliticiansTest {

    PoliticiansBuilder politicianBuilder = new PoliticiansBuilder()
            .setPoliticiansRating(null)
            .setFirstName("Random")
            .setLastName("Name")
            .setFullName()
            .setRating(new Rating(0D, 0D));

    Politicians politicians;

    @Mock RateLimitRepository rateLimitRepo;

    @BeforeEach
    public void setup() {
        politicians = new PoliticianTypes.PresidentialPolitician.PresidentialBuilder(politicianBuilder).build();
    }

    @Test
    public void ratingShouldBeCalculatedAsExpectedWhenRatePoliticianCalled() {
        Double EXPECTED_CALCULATED_AVERAGE_RATING = 2.734D;

        var rater = new UserRater.Builder()
                .setAccountNumber("12345")
                .setName("Random Name")
                .setEmail("test@gmail.com")
                .setPoliticalParty(PoliticalParty.DDS)
                .build();

        var firstRating = new PoliticiansRating.Builder()
                .setRating(2.243D)
                .setRepo(rateLimitRepo)
                .setRater(rater)
                .setPolitician(politicians)
                .build();
        var fourScaledRatingForHalfDownRoundingMode = new PoliticiansRating.Builder()
                .setRating(3.22326D)
                .setRepo(rateLimitRepo)
                .setRater(rater)
                .setPolitician(politicians)
                .build();

        firstRating.ratePolitician();
        fourScaledRatingForHalfDownRoundingMode.ratePolitician();

        assertThat(politicians.getRating().getAverageRating())
                .isEqualTo(EXPECTED_CALCULATED_AVERAGE_RATING);
    }

    @Test
    public void countsOfRatingsShouldReflectOnPoliticianAsCitizensRatePoliticians() {
        int EXPECTED_NUMBER_OF_RATINGS = 2;

        var rater = new UserRater.Builder()
                .setAccountNumber("12345")
                .setName("Random Name")
                .setEmail("test@gmail.com")
                .setPoliticalParty(PoliticalParty.DDS)
                .build();

        var firstRating = new PoliticiansRating.Builder()
                .setRating(2.243D)
                .setRepo(rateLimitRepo)
                .setRater(rater)
                .setPolitician(politicians)
                .build();
        var secondRating = new PoliticiansRating.Builder()
                .setRating(3.22326D)
                .setRepo(rateLimitRepo)
                .setRater(rater)
                .setPolitician(politicians)
                .build();

        firstRating.ratePolitician();
        secondRating.ratePolitician();

        assertThat(politicians.countsOfRatings())
                .isEqualTo(EXPECTED_NUMBER_OF_RATINGS);
    }

}