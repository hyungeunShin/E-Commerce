package com.example.userservice.config.openfeign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    private static final String ERRORS_PATH = "error";
    private static final String ERROR_MESSAGE_PATH = "trace";

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = getResponseMessage(response);
        log.info("errorMessage: {}", errorMessage);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(errorMessage);

            if(response.status() == 400) {
                JsonNode errorsNode = jsonNode.findValue(ERRORS_PATH);
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, errorsNode.toString());
            }

            if(response.status() == 404) {
                JsonNode errorsNode = jsonNode.findValue(ERRORS_PATH);
                return new ResponseStatusException(HttpStatus.NOT_FOUND, errorsNode.toString());
            }

            JsonNode errorMessageNode = jsonNode.findValue(ERROR_MESSAGE_PATH);
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessageNode.toString());
        } catch(JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getResponseMessage(Response response) {
        byte[] body = {};
        try {
            if(response.body() != null) {
                body = Util.toByteArray(response.body().asInputStream());
            }
        } catch(IOException e) {
            log.error(e.getMessage());
        }
        return new String(body);
    }
}