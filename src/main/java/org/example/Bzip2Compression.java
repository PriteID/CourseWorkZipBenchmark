package org.example;

import java.io.*;
import java.util.*;

public class Bzip2Compression {

    public byte[] compress(String input) throws IOException {
        String bwt = burrowsWheelerTransform(input);
        int[] mtf = moveToFrontEncode(bwt);
        ByteArrayOutputStream rle = runLengthEncode(mtf);
        return rle.toByteArray();
    }

    public String decompress(byte[] compressedData) throws IOException {
        int[] rld = runLengthDecode(compressedData);
        String mtfd = moveToFrontDecode(rld);
        return inverseBurrowsWheelerTransform(mtfd);
    }

    private String burrowsWheelerTransform(String input) {
        int len = input.length();
        StringBuilder bwt = new StringBuilder(len);
        int[] suffixArray = buildSuffixArray(input);

        for (int i = 0; i < len; i++) {
            int suffixIndex = suffixArray[i];
            char bwtChar = suffixIndex == 0 ? input.charAt(len - 1) : input.charAt(suffixIndex - 1);
            bwt.append(bwtChar);
        }
        return bwt.toString();
    }

    private int[] moveToFrontEncode(String input) {
        List<Character> symbols = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            symbols.add((char) i);
        }
        int[] mtf = new int[input.length()];
        for (int i = 0; i < input.length(); i++) {
            char curr = input.charAt(i);
            int index = symbols.indexOf(curr);
            mtf[i] = index;
            symbols.remove(index);
            symbols.add(0, curr);
        }
        return mtf;
    }

    private ByteArrayOutputStream runLengthEncode(int[] input) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int count = 1;
        for (int i = 1; i < input.length; i++) {
            if (input[i] == input[i - 1]) {
                count++;
            } else {
                bos.write(input[i - 1]);
                bos.write(count);
                count = 1;
            }
        }
        bos.write(input[input.length - 1]);
        bos.write(count);
        return bos;
    }

    private int[] runLengthDecode(byte[] input) {
        List<Integer> decodedList = new ArrayList<>();
        for (int i = 0; i < input.length; i += 2) {
            int symbol = input[i] & 0xFF;
            int count = input[i + 1] & 0xFF;
            for (int j = 0; j < count; j++) {
                decodedList.add(symbol);
            }
        }
        return decodedList.stream().mapToInt(i -> i).toArray();
    }

    private String moveToFrontDecode(int[] input) {
        List<Character> symbols = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            symbols.add((char) i);
        }
        StringBuilder decoded = new StringBuilder(input.length);
        for (int index : input) {
            char curr = symbols.get(index);
            decoded.append(curr);
            symbols.remove(index);
            symbols.add(0, curr);
        }
        return decoded.toString();
    }

    private String inverseBurrowsWheelerTransform(String input) {
        int n = input.length();
        int[] count = new int[256];
        int[] next = new int[n];

        for (int i = 0; i < n; i++) {
            count[input.charAt(i)]++;
        }

        int[] cumulative = new int[256];
        for (int i = 1; i < 256; i++) {
            cumulative[i] = cumulative[i - 1] + count[i - 1];
        }

        for (int i = 0; i < n; i++) {
            next[cumulative[input.charAt(i)]++] = i;
        }

        StringBuilder orig = new StringBuilder(n);
        int ptr = next[0];
        for (int i = 0; i < n; i++) {
            orig.append(input.charAt(ptr));
            ptr = next[ptr];
        }

        return orig.toString();
    }

    private int[] buildSuffixArray(String text) {
        int n = text.length();
        Suffix[] suffixes = new Suffix[n];

        for (int i = 0; i < n; i++) {
            suffixes[i] = new Suffix();
            suffixes[i].index = i;
            suffixes[i].rank = text.charAt(i);
            suffixes[i].nextRank = (i + 1 < n) ? text.charAt(i + 1) : -1;
        }

        Arrays.sort(suffixes);

        int[] ind = new int[n];
        for (int length = 4; length < 2 * n; length *= 2) {
            int rank = 0, prevRank = suffixes[0].rank;
            suffixes[0].rank = rank;
            ind[suffixes[0].index] = 0;

            for (int i = 1; i < n; i++) {
                if (suffixes[i].rank == prevRank && suffixes[i].nextRank == suffixes[i - 1].nextRank) {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = rank;
                } else {
                    prevRank = suffixes[i].rank;
                    suffixes[i].rank = ++rank;
                }
                ind[suffixes[i].index] = i;
            }

            for (int i = 0; i < n; i++) {
                int nextIndex = suffixes[i].index + length / 2;
                suffixes[i].nextRank = (nextIndex < n) ? suffixes[ind[nextIndex]].rank : -1;
            }

            Arrays.sort(suffixes);
        }

        int[] suffixArr = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArr[i] = suffixes[i].index;
        }

        return suffixArr;
    }

    private static class Suffix implements Comparable<Suffix> {
        int index;
        int rank;
        int nextRank;

        public int compareTo(Suffix s) {
            if (rank != s.rank) {
                return Integer.compare(rank, s.rank);
            }
            return Integer.compare(nextRank, s.nextRank);
        }
    }
}