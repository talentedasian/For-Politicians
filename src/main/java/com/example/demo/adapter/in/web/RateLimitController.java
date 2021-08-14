package com.example.demo.adapter.in.web;

import com.example.demo.adapter.dto.RateLimitJpaDto;
import com.example.demo.adapter.in.web.dto.RateLimitDto;
import com.example.demo.adapter.in.web.jwt.JwtProviderHttpServletRequest;
import com.example.demo.adapter.out.repository.RateLimitAdapterService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/rate-limit")
public class RateLimitController {
	
	private RateLimitAdapterService service;

	public RateLimitController(RateLimitAdapterService service) {
		this.service = service;
	}

	@GetMapping("/{politicianNumber}")
	public ResponseEntity<RateLimitDto> findRateLimitOnCurrentUser(@PathVariable String politicianNumber,
																	  HttpServletRequest req) {
		Claims jwt = JwtProviderHttpServletRequest.decodeJwt(req).getBody();
		final String accountNumber = jwt.getId();

		RateLimitJpaDto rateLimitQueried = service.findUsingAccountNumberAndPoliticianNumber(new RateLimitJpaDto(accountNumber, politicianNumber));

		var selfLink = linkTo(methodOn(RateLimitController.class)
				.findRateLimitOnCurrentUser(rateLimitQueried.getPoliticianNumber(), req))
				.withRel("self");

		return new ResponseEntity<RateLimitDto>(RateLimitDto.from(rateLimitQueried.toRateLimit()), HttpStatus.OK);
	}
}
