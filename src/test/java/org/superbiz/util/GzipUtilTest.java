package org.superbiz.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class GzipUtilTest {
    @Test(expected = IllegalArgumentException.class)
    public void zipShouldThrowIllegalArgumentExceptionWhenStringToCompressIsNull() {
        GzipUtil.zip(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zipShouldThrowIllegalArgumentExceptionWhenStringToCompressIsEmpty() {
        GzipUtil.zip("");
    }

    @Test
    public void zipShouldGzipStringWhenStringIsNotEmpty() {
        String xml = "<Hello>World</Hello>";
        byte[] actual = GzipUtil.zip(xml);
        assertTrue(GzipUtil.isZipped(actual));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unzipShouldThrowIllegalArgumentExceptionWhenByteArrayToDecompressIsNull() {
        GzipUtil.unzip(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unzipShouldThrowIllegalArgumentExceptionWhenByteArrayToDecompressIsEmpty() {
        GzipUtil.unzip(new byte[0]);
    }

    @Test
    public void unzipShouldReturnInputByteArrayAsStringWhenByteArrayContentIsNotGzipped() {
        String xml = "<Hello>World</Hello>";
        byte[] bytes = xml.getBytes();
        String actual = GzipUtil.unzip(bytes);
        assertEquals(xml, actual);
    }

    @Test
    public void unzipShouldDecompressByteArrayGzippedContent() {
        String xml = "<Hello>World</Hello>";
        byte[] compressed = GzipUtil.zip(xml);
        String actual = GzipUtil.unzip(compressed);
        assertEquals(xml, actual);
    }

    @Test
    public void isZippedShouldReturnFalseWhenContentIsNotGzipped() {
        byte[] bytes = new byte[]{1, 2, 3};
        boolean actual = GzipUtil.isZipped(bytes);
        assertFalse(actual);
    }

    @Test
    public void isZippedShouldReturnTrueWhenContentIsGzipped() {
        byte[] bytes = GzipUtil.zip("some data");
        boolean actual = GzipUtil.isZipped(bytes);
        assertTrue(actual);
    }
}