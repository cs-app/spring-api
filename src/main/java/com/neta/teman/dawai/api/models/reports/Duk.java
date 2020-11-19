package com.neta.teman.dawai.api.models.reports;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Duk {

    String nip;
    String nama;
    String kelamin;
    String pangkatDetailGol;
    String tmtGol;
    String jabatan;
    String mulaiPns;
    String masaKerja;
    String pendidikan;
    String pendidikanInstansi;
    String pendidikanJurusan;
    String tempatLahir;
    String tanggalLahir;
    String usia;
    String mutasi;
    String agama;
    String pangkat;
    String golongan;
    String tmtJabatan;
    String tmtCpns;
    String masaWaktuDiPangkat;
    String masaKerjaTahunDanBulan;
    String pelatihanJabatan;
    String pelatihanTeknis;
    String pensiunTMT;
    String pensiunTahun;

}
