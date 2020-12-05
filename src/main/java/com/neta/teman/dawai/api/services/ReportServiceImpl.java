package com.neta.teman.dawai.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.models.converter.ReportConverter;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.response.UserResponse;
import com.neta.teman.dawai.api.models.repository.CutiSummaryRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Value("${application-report-dir}")
    String basePathReport;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    ReportConverter converter;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CutiSummaryRepository cutiSummaryRepository;

    @Autowired
    UserSpecs userSpecs;

    @Override
    public void initTemplate() {

    }

    @Override
    public void printCuti(User user, Cuti cuti, OutputStream outputStream) {
        try {
            Map<String, Object> map = new HashMap<>();
            Employee employee = user.getEmployee();
            map.put("NIP", employee.getNip());
            map.put("NIPL", "NIP. " + employee.getNip());
            map.put("NAMA", employee.getNama());
            map.put("JABATAN", employee.getJabatan());
            String unit = "";
            if (Objects.nonNull(employee.getUnits())) {
                for (EmployeeUnit u : employee.getUnits()) {
                    if (1 == u.getUnit()) {
                        unit = u.getNamaUnitUtama();
                        break;
                    }
                }
            }
            map.put("UNIT_KERJA", unit);
            // jenis
            map.put("JENIS_CUTI_1", 1 == cuti.getJenisCuti() ? "✓" : "");
            map.put("JENIS_CUTI_2", 2 == cuti.getJenisCuti() ? "✓" : "");
            map.put("JENIS_CUTI_3", 3 == cuti.getJenisCuti() ? "✓" : "");
            map.put("JENIS_CUTI_4", 4 == cuti.getJenisCuti() ? "✓" : "");
            map.put("JENIS_CUTI_5", 5 == cuti.getJenisCuti() ? "✓" : "");
            map.put("JENIS_CUTI_6", 6 == cuti.getJenisCuti() ? "✓" : "");
            map.put("KETERANGAN", cuti.getDescription());

            map.put("LAMA_CUTI", String.valueOf(cuti.getTotalDays()));
            map.put("START", DTFomat.format(cuti.getStartDate()));
            map.put("FINISH", DTFomat.format(cuti.getFinishDate()));
            LocalDate localDate = LocalDate.now();
            CutiSummary cutiSummary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());

            map.put("N", String.valueOf(cutiSummary.getKuotaCuti()));
            map.put("N-1", String.valueOf(cutiSummary.getKuotaPastCuti()));

            map.put("ALAMAT", cuti.getCutiAddress());
            map.put("TELP", cuti.getTlpAddress());
            if (Objects.nonNull(cuti.getAtasan())) {
                map.put("ATASAN_NIP", "NIP. " + cuti.getAtasan().getEmployee().getNip());
                map.put("ATASAN_NAMA", "NIP. " + cuti.getAtasan().getEmployee().getNama());
            } else {
                map.put("ATASAN_NIP", "NIP. ");
                map.put("ATASAN_NAMA", "");
            }

            if (Objects.nonNull(cuti.getPejabat())) {
                map.put("PEJABAT_NIP", cuti.getAtasan().getEmployee().getNip());
                map.put("PEJABAT_NAMA", cuti.getAtasan().getEmployee().getNama());
            } else {
                map.put("PEJABAT_NIP", "NIP. ");
                map.put("PEJABAT_NAMA", "");
            }
            //
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                sb.append("<parameter name=\"" + entry.getKey() + "\" class=\"java.lang.String\"/>");
            }
//            log.info("\n{}", sb.toString());
//            String url = "C:\\Users\\demOn\\JaspersoftWorkspace\\Teman Dawai\\report\\";
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(url + "form_cuti.jasper"));
            // JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "template" + File.separator + "form_cuti.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "form_cuti.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, map);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/cuti.pdf"));
