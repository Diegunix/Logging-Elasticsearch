package com.elasticsearch.commons.restlogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class ServletReaderCopier extends Reader {

    private Reader reader;
    private ByteArrayOutputStream copy;
    private OutputStreamWriter outputStreamWriter;

    public ServletReaderCopier(Reader reader1) {
        this.reader = reader1;
        this.copy = new ByteArrayOutputStream(1024);
        this.outputStreamWriter = new OutputStreamWriter(this.copy);
    }

    public byte[] getCopy() {
        try {
            this.outputStreamWriter.flush();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return this.copy.toByteArray();
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int length = this.reader.read(cbuf, off, len);
        if (length > 0) {
            this.outputStreamWriter.write(cbuf, off, length);
        }
        return length;
    }

}
