package com.neta.teman.dawai.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.models.converter.ReportConverter;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.response.UserResponse;
import com.neta.teman.dawai.api.models.repository.CutiSummaryRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
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
    UserRepository userRepository;

    @Autowired
    CutiSummaryRepository cutiSummaryRepository;

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

            CutiSummary cutiSummary = cutiSummaryRepository.findByUser(user);

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
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "template" + File.separator + "form_cuti.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, map);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));

//            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/cuti.pdf"));
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
            JRBeanCollectionDataSource profileDataSource = new JRBeanCollectionDataSource(ReportConverter.profile(userMapOrigin, user));
            // sub report datasource
            JRBeanCollectionDataSource container = new JRBeanCollectionDataSource(ReportConverter.container());
            JRBeanCollectionDataSource familyDataSource = new JRBeanCollectionDataSource(ReportConverter.family(user));
            JRBeanCollectionDataSource educationDataSource = new JRBeanCollectionDataSource(ReportConverter.education(user));
            JRBeanCollectionDataSource mutasiDataSource = new JRBeanCollectionDataSource(ReportConverter.mutasi(user));
            JRBeanCollectionDataSource pendukungDataSource = new JRBeanCollectionDataSource(ReportConverter.pendukung(user));

            userMapOrigin.put("SUB_DIR", basePathReport + File.separator + "template" + File.separator);
            userMapOrigin.put("SUB_DIR", basePathReport + File.separator);
            userMapOrigin.put("CONTAINER_DATASOURCE", container);
            userMapOrigin.put("FAMILY_DATASOURCE", familyDataSource);
            userMapOrigin.put("EDUCATION_DATASOURCE", educationDataSource);
            userMapOrigin.put("MUTASI_DATASOURCE", mutasiDataSource);
            userMapOrigin.put("PENDUKUNG_DATASOURCE", pendukungDataSource);

//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "template" + File.separator + "pegawai_cv.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "pegawai_cv.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, profileDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/cv.pdf"));
//            if (Desktop.isDesktopSupported()) {
//                try {
//                    File myFile = new File("reports/export/cv.pdf");
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
    public void printDUK(List<Employee> employees, OutputStream outputStream) {
        try {
            Map<String, Object> userMapOrigin = new HashMap<>();//new ObjectMapper().convertValue(employees, Map.class);
            JRBeanCollectionDataSource employeeDataSource = new JRBeanCollectionDataSource(ReportConverter.duk(employees));
//            String path = "C:\\Users\\Lenovo\\JaspersoftWorkspace\\MyReports\\duk.jasper";
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(basePathReport + File.separator + "template" + File.separator + "duk.jasper"));
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(path));
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
}
