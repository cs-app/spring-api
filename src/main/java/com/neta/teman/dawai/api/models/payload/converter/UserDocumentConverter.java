package com.neta.teman.dawai.api.models.payload.converter;

import com.neta.teman.dawai.api.applications.commons.ResponseConverter;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.EmployeeDocument;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.response.CutiResponse;
import com.neta.teman.dawai.api.models.payload.response.UserDocumentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDocumentConverter implements ResponseConverter<List<UserDocumentResponse>, List<User>> {
    @Override
    public List<UserDocumentResponse> convert(List<User> param) {
        List<UserDocumentResponse> result = new ArrayList<>();
        for (User user : param) {
            Employee employee = user.getEmployee();
            if (Objects.isNull(employee)) continue;
            List<EmployeeDocument> employeeDocuments = employee.getDocuments();
            if (Objects.isNull(employeeDocuments)) continue;
            for (EmployeeDocument ed : employeeDocuments) {
                if (ed.getApproval() == AppConstants.Uploads.pending) {
                    result.add(new UserDocumentResponse(employee, ed));
                }
            }
        }
        return result;
    }
}
