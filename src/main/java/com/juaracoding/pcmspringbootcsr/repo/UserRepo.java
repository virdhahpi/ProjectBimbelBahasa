package com.juaracoding.pcmspringbootcsr.repo;

import com.juaracoding.pcmspringbootcsr.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsernameOrNoHPOrEmailAndIsActive(String username, String noHp, String email,Boolean isActive);
    Optional<User> findByUsernameOrNoHPOrEmail(String username, String noHp, String email);

    Page<User> findByIsActive(Pageable page , Boolean isActive);

    Page<User> findByIsActive(Pageable page , byte byteIsDelete);
    Page<User> findByIsActiveAndIdUser(Pageable page, Boolean isActive, Long values);
    Page<User> findByIsActiveAndNamaLengkapContainsIgnoreCase(Pageable page , Boolean isActive, String values);

    List<User> findByIsActive(Boolean isActive);
    public Optional<User> findByEmail(String value);
    public Optional<User> findByUsername(String userName);
    @Query("SELECT x FROM User x WHERE x.akses.namaAkses LIKE CONCAT('%',?1,'%') and x.isActive = ?2 ")
    Page<User> findByNamaAkses(Pageable page , String values,Boolean isActive);
}
