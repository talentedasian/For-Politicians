package com.example.demo.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.demo.controller.RatingsController;
import com.example.demo.dto.RatingDTO;

@Component
public class RatingAssembler implements SimpleRepresentationModelAssembler<RatingDTO>{

	@Override
	public void addLinks(EntityModel<RatingDTO> resource) {
		resource.add(linkTo(methodOn(RatingsController.class)
				.getRatingById(resource.getContent().getRater().getUserAccountNumber()))
			.withRel("self"));				
	}

	@Override
	public void addLinks(CollectionModel<EntityModel<RatingDTO>> resources) {
		// TODO Auto-generated method stub
		
	}

}