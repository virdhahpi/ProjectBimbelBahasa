package com.juaracoding.pcmspringbootcsr.repo;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 02/01/2024 13:22
@Last Modified 02/01/2024 13:22
Version 1.0
*/

import com.juaracoding.pcmspringbootcsr.model.Bahasa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BahasaRepo extends JpaRepository<Bahasa, Long> {
    Page<Bahasa> findByIsActive(Pageable page , Boolean isActive);
    List<Bahasa> findByIsActive(Boolean isActive);//penambahan 21-12-2023
    Optional<Bahasa> findByIsActiveAndIdBahasa(Boolean isActive, Long values);
    Page<Bahasa> findByIsActiveAndIdBahasa(Pageable page,Boolean isActive, Long values);
    Page<Bahasa> findByIsActiveAndNamaBahasaContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Bahasa> findByIsActiveAndKodeBahasaContainsIgnoreCase(Pageable page , Boolean isActive, String values);
}
