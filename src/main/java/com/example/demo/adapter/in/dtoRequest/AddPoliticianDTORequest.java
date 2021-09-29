package com.example.demo.adapter.in.dtoRequest;

import com.example.demo.annotations.ExcludeFromJacocoGeneratedCoverage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = AddSenatorialPoliticianDtoRequest.class, name = "SENATORIAL"),
		@JsonSubTypes.Type(value = AddPresidentialPoliticianDTORequest.class, name = "PRESIDENTIAL")
})
public class AddPoliticianDTORequest {

	@NotNull
	private String firstName;
	
	@NotNull
	private String lastName;
	
	@NotNull
	private BigDecimal rating;

	@NotNull
	private String type;

	public String getType() {
		return type;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public BigDecimal getRating() {
		return rating;
	}

	@ExcludeFromJacocoGeneratedCoverage
	public AddPoliticianDTORequest() {
		super();
	}

	@ExcludeFromJacocoGeneratedCoverage
	public AddPoliticianDTORequest(@NotNull String firstName, @NotNull String lastName,
			@NotNull BigDecimal rating, @NotNull String type) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.rating = rating;
		this.type = type;
	}

	@Override
	@ExcludeFromJacocoGeneratedCoverage
	public String toString() {
		return "AddPoliticianDTORequest{" +
				"firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", rating=" + rating +
				", type='" + type + '\'' +
				'}';
	}
}
