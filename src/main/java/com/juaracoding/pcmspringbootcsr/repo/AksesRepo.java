package com.juaracoding.pcmspringbootcsr.repo;

import com.juaracoding.pcmspringbootcsr.model.Akses;
import com.juaracoding.pcmspringbootcsr.model.Bahasa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AksesRepo extends JpaRepository<Akses,Long> {

    Page<Akses> findByIsActive(Pageable page , Boolean isActive);
    List<Akses> findByIsActive(Boolean isActive);
    Optional<Akses> findByIsActiveAndIdAkses(Boolean isActive, Long values);
    Page<Akses> findByIsActiveAndIdAkses(Pageable page,Boolean isActive, Long values);
    Page<Akses> findByIsActiveAndNamaAksesContainsIgnoreCase(Pageable page , Boolean isActive, String values);

    @Query("SELECT x FROM Akses x WHERE x.divisi.namaDivisi = ?1 and x.isActive = 1 ")
    Page<Akses> findByNamaDivisi(Pageable page , String values);

}