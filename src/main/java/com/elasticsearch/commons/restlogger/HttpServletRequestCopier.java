package com.elasticsearch.commons.restlogger;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HttpServletRequestCopier extends HttpServletRequestWrapper {

    private ServletInputStream inputStream;
    private BufferedReader reader;
    private ServletInputStreamCopier copier;
    private ServletReaderCopier readerCopier;

    public HttpServletRequestCopier(HttpServletRequest request) {
        super(request);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (this.inputStream != null) {
            throw new IllegalStateException("getInputStream() has already been called on this response.");
        }
        if (this.reader == null) {
            this.readerCopier = new ServletReaderCopier(getRequest().getReader());
            this.reader = new BufferedReader(this.readerCopier);
        }
        return this.reader;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.reader != null) {
            throw new IllegalStateException("getReader() has already been called on this response.");
        }
        if (this.inputStream == null) {
            this.inputStream = getRequest().getInputStream();
            this.copier = new ServletInputStreamCopier(this.inputStream);
        }
        return this.copier;
    }

    public byte[] getCopy() {
        if (this.copier != null) {
            return this.copier.getCopy();
        }
        if (this.readerCopier != null) {
            return this.readerCopier.getCopy();
        }
        return new byte[0];
    }

}
