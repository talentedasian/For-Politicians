package com.example.demo.dto;

import com.example.demo.annotations.ExcludeFromJacocoGeneratedCoverage;
import com.example.demo.model.entities.politicians.Politicians;
import com.example.demo.model.enums.Rating;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SenatorialPoliticianDTO extends PoliticianDTO{

    private final Integer monthsOfService;

    private final String mostSignificantLawMade;

    public int getMonthsOfService() {
        return this.monthsOfService;
    }

    public String getMostSignificantLawMade() {
        return this.mostSignificantLawMade;
    }

    public SenatorialPoliticianDTO(Politicians entity, Rating satisfactionRate, int monthsOfService, String lawMade) {
        super(entity.getFullName(), entity.getPoliticianNumber(), entity.getRating().getAverageRating(), satisfactionRate);
        this.monthsOfService = monthsOfService;
        this.mostSignificantLawMade = lawMade;
    }

    @Override
    @ExcludeFromJacocoGeneratedCoverage
    public String toString() {
        return "SenatorialPoliticianDTO [name=" + this.getName() + ", id=" + this.getId() +
                ", rating=" + this.getRating() + ", satisfactionRate=" + this.getSatisfactionRate() +
                ", monthsOfService=" + this.monthsOfService + ", mostSignificantLawSigned=" + this.mostSignificantLawMade + "]";
    }

    @Override
    @ExcludeFromJacocoGeneratedCoverage
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        result = prime * result + ((this.getRating() == null) ? 0 : this.getRating().hashCode());
        result = prime * result + ((this.getSatisfactionRate() == null) ? 0 : this.getSatisfactionRate().hashCode());
        result = prime * result + ((monthsOfService == null) ? 0 : monthsOfService.hashCode());
        result = prime * result + ((mostSignificantLawMade == null) ? 0 : mostSignificantLawMade.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SenatorialPoliticianDTO other = (SenatorialPoliticianDTO) obj;
        if (other.monthsOfService == null)
            return false;
        if (this.getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!this.getId().equals(other.getId()))
            return false;
        if (this.getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!this.getName().equals(other.getName()))
            return false;
        if (this.getRating() == null) {
            if (other.getRating() != null)
                return false;
        } else if (!this.getRating().equals(other.getRating()))
            return false;
        if (this.getSatisfactionRate() != other.getSatisfactionRate())
            return false;
        if (mostSignificantLawMade == null) {
            if (other.mostSignificantLawMade != null)
                return false;
        } else if (!mostSignificantLawMade.equals(other.mostSignificantLawMade)) {
            return false;
        }
        if (!other.monthsOfService.equals(monthsOfService)) {
            return false;
        }
        return true;
    }

}
