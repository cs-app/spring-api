package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.User;

import java.io.OutputStream;

public interface ReportService {

    void initTemplate();

    void printCV(String nip, OutputStream outputStream);

    void printCuti(User user, Cuti cuti, OutputStream outputStream);

}
