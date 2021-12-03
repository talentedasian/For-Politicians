package com.example.demo.dtomapper;

import com.example.demo.adapter.in.dtoRequest.AddPoliticianDTORequest;
import com.example.demo.adapter.in.dtoRequest.AddPresidentialPoliticianDTORequest;
import com.example.demo.domain.AverageRating;
import com.example.demo.domain.entities.Name;
import com.example.demo.domain.entities.PoliticianNumber;
import com.example.demo.domain.entities.PoliticianTypes;
import com.example.demo.domain.entities.Politicians;
import io.jsonwebtoken.lang.Assert;

import static com.example.demo.domain.entities.Politicians.Type.PRESIDENTIAL;
import static com.example.demo.domain.politicianNumber.PoliticianNumberCalculatorFactory.politicianCalculator;

class PresidentialDTOUnwrapper extends PoliticianDTOUnwrapper {

    @Override
    public Politicians unWrapDTO(AddPoliticianDTORequest dto) {
        Assert.state(dto instanceof AddPresidentialPoliticianDTORequest,
                "request dto must be a type of presidential");

        return dtoToEntity((AddPresidentialPoliticianDTORequest) dto);
    }

    private Politicians dtoToEntity(AddPresidentialPoliticianDTORequest dto) {
        final PoliticianNumber POLITICIAN_NUMBER = politicianCalculator(PRESIDENTIAL)
                .calculatePoliticianNumber(new Name(dto.getFirstName(), dto.getLastName()));

        Politicians politician = new Politicians.PoliticiansBuilder(POLITICIAN_NUMBER)
                .setFirstName(dto.getFirstName())
                .setLastName(dto.getLastName())
                .setAverageRating(AverageRating.NO_RATING_YET)
                .setPoliticiansRating(null)
                .build();

        return new PoliticianTypes.PresidentialPolitician.PresidentialBuilder(politician)
                .setMostSignificantLawPassed(dto.getMostSignificantLawSigned())
                .build();
    }

}
