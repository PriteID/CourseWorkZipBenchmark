package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GzipCompression {

    public byte[] compress(String data) throws IOException {
        byte[] input = data.getBytes(StandardCharsets.UTF_8);
        byte[] header = createHeader();
        byte[] deflatedData = deflate(input);
        byte[] footer = createFooter(input);

        byte[] output = new byte[header.length + deflatedData.length + footer.length];
        int pos = 0;

        System.arraycopy(header, 0, output, pos, header.length);
        pos += header.length;
        System.arraycopy(deflatedData, 0, output, pos, deflatedData.length);
        pos += deflatedData.length;
        System.arraycopy(footer, 0, output, pos, footer.length);

        return output;
    }

    public String decompress(byte[] compressedData) throws IOException {
        // Step 1: Skip the Gzip header (10 bytes) and footer (8 bytes).
        int headerSize = 10;
        int footerSize = 8;

        byte[] deflateData = Arrays.copyOfRange(compressedData, headerSize, compressedData.length - footerSize);

        byte[] decompressedData = inflate(deflateData);
        return new String(decompressedData, StandardCharsets.UTF_8);
    }

    private byte[] createHeader() {
        // Create Gzip header (10 bytes)
        byte[] header = new byte[10];
        header[0] = 0x1F; // ID1
        header[1] = (byte) 0x8B; // ID2
        header[2] = 0x08; // Compression method (Deflate)
        header[3] = 0x00; // Flags
        header[4] = header[5] = header[6] = header[7] = 0x00; // Modification time
        header[8] = 0x00; // Extra flags
        header[9] = 0x03; // Operating system (Unix)
        return header;
    }

    private byte[] createFooter(byte[] input) {
        int crc = crc32(input);
        int length = input.length;

        byte[] footer = new byte[8];
        footer[0] = (byte) (crc & 0xFF);
        footer[1] = (byte) ((crc >> 8) & 0xFF);
        footer[2] = (byte) ((crc >> 16) & 0xFF);
        footer[3] = (byte) ((crc >> 24) & 0xFF);
        footer[4] = (byte) (length & 0xFF);
        footer[5] = (byte) ((length >> 8) & 0xFF);
        footer[6] = (byte) ((length >> 16) & 0xFF);
        footer[7] = (byte) ((length >> 24) & 0xFF);

        return footer;
    }

    private byte[] deflate(byte[] data) throws IOException {
        // Implement deflate algorithm (combination of LZ77 and Huffman coding)

        // Placeholder code for deflate (this is very simplified and not actual deflate)
        byte[] output = new byte[data.length + 1];
        output[0] = (byte) data.length;
        System.arraycopy(data, 0, output, 1, data.length);

        return output;
    }

    private byte[] inflate(byte[] data) throws IOException {
        // Implement inflate algorithm (reverse of deflate)

        // Placeholder code for inflate (this is very simplified and not actual inflate)
        int length = data[0] & 0xFF;
        return Arrays.copyOfRange(data, 1, 1 + length);
    }

    private int crc32(byte[] data) {
        int crc = 0xFFFFFFFF;

        for (byte b : data) {
            crc ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ 0xEDB88320;
                } else {
                    crc >>>= 1;
                }
            }
        }
        return crc ^ 0xFFFFFFFF;
    }
}