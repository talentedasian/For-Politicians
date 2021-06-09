package com.example.demo.dtomapper.interfaces;

import java.util.List;

import com.example.demo.dto.PoliticianDTO;
import com.example.demo.model.entities.Politicians;

public interface PoliticianDTOMapper extends DTOMapper<PoliticianDTO,Politicians>{
 
	 List<PoliticianDTO> mapToDTO(List<Politicians> entity);
}
