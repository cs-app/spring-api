package com.neta.teman.dawai.api.services;

import java.io.OutputStream;

public interface ReportService {

    void initTemplate();

    void printCV(String nip, OutputStream outputStream);

}
