package org.example;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ZstdCompression {

    public byte[] compress(String data) {
        byte[] input = data.getBytes(StandardCharsets.UTF_8);
        byte[] lz77Encoded = lz77Encode(input);
        byte[] huffmanEncoded = huffmanEncode(lz77Encoded);

        return huffmanEncoded;
    }

    public String decompress(byte[] compressedData) {
        byte[] huffmanDecoded = huffmanDecode(compressedData);
        byte[] lz77Decoded = lz77Decode(huffmanDecoded);

        return new String(lz77Decoded, StandardCharsets.UTF_8);
    }

    private byte[] lz77Encode(byte[] input) {
        int windowSize = 4096;
        int maxOutputSize = input.length * 2;
        byte[] output = new byte[maxOutputSize];
        int outputIndex = 0;

        int pos = 0;
        while (pos < input.length) {
            int bestLength = 0;
            int bestDistance = 0;
            for (int distance = 1; distance <= windowSize && pos - distance >= 0; distance++) {
                int length = 0;

                while (length < 258 && pos + length < input.length && input[pos + length] == input[pos - distance + length]) {
                    length++;
                }

                if (length > bestLength) {
                    bestLength = length;
                    bestDistance = distance;
                }
            }

            if (bestLength > 3) {
                output[outputIndex++] = 1;
                output[outputIndex++] = (byte) (bestDistance >> 8);
                output[outputIndex++] = (byte) (bestDistance & 0xFF);
                output[outputIndex++] = (byte) bestLength;
                pos += bestLength;
            } else {
                output[outputIndex++] = 0;
                output[outputIndex++] = input[pos++];
            }
        }

        return Arrays.copyOf(output, outputIndex);
    }

    private byte[] lz77Decode(byte[] input) {
        int maxOutputSize = input.length * 256;
        byte[] output = new byte[maxOutputSize];
        int outputIndex = 0;

        int pos = 0;
        while (pos < input.length) {
            int flag = input[pos++];
            if (flag == 1) {
                int distance = ((input[pos++] & 0xFF) << 8) | (input[pos++] & 0xFF);
                int length = input[pos++];

                for (int i = 0; i < length; i++) {
                    output[outputIndex] = output[outputIndex - distance];
                    outputIndex++;
                }
            } else {
                output[outputIndex++] = input[pos++];
            }
        }

        return Arrays.copyOf(output, outputIndex);
    }

    private byte[] huffmanEncode(byte[] input) {
        Map<Byte, Integer> frequencyMap = new HashMap<>();
        for (byte b : input) {
            frequencyMap.put(b, frequencyMap.getOrDefault(b, 0) + 1);
        }

        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(node -> node.frequency));
        frequencyMap.forEach((key, value) -> {
            Node node = new Node();
            node.b = key;
            node.frequency = value;
            priorityQueue.add(node);
        });

        while (priorityQueue.size() > 1) {
            Node left = priorityQueue.poll();
            Node right = priorityQueue.poll();
            Node parent = new Node();
            parent.left = left;
            parent.right = right;
            parent.frequency = left.frequency + right.frequency;
            priorityQueue.add(parent);
        }

        Node root = priorityQueue.poll();
        Map<Byte, String> huffmanCodeMap = new HashMap<>();
        buildHuffmanCode(root, "", huffmanCodeMap);

        StringBuilder encodedData = new StringBuilder();
        for (byte b : input) {
            encodedData.append(huffmanCodeMap.get(b));
        }

        int byteLength = (encodedData.length() + 7) / 8;
        byte[] output = new byte[byteLength];
        for (int i = 0; i < encodedData.length(); i++) {
            if (encodedData.charAt(i) == '1') {
                output[i / 8] |= 1 << (7 - i % 8);
            }
        }

        return output;
    }

    private byte[] huffmanDecode(byte[] input) {
        // Назначить начальную емкость результата на основе предполагаемого размера
        int estimatedSize = 256 * (input.length);
        byte[] output = new byte[estimatedSize];
        int outputIndex = 0;

        // Примитивный декодировщик, который просто копирует байты (заменить на свой декодер)
        for (int i = 0; i < input.length; i++) {
            output[outputIndex++] = input[i];
        }

        // Уменьшить результирующий массив до фактического размера
        return Arrays.copyOf(output, outputIndex);
    }

    private void buildHuffmanCode(Node node, String code, Map<Byte, String> huffmanCodeMap) {
        if (node.left == null && node.right == null) {
            huffmanCodeMap.put(node.b, code);
        } else {
            buildHuffmanCode(node.left, code + '0', huffmanCodeMap);
            buildHuffmanCode(node.right, code + '1', huffmanCodeMap);
        }
    }

    private static class Node {
        byte b;
        int frequency;
        Node left, right;
    }
}