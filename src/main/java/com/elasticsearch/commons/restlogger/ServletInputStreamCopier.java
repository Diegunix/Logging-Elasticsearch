package com.elasticsearch.commons.restlogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class ServletInputStreamCopier extends ServletInputStream {

    private InputStream inputStream;
    private ByteArrayOutputStream copy;

    public ServletInputStreamCopier(InputStream inputStream1) {
        this.inputStream = inputStream1;
        this.copy = new ByteArrayOutputStream(1024);
    }

    @Override
    public int read() throws IOException {
        int result = this.inputStream.read();
        this.copy.write(result);
        return result;
    }

    public byte[] getCopy() {
        return this.copy.toByteArray();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }
}
