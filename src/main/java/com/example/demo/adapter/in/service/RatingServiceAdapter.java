package com.example.demo.adapter.in.service;

import com.example.demo.adapter.in.dtoRequest.AddRatingDTORequest;
import com.example.demo.adapter.in.web.jwt.JwtProviderHttpServletRequest;
import com.example.demo.adapter.out.repository.PoliticiansRepository;
import com.example.demo.adapter.out.repository.RatingJpaRepository;
import com.example.demo.adapter.out.repository.RatingRepository;
import com.example.demo.adapter.web.dto.RatingDTO;
import com.example.demo.domain.Score;
import com.example.demo.domain.entities.Politicians;
import com.example.demo.domain.entities.PoliticiansRating;
import com.example.demo.domain.entities.UserRateLimitService;
import com.example.demo.domain.entities.UserRater;
import com.example.demo.domain.enums.PoliticalParty;
import com.example.demo.exceptions.PoliticianNotFoundException;
import com.example.demo.exceptions.RatingsNotFoundException;
import com.example.demo.exceptions.UserRateLimitedOnPoliticianException;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class RatingServiceAdapter {

    private final RatingService service;
    private final PoliticiansRepository polRepo;
    private final RatingJpaRepository ratingJpaRepository;

    public RatingServiceAdapter(RatingRepository ratingRepo, UserRateLimitService rateLimitService, PoliticiansRepository polRepo, RatingJpaRepository ratingJpaRepository) {
        this.ratingJpaRepository = ratingJpaRepository;
        this.service = new RatingService(ratingRepo, polRepo, rateLimitService);
        this.polRepo = polRepo;
    }

    public RatingDTO findUsingId(String id) {
        return RatingDTO.from(service.findById(id)
                .orElseThrow(() -> new RatingsNotFoundException("Rating with " + id + " not found")));
    }

    public RatingDTO saveRatings(AddRatingDTORequest dtoRequest, HttpServletRequest req) throws UserRateLimitedOnPoliticianException {
        Claims jwts = JwtProviderHttpServletRequest.decodeJwt(req).getBody();

        Politicians politician = polRepo.findByPoliticianNumber(dtoRequest.getId())
                .orElseThrow(PoliticianNotFoundException::new);

        var rater = new UserRater.Builder()
                .setName(jwts.get("fullName", String.class))
                .setAccountNumber(jwts.getId())
                .setEmail(jwts.getSubject())
                .setPoliticalParty(PoliticalParty.valueOf(dtoRequest.getPoliticalParty()))
                .build();

        PoliticiansRating rating = new PoliticiansRating.Builder()
                .setPolitician(politician)
                .setRating(Score.of(dtoRequest.getRating().doubleValue()))
                .setRater(rater)
                .build();

        return RatingDTO.from(service.saveRatings(rating));
    }

    public List<RatingDTO> findRatingsUsingFacebookEmail(String email) {
        return service.findRatingsByFacebookEmail(email).stream()
                .map(entity -> RatingDTO.from(entity))
                .toList();
    }

    public List<RatingDTO> findRatingsUsingAccountNumber(String accNumber) {
        return service.findRatingsByAccountNumber(accNumber).stream()
                .map(entity -> RatingDTO.from(entity))
                .toList();
    }

    public void deleteUsingId(Integer id) {
        service.deleteById(id);
    }

    public void deleteUsingAccountNumber(String accountNumber) {
        service.deleteByAccountNumber(accountNumber);
    }

}
