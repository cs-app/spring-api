package com.neta.spring.api.applications.base;

import com.neta.spring.api.applications.commons.ResponseConverter;
import com.neta.spring.api.applications.commons.ValueValidation;
import com.neta.spring.api.repositories.payload.response.PageResponse;
import com.neta.spring.api.repositories.payload.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletContext;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class BaseRestController {

    @Autowired
    protected ServletContext servletContext;
    protected String SUCCESS = "success";
    protected String ERROR_MANDATORY_FIELD = "mandatory field not complete";

    protected boolean isNull(Object... values) {
        return ValueValidation.isNull(values);
    }

    protected ResponseEntity response() {
        Response response = new Response();
        response.setCode(200);
        response.setMessage(SUCCESS);
        return ResponseEntity.ok(response);
    }

    protected ResponseEntity responseMessage(String message) {
        Response response = new Response();
        response.setCode(200);
        response.setMessage(message);
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity response(ServiceResolver<T> resolver) {
        if (resolver.isError()) return responseError(resolver);
        return response(resolver.getResult());
    }

    protected <T, E extends ResponseConverter> ResponseEntity response(ServiceResolver<T> resolver, Class<E> converterClass) {
        if (resolver.isError()) return responseError(resolver);
        return response(BeanUtils.instantiateClass(converterClass).convert(resolver.getResult()));
    }

    protected <T> ResponseEntity response(T value) {
        Response<T> response = new Response<>();
        response.setCode(200);
        response.setMessage(SUCCESS);
        response.setResult(value);
        return ResponseEntity.ok(response);
    }

    protected <T extends Page> ResponseEntity responsePage(ServiceResolver<T> resolver) {
        Response<PageResponse> response = new Response<>();
        response.setCode(200);
        response.setMessage(SUCCESS);
        response.setResult(new PageResponse(resolver.getResult()));
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity responseError(ServiceResolver<T> resolver) {
        return responseError(resolver.getCode(), resolver.getMessage());
    }

    protected <T> ResponseEntity responseError(int code, String message) {
        Response<T> response = new Response<>();
        if (code == 200) { //throw new Exception("invalid response code 200 for negative case");
            code = 1000;
        }
        if ("success".equalsIgnoreCase(message)) { // throw new Exception("invalid response message \"success\" for negative case");
            message = "error";
        }
        response.setCode(code);
        response.setMessage(message);
        return ResponseEntity.ok(response);
    }
}
