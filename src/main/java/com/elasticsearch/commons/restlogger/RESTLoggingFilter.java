package com.elasticsearch.commons.restlogger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.elasticsearch.commons.restlogger.OperationLog.OperationLogType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RESTLoggingFilter implements javax.servlet.Filter {
    private static final String NO_TRACE_BODY_MESSAGE = "NOT_SAVED";
    private static final String SEPARATOR = "--------------------------------------";

    private List<String> denyUrlsList;
    private boolean enabled;

    public RESTLoggingFilter(List<String> denyUrlsList) {
        this.denyUrlsList = denyUrlsList;
        this.enabled = true;
    }

    public RESTLoggingFilter(List<String> denyUrlsList, boolean enabled) {
        this.denyUrlsList = denyUrlsList;
        this.enabled = enabled;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (this.enabled && ((HttpServletRequest) req).getRequestURI() != null
                && denyUrlsList.stream().noneMatch(p -> ((HttpServletRequest) req).getRequestURI().contains(p))) {
            HttpServletRequestCopier requestCopier = new HttpServletRequestCopier((HttpServletRequest) req);
            HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) res);
            LocalDateTime requestTime = LocalDateTime.now();
            try {
                chain.doFilter(requestCopier, responseCopier);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw e;
            } finally {
                logRequest(requestCopier, requestTime);
                logResponse(responseCopier, ((HttpServletRequest) req).getRequestURI(), requestTime);
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    private void logRequest(HttpServletRequestCopier servletRequest, LocalDateTime requestTime) {
        byte[] requestCopy = servletRequest.getCopy();
        String message = "REST REQUEST" + System.lineSeparator() + SEPARATOR + System.lineSeparator() + "Address: "
                + servletRequest.getRequestURI() + System.lineSeparator() + "Encoding: " + servletRequest.getCharacterEncoding()
                + System.lineSeparator() + "Http-Method: " + servletRequest.getMethod() + System.lineSeparator() + "Content-Type: "
                + servletRequest.getContentType() + System.lineSeparator() + "Request-Time: " + requestTime + System.lineSeparator()
                + "Request-Params: " + servletRequest.getQueryString() + System.lineSeparator() + "Request-Body: " + new String(requestCopy)
                + System.lineSeparator() + SEPARATOR;
        OperationLog.log(log, message, servletRequest.getRequestURI(), OperationLogType.REQUEST);

    }

    private void logResponse(HttpServletResponseCopier servletResponse, String requestUri, LocalDateTime requestTime) throws IOException {
        final int status = servletResponse.getStatus();
        String body;
        if (status >= 400) {
            body = new String(servletResponse.getCopy());
        } else {
            body = NO_TRACE_BODY_MESSAGE;
        }

        servletResponse.flushBuffer();
        LocalDateTime responseTime = LocalDateTime.now();
        final long executionTime = ChronoUnit.MILLIS.between(requestTime, responseTime);
        String message = "REST RESPONSE" + System.lineSeparator() + SEPARATOR + System.lineSeparator() + "Address: " + requestUri
                + System.lineSeparator() + "Response-Code: " + status + System.lineSeparator() + "Encoding: "
                + servletResponse.getCharacterEncoding() + System.lineSeparator() + "Content-Type: " + servletResponse.getContentType()
                + System.lineSeparator() + "Request-Time: " + requestTime + System.lineSeparator() + "Response-Time: " + responseTime
                + System.lineSeparator() + "Execution-Time: " + executionTime + System.lineSeparator() + "Response-Body: " + body
                + System.lineSeparator() + SEPARATOR;
        OperationLog.log(log, message, requestUri, OperationLogType.RESPONSE, status, executionTime);
    }

}