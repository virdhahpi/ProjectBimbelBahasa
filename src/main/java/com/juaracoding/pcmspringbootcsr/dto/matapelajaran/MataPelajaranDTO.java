package com.juaracoding.pcmspringbootcsr.dto.matapelajaran;/*
Created by IntelliJ IDEA 2022.2.3 (Community Edition)
Build #IC-232.10072.27, built on October 11, 2023
@Author LENOVO a.k.a. Virdha Haniva Pratiwie Ishak
Java Developer
Created on 03/01/2024 8:05
@Last Modified 03/01/2024 8:05
Version 1.0
*/

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.juaracoding.pcmspringbootcsr.constant.ConstantMessageMataPelajaran;
import com.juaracoding.pcmspringbootcsr.dto.bahasa.BahasaDTO;
import com.juaracoding.pcmspringbootcsr.dto.bahasa.BahasaOptionDTO;
import com.juaracoding.pcmspringbootcsr.dto.siswa.SiswaDTO;
import com.juaracoding.pcmspringbootcsr.dto.tutor.TutorDTO;
import com.juaracoding.pcmspringbootcsr.dto.tutor.TutorOptionDTO;
import com.juaracoding.pcmspringbootcsr.model.Bahasa;
import com.juaracoding.pcmspringbootcsr.model.Tutor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MataPelajaranDTO {

    private Long idMataPelajaran;
    @NotEmpty(message = ConstantMessageMataPelajaran.ERROR_NAMA_MATA_PELAJARAN_IS_EMPTY)
    @NotNull(message = ConstantMessageMataPelajaran.ERROR_NAMA_MATA_PELAJARAN_IS_NULL)
    @NotBlank(message = ConstantMessageMataPelajaran.ERROR_NAMA_MATA_PELAJARAN_IS_BLANK)
    private String namaMataPelajaran;
    @NotEmpty(message = ConstantMessageMataPelajaran.ERROR_KODE_MATA_PELAJARAN_IS_EMPTY)
    @NotNull(message = ConstantMessageMataPelajaran.ERROR_KODE_MATA_PELAJARAN_IS_NULL)
    @NotBlank(message = ConstantMessageMataPelajaran.ERROR_KODE_MATA_PELAJARAN_IS_BLANK)
    @Length(max=3, message = ConstantMessageMataPelajaran.ERROR_KODE_MATA_PELAJARAN_LENGTH)
    private String kodeMataPelajaran;
    @NotEmpty(message = ConstantMessageMataPelajaran.ERROR_KODE_KELAS_IS_EMPTY)
    @NotNull(message = ConstantMessageMataPelajaran.ERROR_KODE_KELAS_IS_NULL)
    @NotBlank(message = ConstantMessageMataPelajaran.ERROR_KODE_KELAS_IS_BLANK)
    @Length(max=6, message = ConstantMessageMataPelajaran.ERROR_KODE_KELAS_LENGTH)
    private String kodeKelas;
    @NotNull(message = ConstantMessageMataPelajaran.ERROR_BAHASA_IS_NULL)
    private BahasaDTO bahasa;
    @NotNull(message = ConstantMessageMataPelajaran.ERROR_TUTOR_IS_NULL)
    private TutorDTO tutor;
    @JsonBackReference
    private List<SiswaDTO> listSiswaDTO;

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

    public BahasaDTO getBahasa() {
        return bahasa;
    }

    public void setBahasa(BahasaDTO bahasa) {
        this.bahasa = bahasa;
    }

    public TutorDTO getTutor() {
        return tutor;
    }

    public void setTutor(TutorDTO tutor) {
        this.tutor = tutor;
    }

    public List<SiswaDTO> getListSiswaDTO() {
        return listSiswaDTO;
    }

    public void setListSiswaDTO(List<SiswaDTO> listSiswaDTO) {
        this.listSiswaDTO = listSiswaDTO;
    }
}

