package com.juaracoding.pcmspringbootcsr.repo;

import com.juaracoding.pcmspringbootcsr.model.Siswa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiswaRepo extends JpaRepository<Siswa, Long> {
    Page<Siswa> findByIsActive(Pageable page , Boolean isActive);
    List<Siswa> findByIsActive(Boolean isActive);//penambahan 21-12-2023
    Optional<Siswa> findByIsActiveAndIdSiswa(Boolean isActive, Long values);
    Optional<Siswa> findByIsActiveAndNoHp(Boolean isActive, String noHp);
    Page<Siswa> findByIsActiveAndIdSiswa(Pageable page,Boolean isActive, Long values);
    Page<Siswa> findByIsActiveAndNamaSiswaContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Siswa> findByIsActiveAndAlamatContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Siswa> findByIsActiveAndNoHpContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<Siswa> findByIsActiveAndEmailContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    
}
