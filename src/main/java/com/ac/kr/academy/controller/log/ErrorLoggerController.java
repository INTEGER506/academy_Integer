package com.ac.kr.academy.controller.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/error")
public class ErrorLoggerController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorLoggerController.class);

    @RequestMapping
    public String handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpServletResponse.SC_NOT_FOUND) {
                logger.warn("Client Error: Status 404 Not Found - {}", request.getRequestURI());
            } else if (statusCode >= 500) {
                logger.error("Server Error: Status " + statusCode + " - " + request.getRequestURI());
            }
        }
        return "redirect:/auth/login";
    }
}
