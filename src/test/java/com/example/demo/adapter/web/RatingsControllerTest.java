package com.example.demo.adapter.web;

import com.example.demo.BaseSpringHateoasTest;
import com.example.demo.adapter.in.dtoRequest.AddRatingDTORequest;
import com.example.demo.adapter.out.repository.PoliticiansJpaRepository;
import com.example.demo.adapter.out.repository.PoliticiansRepository;
import com.example.demo.adapter.out.repository.RatingRepository;
import com.example.demo.domain.RateLimitRepository;
import com.example.demo.domain.Score;
import com.example.demo.domain.entities.*;
import com.example.demo.domain.entities.PoliticianTypes.PresidentialPolitician;
import com.example.demo.domain.entities.PoliticianTypes.PresidentialPolitician.PresidentialBuilder;
import com.example.demo.domain.enums.PoliticalParty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.adapter.in.web.jwt.JwtUtils.fixedExpirationDate;
import static com.example.demo.baseClasses.MockMvcAssertions.assertThat;
import static com.example.demo.baseClasses.NumberTestFactory.ACC_NUMBER;
import static com.example.demo.baseClasses.NumberTestFactory.POL_NUMBER;
import static com.example.demo.domain.enums.PoliticalParty.GREY_ZONE;
import static java.math.BigDecimal.valueOf;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RatingsControllerTest extends BaseSpringHateoasTest {

    @Autowired RatingRepository ratingRepo;
    @Autowired PoliticiansRepository polRepo;
    @Autowired PoliticiansJpaRepository jpaR;
    @Autowired RateLimitRepository rateLimitRepo;
    @Autowired UserRateLimitService rateLimitService;

    PresidentialPolitician politician = new PresidentialBuilder(new Politicians.PoliticiansBuilder(POL_NUMBER())
                .setFirstName("Fake")
                .setTotalRating(valueOf(4)))
            .build();

    UserRater rater = new UserRater.Builder()
            .setName("Fake")
            .setAccountNumber(ACC_NUMBER().accountNumber())
            .setEmail("test@gmail.com")
            .setPoliticalParty(PoliticalParty.DDS)
            .build();
    PoliticiansRating politiciansRating = new PoliticiansRating.Builder(politician)
            .setRater(rater)
            .setRating(Score.of(1.321321))
            .build();

    @Test
    public void shouldThrowBadRequestWithInappropriateRaterAccountNumber() throws Exception{
        PoliticiansRating savedRating = ratingRepo.save(politiciansRating);

        final String inappropriateAccountNumber = String.valueOf(savedRating.id());
        mvc.perform(get(create("/api/ratings/ratings/" + inappropriateAccountNumber)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnInappropriateAccountNumberAsBody() throws Exception{
        PoliticiansRating savedRating = ratingRepo.save(politiciansRating);

        final String inappropriateAccountNumber = String.valueOf(savedRating.id());
        MvcResult response = mvc.perform(get(create("/api/ratings/ratings/" + inappropriateAccountNumber))).andReturn();

        assertThat(response)
                .hasPath("reason")
                .isEqualTo("Inappropriate account number given")
                .hasPath("action")
                .isEqualTo("Check appropriate account numbers for valid account numbers");
    }

    @Test
    @Transactional
    public void shouldSaveToDatabaseAndReturn201Created() throws Exception{
        polRepo.save(politician);
        //make sure user is not rate limited
        rateLimitRepo.deleteUsingIdAndPoliticianNumber(ACC_NUMBER().accountNumber(), PoliticianNumber.of(politician.retrievePoliticianNumber()));

        double RATING = 9.21D;
        var requestObject = new AddRatingDTORequest(valueOf(RATING), politician.retrievePoliticianNumber(), GREY_ZONE.toString());
        String requestJsonString = new ObjectMapper().writeValueAsString(requestObject);

        String NAME = "Jake";
        String ID = ACC_NUMBER().accountNumber();
        String EMAIL = "t@gmail.com";
        String jwt = fixedExpirationDate(EMAIL, ID, NAME);

        mvc.perform(post(create("/api/ratings/rating/"))
                        .content(requestJsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(content().contentType(MediaTypes.HAL_FORMS_JSON))

                .andExpect(jsonPath("rater.account_number", equalTo(ID)))
                .andExpect(jsonPath("rater.name", equalTo(NAME)))
                .andExpect(jsonPath("rating", equalTo(RATING)));
    }

    @Test
    public void shouldReturnRatingWithPolitician() throws Exception{
        PoliticiansRating savedRating = ratingRepo.save(politiciansRating);

        mvc.perform(get(create("/api/ratings/rating/" + savedRating.id())))
                .andExpect(status().isOk())

                    .andExpect(jsonPath("rating", equalTo(savedRating.score())))
                    .andExpect(jsonPath("id", equalTo(savedRating.id().toString())))
                    .andExpect(jsonPath("politician.id", equalTo(politician.retrievePoliticianNumber())));
    }

    @Test
    public void shouldHaveSelfLink_RateLimitLink_And_PoliticianLink() throws Exception{
        PoliticiansRating savedRating = ratingRepo.save(politiciansRating);

        mvc.perform(get(create("/api/ratings/rating/" + savedRating.id())))
                .andExpect(content().contentType(MediaTypes.HAL_FORMS_JSON))

                    .andDo(document("rating", links(halLinks(),
                            linkWithRel("self").description("Link that points to the rating entity"),
                            linkWithRel("rate-limit").description("Link that gives you information about the rate limit imposed on the rater"),
                            linkWithRel("politician").description("Link that points to a politician that the rating rates to"))));
    }

    @Test
    @Transactional
    public void shouldHaveHalTemplateToRatePoliticianAgainWhenJwtIsPresentAndUserIsNotRateLimited() throws Exception{
        PoliticiansRating savedRating = ratingRepo.save(politiciansRating);
        //make sure user is not rate limited
        rateLimitRepo.deleteUsingIdAndPoliticianNumber(ACC_NUMBER().accountNumber(), PoliticianNumber.of(politician.retrievePoliticianNumber()));

        String jwt = fixedExpirationDate("t@gmail.com", ACC_NUMBER().accountNumber(), "Jake");

        String targetLink = "/api/ratings/rating";

        mvc.perform(get(create("/api/ratings/rating/" + savedRating.id()))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(content().contentType(MediaTypes.HAL_FORMS_JSON))

                    .andExpect(jsonPath("_templates.default.target", containsStringIgnoringCase(targetLink)));
    }

    @Test
    public void shouldNotHaveHalTemplatesWhenSentJwtInRequestAndIsRateLimited() throws Exception{
        polRepo.save(politician);
        PoliticiansRating savedRating = ratingRepo.save(politiciansRating);

        rateLimitService.rateLimitUser(AccountNumber.of(rater.returnUserAccountNumber()),
                PoliticianNumber.of(politician.retrievePoliticianNumber()));

        String jwt = fixedExpirationDate("t@gmail.com", ACC_NUMBER().accountNumber(), "Jake");

        mvc.perform(get(create("/api/ratings/rating/" + savedRating.id()))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(content().contentType(MediaTypes.HAL_FORMS_JSON))

                    .andExpect(jsonPath("_templates.default").doesNotExist());
    }

}
