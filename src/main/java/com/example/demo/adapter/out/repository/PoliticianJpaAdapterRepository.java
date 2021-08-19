package com.example.demo.adapter.out.repository;

import com.example.demo.adapter.out.jpa.PoliticiansJpaEntity;
import com.example.demo.domain.politicians.Politicians;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PoliticianJpaAdapterRepository implements PoliticiansRepository {

    private final PoliticiansJpaRepository politicianRepository;

    public PoliticianJpaAdapterRepository(PoliticiansJpaRepository politicianRepository) {
        this.politicianRepository = politicianRepository;
    }

    @Override
    public Politicians save(Politicians politician) {
        PoliticiansJpaEntity entitySaved = politicianRepository.save(PoliticiansJpaEntity.from(politician));

        return entitySaved.toPoliticians();
    }

    @Override
    public Politicians update(Politicians politician) {
        return save(politician);
    }

    @Override
    public List<Politicians> findByLastNameAndFirstName(String lastName, String firstName) {
        return politicianRepository.findByLastNameAndFirstName(lastName, firstName).stream()
                .map(entity -> entity.toPoliticians())
                .toList();
    }

    @Override
    public Optional<Politicians> findByPoliticianNumber(String polNumber) {
        Optional<PoliticiansJpaEntity> entity = politicianRepository.findById(polNumber);

        return entity.isEmpty() ? Optional.empty() : Optional.of(entity.get().toPoliticians());
    }

    @Override
    public boolean existsByPoliticianNumber(String polNumber) {
        return politicianRepository.existsById(polNumber);
    }

    @Override
    public void deleteByPoliticianNumber(String polNumber) {
        politicianRepository.deleteById(polNumber);
    }

    @Override
    public List<Politicians> findAll() {
        return politicianRepository.findAll().stream()
                .map(PoliticiansJpaEntity::toPoliticians)
                .toList();
    }
}