package com.juaracoding.pcmspringbootcsr.repo;

import com.juaracoding.pcmspringbootcsr.model.Tutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TutorRepo extends JpaRepository<Tutor, Long> {
    Page<Tutor> findByIsActive(Pageable page , Boolean isActive);
    List<Tutor> findByIsActive(Boolean isActive);//penambahan 21-12-2023
    Optional<Tutor> findByIsActiveAndIdTutor(Boolean isActive, Long values);
    Page<Tutor> findByIsActiveAndIdTutor(Pageable page,Boolean isActive, Long values);
    Page<Tutor> findByIsActiveAndNamaTutorContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Tutor> findByIsActiveAndAlamatContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Tutor> findByIsActiveAndNoHpContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Tutor> findByIsActiveAndEmailContainsIgnoreCase(Pageable page , Boolean isActive, String values);
}
