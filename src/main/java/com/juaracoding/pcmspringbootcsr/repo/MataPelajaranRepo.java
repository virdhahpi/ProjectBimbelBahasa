package com.juaracoding.pcmspringbootcsr.repo;

import com.juaracoding.pcmspringbootcsr.model.Akses;
import com.juaracoding.pcmspringbootcsr.model.MataPelajaran;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MataPelajaranRepo extends JpaRepository<MataPelajaran, Long> {
    Page<MataPelajaran> findByIsActive(Pageable page , Boolean isActive);
    List<MataPelajaran> findByIsActive(Boolean isActive);//penambahan 21-12-2023
    Optional<MataPelajaran> findByIsActiveAndIdMataPelajaran(Boolean isActive, Long values);
    Page<MataPelajaran> findByIsActiveAndIdMataPelajaran(Pageable page,Boolean isActive, Long values);
    Page<MataPelajaran> findByIsActiveAndNamaMataPelajaranContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<MataPelajaran> findByIsActiveAndKodeMataPelajaranContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    Page<MataPelajaran> findByIsActiveAndKodeKelasContainsIgnoreCase(Pageable page , Boolean isActive, String values);
    @Query("SELECT x FROM MataPelajaran x WHERE x.bahasa.namaBahasa LIKE CONCAT('%',?1,'%') and x.isActive = ?2 ")
    Page<MataPelajaran> findByNamaBahasa(Pageable page , String values,Boolean isActive);
    @Query("SELECT x FROM MataPelajaran x WHERE x.tutor.namaTutor LIKE CONCAT('%',?1,'%') and x.isActive = ?2 ")
    Page<MataPelajaran> findByNamaTutor(Pageable page , String values,Boolean isActive);
    @Query("SELECT x FROM MataPelajaran x WHERE x.tutor.noHp = ?1 and x.isActive = ?2 ")
    List<MataPelajaran> findByNoHpTutor(String values,Boolean isActive);
}
