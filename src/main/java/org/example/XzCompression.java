package org.example;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class XzCompression {

    public byte[] compress(String data) {
        byte[] input = data.getBytes(StandardCharsets.UTF_8);
        return lzmaCompress(input);
    }

    public String decompress(byte[] compressedData) {
        byte[] decompressedData = lzmaDecompress(compressedData);
        return new String(decompressedData, StandardCharsets.UTF_8);
    }

    private byte[] lzmaCompress(byte[] input) {
        // Составление словаря и подсчет частоты символов
        Map<Byte, Integer> dictionary = new HashMap<>();
        for (byte b : input) {
            dictionary.put(b, dictionary.getOrDefault(b, 0) + 1);
        }

        // Рассчет размера результирующего массива
        int outputSize = dictionary.size() * 2; // по 2 байта на каждую пару (символ, частота)
        byte[] output = new byte[outputSize];
        int outputIndex = 0;

        // Заполнение выходного массива данными
        for (Map.Entry<Byte, Integer> entry : dictionary.entrySet()) {
            output[outputIndex++] = entry.getKey();
            output[outputIndex++] = entry.getValue().byteValue();
        }

        return output;
    }

    private byte[] lzmaDecompress(byte[] input) {
        // Рассчет изначального размера массива based on асортимент символов
        int estimatedSize = 256 * (input.length / 2);  // максимально возможный размер
        byte[] output = new byte[estimatedSize];
        int outputIndex = 0;

        for (int i = 0; i < input.length; i += 2) {
            byte b = input[i];
            int count = input[i + 1] & 0xFF;

            for (int j = 0; j < count; j++) {
                output[outputIndex++] = b;
            }
        }

        // Сжатие результирующего массива до фактического размера
        byte[] result = new byte[outputIndex];
        System.arraycopy(output, 0, result, 0, outputIndex);

        return result;
    }
}