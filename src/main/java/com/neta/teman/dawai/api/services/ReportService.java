package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.User;
import net.sf.jasperreports.engine.JRException;

import java.io.OutputStream;
import java.util.List;

public interface ReportService {

    void initTemplate();

    void printCV(String nip, OutputStream outputStream);

    void printCuti(User user, Cuti cuti, OutputStream outputStream);

    void printDUK(List<Employee> employees, OutputStream outputStream);

    void printBlanko1(User employees, OutputStream o);

    void printBlanko2(User employees, OutputStream o);

    void printBlanko3(User employees, OutputStream o);

    void printBlanko4(User employees, OutputStream o);

    void printBlanko5(User employees, OutputStream o);

    void printNaikPangkat(int tahun, int bulan, OutputStream o);

    void printPensiunAjuan(User user, OutputStream o);
}
