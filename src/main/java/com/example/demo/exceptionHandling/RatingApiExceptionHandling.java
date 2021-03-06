package com.example.demo.exceptionHandling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.controller.RatingsController;
import com.example.demo.exceptions.PoliticianNotFoundException;
import com.example.demo.exceptions.RateLimitNotFoundException;
import com.example.demo.exceptions.RatingsNotFoundException;
import com.example.demo.exceptions.UserRateLimitedOnPoliticianException;

@RestControllerAdvice(assignableTypes = { RatingsController.class })
public class RatingApiExceptionHandling {

	@ExceptionHandler(RatingsNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionModel handleRatingsNotFoundException(RatingsNotFoundException ex) {
		ExceptionModel exceptionModel = new ExceptionModel();
		exceptionModel.setCode("404");
		exceptionModel.setErr(ex.getMessage());
		
		return exceptionModel;
	}
	
	@ExceptionHandler(PoliticianNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ExceptionModel> handlePoliticianNotFoundException(PoliticianNotFoundException e) {
		var exceptionModel = new ExceptionModel();
		exceptionModel.setCode("404");
		exceptionModel.setErr(e.getMessage());
		
		return new ResponseEntity<ExceptionModel>(exceptionModel, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UserRateLimitedOnPoliticianException.class)
	@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
	public ResponseEntity<ExceptionModel> handleRateLimitedException(UserRateLimitedOnPoliticianException e) {
		var exceptionModel = new ExceptionModel();
		exceptionModel.setCode("429");
		exceptionModel.setErr(e.getMessage());
		exceptionModel.setOptional("This endpoint only allows one request per week");
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Retry-After", e.getDaysLeft().toString() + " days");
		return new ResponseEntity<ExceptionModel>(exceptionModel, headers, HttpStatus.TOO_MANY_REQUESTS);
	}
	
	@ExceptionHandler(RateLimitNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ExceptionModel> handleRateLimitNotFoundException(RateLimitNotFoundException e) {
		var exceptionModel = new ExceptionModel();
		exceptionModel.setCode("404");
		exceptionModel.setErr(e.getMessage());
		
		return new ResponseEntity<ExceptionModel>(exceptionModel, HttpStatus.NOT_FOUND);
	}
	
}
