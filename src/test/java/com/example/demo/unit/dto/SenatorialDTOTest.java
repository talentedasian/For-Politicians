package com.example.demo.unit.dto;

import com.example.demo.baseClasses.BaseClassForPoliticianDTOTests;
import com.example.demo.dto.PoliticianDTO;
import com.example.demo.dto.SenatorialPoliticianDTO;
import com.example.demo.dtomapper.PoliticiansDtoMapper;
import com.example.demo.model.enums.Rating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SenatorialDTOTest extends BaseClassForPoliticianDTOTests {

    @Test
    public void testEqualsLogic() {
        PoliticianDTO actual = new SenatorialPoliticianDTO(politicianBuilder.build(), Rating.LOW, monthsOfService, LAW_MADE);

        assertEquals(actual,
                new PoliticiansDtoMapper().mapToDTO(senatorialBuilder
                        .setMostSignificantLawMade(LAW_MADE)
                        .build()));
    }

    @Test
    public void shouldNotBeEqualDueToType() {
        PoliticianDTO actual = new SenatorialPoliticianDTO(politicianBuilder.build(), Rating.LOW, monthsOfService, LAW_MADE);

        assertNotEquals(actual,
                new PoliticiansDtoMapper().mapToDTO(presidentialBuilder
                        .setMostSignificantLawPassed(LAW_SIGNED)
                        .build()));
    }

}
