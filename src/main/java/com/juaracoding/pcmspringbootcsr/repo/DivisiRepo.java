package com.juaracoding.pcmspringbootcsr.repo;

import com.juaracoding.pcmspringbootcsr.model.Divisi;
import com.juaracoding.pcmspringbootcsr.model.MenuHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DivisiRepo extends JpaRepository<Divisi,Long> {
    Page<Divisi> findByIsActive(Pageable page , Boolean isActive);
    List<Divisi> findByIsActive(Boolean isActive);//penambahan 21-12-2023
    Optional<Divisi> findByIsActiveAndIdDivisi(Boolean isActive, Long values);
    Page<Divisi> findByIsActiveAndIdDivisi(Pageable page,Boolean isActive, Long values);
    Page<Divisi> findByIsActiveAndNamaDivisiContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Divisi> findByIsActiveAndKodeDivisiContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Divisi> findByIsActiveAndDeskripsiDivisiContainsIgnoreCase(Pageable page , Boolean isActive, String values);

}
