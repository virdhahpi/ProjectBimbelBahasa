package com.juaracoding.pcmspringbootcsr.model;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 04/01/2024 10:31
@Last Modified 04/01/2024 10:31
Version 1.0
*/

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/*
    Modul Code = 13
 */
@Entity
@Table(name = "MstSiswa")
public class Siswa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDSiswa")
    private Long idSiswa;
    @Column(name = "NamaSiswa")
    private String namaSiswa;
    @Column(name = "Alamat")
    private String alamat;
    @Column(name = "NoHP", unique = true)
    private String noHp;
    @Column(name = "Email")
    private String email;
    @ManyToMany
    @JoinTable(name = "MapSiswaMataPelajaran",
            joinColumns = {
                    @JoinColumn(name = "IDSiswa",referencedColumnName = "IDSiswa")}, inverseJoinColumns = {
            @JoinColumn (name = "IDMataPelajaran",referencedColumnName = "IDMataPelajaran")}, uniqueConstraints = @UniqueConstraint(columnNames = {
            "IDSiswa", "IDMataPelajaran" }))
    private List<MataPelajaran> listMataPelajaran;
    /*
        start audit trails
     */
    @Column(name ="CreatedDate" , nullable = false)
    private Date createdDate = new Date();

    @Column(name = "CreatedBy", nullable = false)
    private Integer createdBy=1;

    @Column(name = "ModifiedDate")
    private Date modifiedDate;
    @Column(name = "ModifiedBy")
    private Integer modifiedBy;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive = true;
    /*
        end audit trails
     */

    public Long getIdSiswa() {
        return idSiswa;
    }

    public void setIdSiswa(Long idSiswa) {
        this.idSiswa = idSiswa;
    }

    public String getNamaSiswa() {
        return namaSiswa;
    }

    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<MataPelajaran> getListMataPelajaran() {
        return listMataPelajaran;
    }

    public void setListMataPelajaran(List<MataPelajaran> listMataPelajaran) {
        this.listMataPelajaran = listMataPelajaran;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