//            if (Desktop.isDesktopSupported()) {
//                try {
//                    File myFile = new File("reports/export/cuti.pdf");
//                    Desktop.getDesktop().open(myFile);
//                } catch (IOException ex) {
//                    // no application registered for PDFs
//                }
//            }

            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void printCV(String nip, OutputStream outputStream) {
        try {
            User user = userRepository.findByUsername(nip);
            if (Objects.isNull(user)) return;
            UserResponse response = new UserResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource profileDataSource = new JRBeanCollectionDataSource(converter.profile(userMapOrigin, user));
            // sub report datasource
            JRBeanCollectionDataSource container = new JRBeanCollectionDataSource(converter.container());
            //JRBeanCollectionDataSource familyDataSource = new JRBeanCollectionDataSource(converter.family(user));
            JRBeanCollectionDataSource educationDataSource = new JRBeanCollectionDataSource(converter.education(user));
            JRBeanCollectionDataSource mutasiDataSource = new JRBeanCollectionDataSource(converter.mutasi(user));
            JRBeanCollectionDataSource pangkatDataSource = new JRBeanCollectionDataSource(converter.pangkat(user));
            // JRBeanCollectionDataSource jabatanDataSource = new JRBeanCollectionDataSource(converter.jabatan(user));
            JRBeanCollectionDataSource pelatihanJabatanDataSource = new JRBeanCollectionDataSource(converter.pelatihanJabatan(user));
            JRBeanCollectionDataSource pelatihanTeknisDataSource = new JRBeanCollectionDataSource(converter.pelatihanTeknis(user));
            JRBeanCollectionDataSource pendukungDataSource = new JRBeanCollectionDataSource(converter.pendukung(user));
            JRBeanCollectionDataSource skpDataSource = new JRBeanCollectionDataSource(converter.skp(user));
            JRBeanCollectionDataSource creditDataSource = new JRBeanCollectionDataSource(converter.credit(user));
            JRBeanCollectionDataSource lencanaDataSource = new JRBeanCollectionDataSource(converter.lancana(user));
            JRBeanCollectionDataSource cutiDataSource = new JRBeanCollectionDataSource(converter.cuti(cutiSummaryRepository.findByUser(user)));
            JRBeanCollectionDataSource hukumanDisiplinDataSource = new JRBeanCollectionDataSource(converter.disiplin(user));
            JRBeanCollectionDataSource prediksiPensiunDataSource = new JRBeanCollectionDataSource(converter.pensiun(user));

            userMapOrigin.put("SUB_DIR", basePathReport);
            userMapOrigin.put("CONTAINER_DATASOURCE", container);
            userMapOrigin.put("EDUCATION_DATASOURCE", educationDataSource);
            userMapOrigin.put("MUTASI_DATASOURCE", mutasiDataSource);
            userMapOrigin.put("PANGKAT_DATASOURCE", pangkatDataSource);
            userMapOrigin.put("PENDUKUNG_DATASOURCE", pendukungDataSource);
            userMapOrigin.put("PELATIHAN_JABATAN_DATASOURCE", pelatihanJabatanDataSource);
            userMapOrigin.put("PELATIHAN_TEKNIS_DATASOURCE", pelatihanTeknisDataSource);
            userMapOrigin.put("SKP_DATASOURCE", skpDataSource);
            userMapOrigin.put("CREDIT_DATASOURCE", creditDataSource);
            userMapOrigin.put("LENCANA_DATASOURCE", lencanaDataSource);
            userMapOrigin.put("CUTI_DATASOURCE", cutiDataSource);
            userMapOrigin.put("HUKUMAN_DATASOURCE", hukumanDisiplinDataSource);
            userMapOrigin.put("PREDIKSI_PENSIUN_DATASOURCE", prediksiPensiunDataSource);


//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "template" + File.separator + "pegawai_cv.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pegawai_cv.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, profileDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/cv.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void printDUK(List<Employee> employees, OutputStream outputStream) {
        try {
            Map<String, Object> userMapOrigin = new HashMap<>();//new ObjectMapper().convertValue(employees, Map.class);
            JRBeanCollectionDataSource employeeDataSource = new JRBeanCollectionDataSource(converter.duk(employees));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "duk.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, employeeDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/duk.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);
            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBlanko1(User param, OutputStream outputStream) {
        try {
            User user = userRepository.findByUsername(param.getUsername());
            if (Objects.isNull(user)) return;
            UserResponse response = new UserResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(converter.defaultDatasource(userMapOrigin, user));
            userMapOrigin.put("SUB_DIR", basePathReport);
            Employee employee = user.getEmployee();
            userMapOrigin.put("NAMA", ": " + employee.getNama());
            userMapOrigin.put("NIP", ": " + employee.getNip());
            userMapOrigin.put("KARPEG", ": " + employee.getNoKarpeg());
            userMapOrigin.put("PANGKAT_GOLONGAN", ": " + employee.getPangkat() + ", " + employee.getGol());
            userMapOrigin.put("UNIT", ": ");
            userMapOrigin.put("ALAMAT", ": " + employee.getAlamat());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pensiun_doc_blanko1.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, mainDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/pensiun_blanko_1.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBlanko2(User param, OutputStream outputStream) {
        try {
            User user = userRepository.findByUsername(param.getUsername());
            if (Objects.isNull(user)) return;
            UserResponse response = new UserResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(converter.defaultDatasource(userMapOrigin, user));
            userMapOrigin.put("SUB_DIR", basePathReport);
            Employee employee = user.getEmployee();
            userMapOrigin.put("NAMATTD", employee.getNama());
            userMapOrigin.put("NAMA", ": " + employee.getNama());
            userMapOrigin.put("NIPTTD", employee.getNip());
            userMapOrigin.put("NIP", ": " + employee.getNip());
            userMapOrigin.put("KARPEG", " / " + employee.getNoKarpeg());
            userMapOrigin.put("PANGKAT_GOLONGAN", ": " + employee.getPangkat() + ", " + employee.getGol());
            userMapOrigin.put("UNIT", ": ");
            userMapOrigin.put("ALAMAT", ": " + employee.getAlamat());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pensiun_doc_blanko2.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, mainDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/pensiun_blanko_2.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBlanko3(User param, OutputStream outputStream) {
        try {
            User user = userRepository.findByUsername(param.getUsername());
            if (Objects.isNull(user)) return;
            UserResponse response = new UserResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(converter.blanko3ProfileFamilyAnak(userMapOrigin, user));
            userMapOrigin.put("SUB_DIR", basePathReport);
            Employee employee = user.getEmployee();
            userMapOrigin.put("NAMATTD", employee.getNama());
            userMapOrigin.put("NAMA", ": " + employee.getNama());
            userMapOrigin.put("NIPTTD", employee.getNip());
            userMapOrigin.put("NIP", ": " + employee.getNip());
            userMapOrigin.put("KARPEG", ": " + employee.getNoKarpeg());
            userMapOrigin.put("PANGKAT_GOLONGAN", ": " + employee.getPangkat() + ", " + employee.getGol());
            userMapOrigin.put("UNIT", ": ");
            userMapOrigin.put("ALAMAT", ": " + employee.getAlamat());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pensiun_doc_blanko3.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, mainDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/pensiun_blanko_3.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBlanko4(User param, OutputStream outputStream) {
        try {
            User user = userRepository.findByUsername(param.getUsername());
            if (Objects.isNull(user)) return;
            UserResponse response = new UserResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(converter.blanko3ProfileFamilyAnak(userMapOrigin, user));
            userMapOrigin.put("SUB_DIR", basePathReport);
            Employee employee = user.getEmployee();
            userMapOrigin.put("NAMATTD", employee.getNama());
            userMapOrigin.put("NAMA", ": " + employee.getNama());
            userMapOrigin.put("NIPTTD", employee.getNip());
            userMapOrigin.put("NIP", ": " + employee.getNip());
            userMapOrigin.put("KARPEG", ": " + employee.getNoKarpeg());
            userMapOrigin.put("PANGKAT_GOLONGAN", ": " + employee.getPangkat() + ", " + employee.getGol());
            userMapOrigin.put("UNIT", ": ");
            userMapOrigin.put("ALAMAT", ": " + employee.getAlamat());
            userMapOrigin.put("JABATAN", ": " + employee.getJabatan());

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pensiun_doc_blanko4.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, mainDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/pensiun_blanko_4.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printBlanko5(User param, OutputStream outputStream) {
        try {
            User user = userRepository.findByUsername(param.getUsername());
            if (Objects.isNull(user)) return;
            UserResponse response = new UserResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(converter.defaultDatasource(userMapOrigin, user));
            JRBeanCollectionDataSource profileDataSource = new JRBeanCollectionDataSource(converter.blanko5Profile(userMapOrigin, user));
            JRBeanCollectionDataSource profileFamilyDataSource = new JRBeanCollectionDataSource(converter.blanko5ProfileFamilyPasangan(userMapOrigin, user));
            JRBeanCollectionDataSource profileFamilyAnakDataSource = new JRBeanCollectionDataSource(converter.blanko5ProfileFamilyAnak(userMapOrigin, user));
            // sub report datasource
            JRBeanCollectionDataSource container = new JRBeanCollectionDataSource(converter.container());
            JRBeanCollectionDataSource mutasiDataSource = new JRBeanCollectionDataSource(converter.mutasi(user));
            JRBeanCollectionDataSource pangkatDataSource = new JRBeanCollectionDataSource(converter.pangkat(user));
            JRBeanCollectionDataSource pelatihanJabatanDataSource = new JRBeanCollectionDataSource(converter.pelatihanJabatan(user));
            JRBeanCollectionDataSource pelatihanTeknisDataSource = new JRBeanCollectionDataSource(converter.pelatihanTeknis(user));
            JRBeanCollectionDataSource pendukungDataSource = new JRBeanCollectionDataSource(converter.pendukung(user));
            JRBeanCollectionDataSource skpDataSource = new JRBeanCollectionDataSource(converter.skp(user));
            JRBeanCollectionDataSource creditDataSource = new JRBeanCollectionDataSource(converter.credit(user));
            JRBeanCollectionDataSource lencanaDataSource = new JRBeanCollectionDataSource(converter.lancana(user));
            JRBeanCollectionDataSource cutiDataSource = new JRBeanCollectionDataSource(converter.cuti(cutiSummaryRepository.findByUser(user)));
            JRBeanCollectionDataSource hukumanDisiplinDataSource = new JRBeanCollectionDataSource(converter.disiplin(user));
            JRBeanCollectionDataSource prediksiPensiunDataSource = new JRBeanCollectionDataSource(converter.pensiun(user));

            userMapOrigin.put("SUB_DIR", basePathReport);
            userMapOrigin.put("CONTAINER_DATASOURCE", container);
            userMapOrigin.put("PROFILE_DATASOURCE", profileDataSource);
            userMapOrigin.put("FAMILY_PASANGAN_DATASOURCE", profileFamilyDataSource);
            userMapOrigin.put("FAMILY_ANAK_DATASOURCE", profileFamilyAnakDataSource);
            userMapOrigin.put("MUTASI_DATASOURCE", mutasiDataSource);
            userMapOrigin.put("PANGKAT_DATASOURCE", pangkatDataSource);
            userMapOrigin.put("PENDUKUNG_DATASOURCE", pendukungDataSource);
            userMapOrigin.put("PELATIHAN_JABATAN_DATASOURCE", pelatihanJabatanDataSource);
            userMapOrigin.put("PELATIHAN_TEKNIS_DATASOURCE", pelatihanTeknisDataSource);
            userMapOrigin.put("SKP_DATASOURCE", skpDataSource);
            userMapOrigin.put("CREDIT_DATASOURCE", creditDataSource);
            userMapOrigin.put("LENCANA_DATASOURCE", lencanaDataSource);
            userMapOrigin.put("CUTI_DATASOURCE", cutiDataSource);
            userMapOrigin.put("HUKUMAN_DATASOURCE", hukumanDisiplinDataSource);
            userMapOrigin.put("PREDIKSI_PENSIUN_DATASOURCE", prediksiPensiunDataSource);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pensiun_doc_blanko5.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, mainDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/pensiun_blanko_5.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printNaikPangkat(int tahun, int bulan, OutputStream outputStream) {
        try {
            Map<String, Object> userMapOrigin = new HashMap<>();
            List<User> users = userRepository.findAll(userSpecs.pageNaikPangkat("", tahun, bulan));
            JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(converter.naikPangkat(users));
            JRBeanCollectionDataSource lampiranDataSource = new JRBeanCollectionDataSource(converter.naikPangkat(users));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pegawai_naik_pangkat.jasper"));
            userMapOrigin.put("SUB_DIR", basePathReport);
            userMapOrigin.put("LAMPIRAN_DATASOURCE", lampiranDataSource);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, mainDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/pegawai_naik_pangkat.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void printPensiunAjuan(User user, OutputStream outputStream) {
        try {
            Map<String, Object> userMapOrigin = new HashMap<>();
            List<User> users = userRepository.findAll();
            JRBeanCollectionDataSource mainDataSource = new JRBeanCollectionDataSource(converter.naikPangkat(users));
            JRBeanCollectionDataSource lampiranDataSource = new JRBeanCollectionDataSource(converter.naikPangkat(users));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pensiun_doc_usulan.jasper"));
            userMapOrigin.put("SUB_DIR", basePathReport);
            userMapOrigin.put("LAMPIRAN_DATASOURCE", lampiranDataSource);
            Employee employee = user.getEmployee();
            userMapOrigin.put("NAMATTD", employee.getNama());
            userMapOrigin.put("NAMA", employee.getNama());
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, mainDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/pensiun_doc_usulan.pdf"));
            SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
            reportConfig.setSizePageToContent(true);
            reportConfig.setForceLineBreakPolicy(false);

            SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
            exportConfig.setMetadataAuthor("Teman Dawai");
            exportConfig.setEncrypted(true);
            exportConfig.setAllowedPermissionsHint("PRINTING");
            exporter.setConfiguration(reportConfig);
            exporter.setConfiguration(exportConfig);
            if (Objects.nonNull(outputStream)) {
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            }
            exporter.exportReport();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
