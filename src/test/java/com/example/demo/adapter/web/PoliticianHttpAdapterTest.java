package com.example.demo.adapter.web;

import com.example.demo.BaseSpringHateoasTest;
import com.example.demo.adapter.out.jpa.PoliticiansJpaEntity;
import com.example.demo.adapter.out.repository.PoliticiansJpaRepository;
import com.example.demo.adapter.out.repository.PoliticiansRepository;
import com.example.demo.domain.entities.Rating;
import com.example.demo.domain.politicians.Name;
import com.example.demo.domain.politicians.PoliticianNumber;
import com.example.demo.domain.politicians.PoliticianTypes.PresidentialPolitician.PresidentialBuilder;
import com.example.demo.domain.politicians.PoliticianTypes.SenatorialPolitician.SenatorialBuilder;
import com.example.demo.domain.politicians.Politicians.PoliticiansBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;

import static com.example.demo.baseClasses.MockMvcAssertions.assertThat;
import static com.example.demo.baseClasses.MultiplePoliticianSetup.pagedPoliticianSetup;
import static com.example.demo.domain.enums.Rating.HIGH;
import static com.example.demo.domain.enums.Rating.LOW;
import static com.example.demo.domain.politicianNumber.PoliticianNumberCalculatorFactory.politicianCalculator;
import static com.example.demo.domain.politicians.Politicians.Type.PRESIDENTIAL;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PoliticianHttpAdapterTest extends BaseSpringHateoasTest {

    final String FIRST_NAME = "Mirriam";
    final String LAST_NAME = "Defensor";

    @Autowired PoliticiansRepository polRepo;

    @Autowired PoliticiansJpaRepository jpaRepo;

    PoliticiansBuilder politicianBuilder;

    final PoliticianNumber POLITICIAN_NUMBER = politicianCalculator(PRESIDENTIAL).calculatePoliticianNumber(Name.of(FIRST_NAME, LAST_NAME));

    String requestContent = """
            {
                "first_name" : "Mirriam",
                "last_name" : "Defensor",
                "rating" : 0.09,
                "most_significant_law_signed" : "Random Law",
                "type" : "PRESIDENTIAL"
            }
            """;

    @BeforeEach
    public void setup() {
        politicianBuilder = new PoliticiansBuilder(POLITICIAN_NUMBER)
                .setPoliticiansRating(null)
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setFullName()
                .setRating(new Rating(0D, 0D));
    }

    @AfterEach
    public void teardown() {
        jpaRepo.deleteAll();
    }

    @Test
    public void shouldSaveToDatabaseGivenWithCorrectAuthorization() throws Exception{
        var politician = new PresidentialBuilder(politicianBuilder)
                .setMostSignificantLawPassed("Random Law").build();

        MvcResult response = mvc.perform(post(create("/api/politicians/politician"))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Politician-Access", "password")
                .content(requestContent))

                    .andDo(document("politician", links(halLinks(),
                            linkWithRel("self").description("Link that points to politician resource"),
                            linkWithRel("rate-politician").description("Link that rates politicians"))))

                .andReturn();

        assertThat(response)
                .hasPath("id")
                    .isEqualTo(POLITICIAN_NUMBER.politicianNumber())
                .hasPath("name")
                    .isEqualTo(politician.fullName())
                .hasPath("rating")
                    .isEqualTo(politician.averageRating())
                .hasPath("most_significant_law_signed")
                    .isEqualTo(politician.getMostSignificantLawSigned());
    }

    @Test
    public void shouldReturn200OKAndCorrectFieldsWhenFindingIndividualPoliticians() throws Exception{
        var politician = new PresidentialBuilder(politicianBuilder)
                .setMostSignificantLawPassed("Random Law").build();

        polRepo.save(politician);

        MvcResult response = mvc.perform(get(create("/api/politicians/politician/" + POLITICIAN_NUMBER.politicianNumber()))
                        .contentType(MediaTypes.HAL_FORMS_JSON))
                .andExpect(status().isOk())

                .andDo(document("politician", links(halLinks(),
                        linkWithRel("self").description("Link that points to politician resource"),
                        linkWithRel("rate-politician").description("Link that rates politicians"))))

                .andReturn();

        assertThat(response)
                .hasPath("id")
                .isEqualTo(POLITICIAN_NUMBER.politicianNumber())
                .hasPath("name")
                .isEqualTo(politician.fullName())
                .hasPath("rating")
                .isEqualTo(politician.averageRating())
                .hasPath("most_significant_law_signed")
                .isEqualTo(politician.getMostSignificantLawSigned());
    }

    @Test
    public void listOfPoliticiansShouldSuccessfullyReturnPolymorphicPoliticiansAndShouldHaveSelfLinkIncluded() throws Exception{
        var presidential = new PresidentialBuilder(politicianBuilder)
                .setMostSignificantLawPassed("Random Law").build();
        var senatorial = new SenatorialBuilder(politicianBuilder
                            .setPoliticianNumber(POLITICIAN_NUMBER.politicianNumber() + "1")
                            .setRating(new Rating(2D, 9D)))
                .setTotalMonthsOfService(44).build();

        polRepo.save(presidential);
        polRepo.save(senatorial);

        String presidentialBasePath = "_embedded.presidentialPoliticianDtoList";
        String senatorialBasePath = "_embedded.senatorialPoliticianDtoList";

        mvc.perform(get("/api/politicians")
                        .contentType(MediaTypes.HAL_FORMS_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_FORMS_JSON))

                    .andExpect(jsonPath(presidentialBasePath.concat("[0].id"), equalTo(presidential.retrievePoliticianNumber())))
                    .andExpect(jsonPath(presidentialBasePath.concat("[0].name"), equalTo(presidential.fullName())))
                    .andExpect(jsonPath(presidentialBasePath.concat("[0].rating"), equalTo(presidential.averageRating())))
                    .andExpect(jsonPath(presidentialBasePath.concat("[0].satisfaction_rate"), equalTo(LOW.toString())))
                    .andExpect(jsonPath(presidentialBasePath.concat("[0].most_significant_law_signed"), equalTo(presidential.getMostSignificantLawSigned())))
                    .andExpect(jsonPath(senatorialBasePath.concat("[0].id"), equalTo(senatorial.retrievePoliticianNumber())))
                    .andExpect(jsonPath(senatorialBasePath.concat("[0].name"), equalTo(senatorial.fullName())))
                    .andExpect(jsonPath(senatorialBasePath.concat("[0].rating"), equalTo(senatorial.averageRating())))
                    .andExpect(jsonPath(senatorialBasePath.concat("[0].satisfaction_rate"), equalTo(HIGH.toString())))

                .andDo(document("politician", links(halLinks(),
                        linkWithRel("self").description("Link that points to all politicians"))));
    }

    @Test
    public void shouldReturnSinglePagedObjectWhenQueryingForFirstPageWithLimitLessThan10() throws Exception{
       jpaRepo.saveAll(pagedPoliticianSetup(30, politicianBuilder).stream().map(PoliticiansJpaEntity::from).toList());

       mvc.perform(get(URI.create("/api/politicians/politicians?page=0&items=20")))
               .andExpect(status().isOk())

               .andDo(document("politician", links(halLinks(),
                       linkWithRel("self").description("Link that points to all politicians with pagination"))));
    }

}
