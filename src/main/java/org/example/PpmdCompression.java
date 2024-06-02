package org.example;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PpmdCompression {

    public byte[] compress(String data) {
        byte[] input = data.getBytes(StandardCharsets.UTF_8);
        return ppmdCompress(input);
    }

    public String decompress(byte[] compressedData) {
        byte[] decompressedData = ppmdDecompress(compressedData);
        return new String(decompressedData, StandardCharsets.UTF_8);
    }

    private byte[] ppmdCompress(byte[] input) {
        int[] freqTable = new int[256];

        // Подсчет частоты символов
        for (byte b : input) {
            freqTable[b & 0xFF]++;
        }

        // Рассчитать необходимый размер массива
        int length = input.length * 2;
        byte[] output = new byte[length];
        int outputIndex = 0;

        for (byte b : input) {
            int symbol = b & 0xFF;
            output[outputIndex++] = (byte) symbol;
            output[outputIndex++] = (byte) freqTable[symbol];
        }

        return output;
    }

    private byte[] ppmdDecompress(byte[] input) {
        // Рассчитать необходимый размер массива
        int length = input.length / 2 * 256;
        byte[] output = new byte[length];
        int outputIndex = 0;

        Map<Integer, Integer> freqTable = new HashMap<>();
        for (int i = 0; i < input.length; i += 2) {
            int symbol = input[i] & 0xFF;
            int freq = input[i + 1] & 0xFF;
            freqTable.put(symbol, freq);
            for (int j = 0; j < freq; j++) {
                output[outputIndex++] = (byte) symbol;
            }
        }

        // Вернем массив нужного размера
        byte[] result = new byte[outputIndex];
        System.arraycopy(output, 0, result, 0, outputIndex);

        return result;
    }
}