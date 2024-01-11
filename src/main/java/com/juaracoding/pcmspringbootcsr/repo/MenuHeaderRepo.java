package com.juaracoding.pcmspringbootcsr.repo;

import com.juaracoding.pcmspringbootcsr.model.MenuHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuHeaderRepo extends JpaRepository<MenuHeader,Long> {

    Page<MenuHeader> findByIsActive(Pageable page , Boolean isActive);
    List<MenuHeader> findByIsActive(Boolean isActive);//penambahan 21-12-2023
    Optional<MenuHeader> findByIsActiveAndIdMenuHeader(Boolean isActive, Long values);
    Page<MenuHeader> findByIsActiveAndIdMenuHeader(Pageable page,Boolean isActive, Long values);
    Page<MenuHeader> findByIsActiveAndNamaMenuHeaderContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<MenuHeader> findByIsActiveAndDeskripsiMenuHeaderContainsIgnoreCase(Pageable page , Boolean isActive, String values);
}
