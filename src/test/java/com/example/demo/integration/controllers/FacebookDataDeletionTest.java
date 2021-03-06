package com.example.demo.integration.controllers;

import static com.example.demo.jwt.JwtProvider.createJwtWithFixedExpirationDate;
import static com.example.demo.model.userRaterNumber.facebook.FacebookUserRaterNumberImplementor.with;
import static java.net.URI.create;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.oauth2.FacebookDataDeletion;
import com.example.demo.service.RatingService;

@ExtendWith(SpringExtension.class)
public class FacebookDataDeletionTest {
	
	MockMvc mvc;
	
	@Mock RatingService service;
	
	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.standaloneSetup(new FacebookDataDeletion(service))
				.alwaysDo(print())
				.build();
	}
	
	@Test
	public void shouldCallDeleteMethodOnUserData() throws Exception {
		String accNumber = with("Test Name", "123").calculateEntityNumber().getAccountNumber();
		
		String jwt = createJwtWithFixedExpirationDate("test@gmail.com", "123", "Test Name"); 
				
		mvc.perform(delete(create("/facebook/delete"))
				.header("Authorization", "Bearer " + jwt))
				.andExpect(status().isNoContent());
		
		verify(service, times(1)).deleteByAccountNumber(accNumber);
	}

}
