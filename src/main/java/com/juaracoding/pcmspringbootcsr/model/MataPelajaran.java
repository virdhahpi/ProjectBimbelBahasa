package com.juaracoding.pcmspringbootcsr.model;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 03/01/2024 7:52
@Last Modified 03/01/2024 7:52
Version 1.0
*/

/*
    Modul Code = 11
 */
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table (name = "MstMataPelajaran")
public class MataPelajaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDMataPelajaran")
    private Long idMataPelajaran;
    @Column(name = "NamaMataPelajaran")
    private String namaMataPelajaran;
    @Column(name = "KodeMataPelajaran")
    private String kodeMataPelajaran;
    @Column(name = "KodeKelas")
    private String kodeKelas;
    @ManyToOne
    @JoinColumn(name ="IDBahasa")
    private Bahasa bahasa;

    @ManyToOne
    @JoinColumn(name ="IDTutor")
    private Tutor tutor;

    @ManyToMany(mappedBy = "listMataPelajaran")
    private List<Siswa> listSiswa;
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
    public Long getIdMataPelajaran() {
        return idMataPelajaran;
    }

    public void setIdMataPelajaran(Long idMataPelajaran) {
        this.idMataPelajaran = idMataPelajaran;
    }

    public String getNamaMataPelajaran() {
        return namaMataPelajaran;
    }

    public void setNamaMataPelajaran(String namaMataPelajaran) {
        this.namaMataPelajaran = namaMataPelajaran;
    }

    public String getKodeMataPelajaran() {
        return kodeMataPelajaran;
    }

    public void setKodeMataPelajaran(String kodeMataPelajaran) {
        this.kodeMataPelajaran = kodeMataPelajaran;
    }

    public String getKodeKelas() {
        return kodeKelas;
    }

    public void setKodeKelas(String kodeKelas) {
        this.kodeKelas = kodeKelas;
    }

    public Bahasa getBahasa() {
        return bahasa;
    }

    public void setBahasa(Bahasa bahasa) {
        this.bahasa = bahasa;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public List<Siswa> getListSiswa() {
        return listSiswa;
    }

    public void setListSiswa(List<Siswa> listSiswa) {
        this.listSiswa = listSiswa;
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

