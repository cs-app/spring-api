package com.neta.teman.dawai.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.converter.ReportConverter;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.response.LoginResponse;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    UserRepository userRepository;

    @Override
    public void initTemplate() {
        File folders = new File(AppConstants.reportTemplate);
        if (!folders.exists()) {
            if (folders.mkdirs()) {
                log.info("report generate folder template");
            } else {
                log.info("report generate folder template already exist");
            }
        }
        File foldersExport = new File(AppConstants.reportExport);
        if (!foldersExport.exists()) {
            if (foldersExport.mkdirs()) {
                log.info("report generate folder export");
            } else {
                log.info("report generate folder export already exist");
            }
        }
        String[] fileName = {
                "form_cuti",
                "pegawai_cv",
                "pegawai_cv_container",
                "pegawai_cv_keluarga",
                "pegawai_cv_mutasi",
                "pegawai_cv_pendukung",
                "pegawai_cv_pendidikan"
        };
//        String baseProject = "C:\\Users\\demOn\\JaspersoftWorkspace\\Teman Dawai\\report\\";
        for (String s : fileName) {
            try {
                InputStream employeeReportStream = resourceLoader.getResource("classpath:reports/" + s + ".jrxml").getInputStream();
//                InputStream employeeReportStream = new FileInputStream(new File(baseProject + s + ".jrxml")); // test only
                JasperReport jasperReport = JasperCompileManager.compileReport(employeeReportStream);
                JRSaver.saveObject(jasperReport, AppConstants.reportTemplate + s + ".jasper");
            } catch (JRException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void printCuti(User userx, Cuti cutix, OutputStream outputStream) {
        try {
            /*
            User user = userRepository.findByUsername(nip);
            if (Objects.isNull(user)) return;
            LoginResponse response = new LoginResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource profileDataSource = new JRBeanCollectionDataSource(ReportConverter.profile(userMapOrigin, user));
            // sub report datasource
            JRBeanCollectionDataSource container = new JRBeanCollectionDataSource(ReportConverter.container());
            JRBeanCollectionDataSource familyDataSource = new JRBeanCollectionDataSource(ReportConverter.family(user));
            JRBeanCollectionDataSource educationDataSource = new JRBeanCollectionDataSource(ReportConverter.education(user));
            JRBeanCollectionDataSource mutasiDataSource = new JRBeanCollectionDataSource(ReportConverter.mutasi(user));
            JRBeanCollectionDataSource pendukungDataSource = new JRBeanCollectionDataSource(ReportConverter.pendukung(user));

//            userMapOrigin.put("SUB_DIR", "C:\\Users\\demOn\\Documents\\Workspace\\Zuliadin\\teman-dawai-api\\reports\\template\\");
            userMapOrigin.put("SUB_DIR", AppConstants.reportTemplate);
            userMapOrigin.put("CONTAINER_DATASOURCE", container);
            userMapOrigin.put("FAMILY_DATASOURCE", familyDataSource);
            userMapOrigin.put("EDUCATION_DATASOURCE", educationDataSource);
            userMapOrigin.put("MUTASI_DATASOURCE", mutasiDataSource);
            userMapOrigin.put("PENDUKUNG_DATASOURCE", pendukungDataSource);
             */
            Map<String, Object> userMapOrigin = new HashMap<>();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(AppConstants.reportTemplate + "form_cuti.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/cuti.pdf"));
            if (Desktop.isDesktopSupported()) {
                try {
                    File myFile = new File("reports/export/cuti.pdf");
                    Desktop.getDesktop().open(myFile);
                } catch (IOException ex) {
                    // no application registered for PDFs
                }
            }

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
            LoginResponse response = new LoginResponse(user);
            Map<String, Object> userMapOrigin = new ObjectMapper().convertValue(response, Map.class);
            JRBeanCollectionDataSource profileDataSource = new JRBeanCollectionDataSource(ReportConverter.profile(userMapOrigin, user));
            // sub report datasource
            JRBeanCollectionDataSource container = new JRBeanCollectionDataSource(ReportConverter.container());
            JRBeanCollectionDataSource familyDataSource = new JRBeanCollectionDataSource(ReportConverter.family(user));
            JRBeanCollectionDataSource educationDataSource = new JRBeanCollectionDataSource(ReportConverter.education(user));
            JRBeanCollectionDataSource mutasiDataSource = new JRBeanCollectionDataSource(ReportConverter.mutasi(user));
            JRBeanCollectionDataSource pendukungDataSource = new JRBeanCollectionDataSource(ReportConverter.pendukung(user));

//            userMapOrigin.put("SUB_DIR", "C:\\Users\\demOn\\Documents\\Workspace\\Zuliadin\\teman-dawai-api\\reports\\template\\");
            userMapOrigin.put("SUB_DIR", AppConstants.reportTemplate);
            userMapOrigin.put("CONTAINER_DATASOURCE", container);
            userMapOrigin.put("FAMILY_DATASOURCE", familyDataSource);
            userMapOrigin.put("EDUCATION_DATASOURCE", educationDataSource);
            userMapOrigin.put("MUTASI_DATASOURCE", mutasiDataSource);
            userMapOrigin.put("PENDUKUNG_DATASOURCE", pendukungDataSource);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(AppConstants.reportTemplate + "pegawai_cv.jasper"));
            JasperPrint print = JasperFillManager.fillReport(jasperReport, userMapOrigin, profileDataSource);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reports/export/cv.pdf"));
            if (Desktop.isDesktopSupported()) {
                try {
                    File myFile = new File("reports/export/cv.pdf");
                    Desktop.getDesktop().open(myFile);
                } catch (IOException ex) {
                    // no application registered for PDFs
                }
            }

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
