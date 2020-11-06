package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.models.converter.JsonDateConverter;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * "id": 10453,
 * "username": "kincat4",
 * "NIP": null,
 * "name": "kincat 4",
 * "email": null,
 * "role": "ADMIN",
 * "phone": "11111",
 * "photo": "users/default.png",
 * "deleted": 0,
 * "remember_token": "",
 * "created_at": "2020-07-25 11:25:41",
 * "updated_at": "2020-10-25 22:38:14",
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegAuth {

    List<String> photos;

    Integer unreadMessage;

    Integer unreadNotification;

    String token;

    // delimiter deprecated

    Personal personal;

    Employee employee;

    Keluarga keluarga;

    Integer deleted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedAt;

    /**
     * "date": "2020-10-25 22:38:14.894520",
     * "timezone_type": 3,
     * "timezone": "UTC"
     */
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class LastLogin {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        Date date;
        Integer timezoneType;
        String timezone;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Personal {

        Long id;

        @JsonProperty("NIP")
        String nip;

        String nama;

        String presensiId;

        String photo;

        String tempatLahir;

        @JsonDeserialize(converter = JsonDateConverter.class)
        Date tanggalLahir;

        String usia;

        String kelamin;

        String statusDiri;

        String agama;

        String alamat;

        String kelurahan;

        String kelurahanData;

        String kecamatan;

        String kecamatanData;

        String kebupaten;

        String kebupatenData;

        String provinsi;

        String provinsiData;

        @JsonProperty("rt_rw")
        String rtRW;

        String kodePos;

        PersonalPendidikan pendidikan;

        PersonalLainya lainnya;

        @Data
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        public static class PersonalPendidikan {

            String jenjang;
            String jenjangStudi;
            String bidangStudi;
            String namaSekolah;
            String thnLulus;
            PersonalPendidikanJenjangData jenjangData;
            PersonalPendidikanStudiData bidangStudiData;

            @Data
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            public static class PersonalPendidikanJenjangData {
                Integer kodePend;
                String namaPend;
                Integer pendLevelId;
                Integer gelarPend;
            }

            @Data
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            public static class PersonalPendidikanStudiData {
                Integer kodeStudi;
                Integer kodePend;
                Integer kodeRumpun;
                String namaStudi;
                String gelar;
            }
        }

        @Data
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        public static class PersonalLainya {

            @JsonProperty("NPWP")
            String npwp;
            String noTaspen;
            String hpNumber;
            String emailAddress;

        }

    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Employee {
        String statusPeg;
        String jenisPeg;
        String mks;
        String mkg;
        String noKarpeg;
        String noKarisSu;
        @JsonProperty("PMK")
        String pmk;
        String statusAktif;
        String perkiraanPensiun;
        EmployeeCPNS cpns;
        EmployeePNS pns;
        EmployeeJabatanEselon jabatanEselon;

        @Data
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        public static class EmployeeCPNS {

            @JsonProperty("tgl_mulai_CPNS")
            @JsonDeserialize(converter = JsonDateConverter.class)
            Date tglMulaiCPNS;
            @JsonProperty("no_sk_CPNS")
            String noSKCPNS;
            @JsonDeserialize(converter = JsonDateConverter.class)
            @JsonProperty("tgl_sk_CPNS")
            Date tglSKCPNS;
        }

        @Data
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        public static class EmployeePNS {

            @JsonProperty("tgl_mulai_PNS")
            @JsonDeserialize(converter = JsonDateConverter.class)
            Date tglMulaiPNS;
            @JsonProperty("no_sk_PNS")
            String noSKPNS;
            @JsonProperty("tgl_sk_PNS")
            @JsonDeserialize(converter = JsonDateConverter.class)
            Date tglSKPNS;
        }

        @Data
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        public static class EmployeeJabatanEselon {

            String jabatan;
            String eselon;
            @JsonDeserialize(converter = JsonDateConverter.class)
            Date tmtJabatan;
            String noSkJabatan;
            @JsonDeserialize(converter = JsonDateConverter.class)
            Date tglSkJabatan;
            EmployeeJabatanData jabatanData;
            EmployeeJabatanEselonData eselonData;

            @Data
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            public static class EmployeeJabatanData {
                Integer jabatanId;
                String namaJabatan;
                String singkatan;
                String kategori;
                String subkategori;
                Integer tingkatan;
                String eselonIdAwal;
                String eselonIdAkhir;
                Integer kelasJabatan;
                Integer nilaiJabatan;
                Integer unitUtamaId;
                Integer kelJabatanId;
                String refId;
                String pensiun;
            }

            @Data
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            public static class EmployeeJabatanEselonData {
                String eselonId;
                String eselon;
                Integer tingkatan;
                String keterangan;
                String golAwal;
                String golAkhir;
            }
        }

        @Data
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        public static class EmployeeGolonganPangkat {

            String gol;
            String pangkat;
            Date tmtGol;
            String noSkGol;
            Date tglSkGol;
            EmployeeGolonganPangkatGolData golData;
            EmployeeGolonganPangkatData pangkatData;

            @Data
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            public static class EmployeeGolonganPangkatGolData {
                String gol;
                Integer tingkatan;
                String keterangan;
            }

            @Data
            @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
            public static class EmployeeGolonganPangkatData {
                Integer pangkatId;
                String gol;
                String pangkat;
                String refId;
            }
        }
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class JabatanEselon {
        String eselon;
        String jabatan;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Keluarga {

        List<KeluargaDetail> pasangan;
        List<KeluargaDetail> anak;
        List<KeluargaDetail> orangTua;
        List<KeluargaDetail> mertua;
        List<KeluargaDetail> saudara;

        @Data
        @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
        public static class KeluargaDetail {
            String hubungan;
            String nama;
            String kelamin;
            String tempatLahir;
            String pekerjaan;
            String keterangan;
            @JsonDeserialize(converter = JsonDateConverter.class)
            Date tanggalLahir;
            @JsonDeserialize(converter = JsonDateConverter.class)
            Date tanggalNikah;
        }
    }

}
