import java.io.*;
import java.util.*;

class HuffmanNode {
    char ch;
    int freq;
    HuffmanNode left, right;

    HuffmanNode(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
        left = right = null;
    }
}

public class CustomHuffman {

    public static void main(String[] args) {
        String inputFilePath = "";  //scieżki do zmiany skąd ma pobierać plik igdzie wydawać
        String outputFilePath = ""; //scieżki do zmiany skąd ma pobierać plik igdzie wydawać
        Map<Character, String> huffmanCodes = encrypt(inputFilePath, outputFilePath);
        System.out.println("Huffman:");
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static Map<Character, Integer> calculate(String text) {
        Map<Character, Integer> charCounts = new HashMap<>();
        for (char c : text.toCharArray()) {
            charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
        }
        return charCounts;
    }

    private static Map<Character, String> huffmanik(String text) {
        Map<Character, Integer> charCounts = calculate(text);
        PriorityQueue<HuffmanNode> minHeap = new PriorityQueue<>((a, b) -> a.freq - b.freq);
        for (Map.Entry<Character, Integer> entry : charCounts.entrySet()) {
            minHeap.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (minHeap.size() > 1) {
            HuffmanNode x = minHeap.poll();
            HuffmanNode y = minHeap.poll();
            HuffmanNode z = new HuffmanNode('\0', x.freq + y.freq);
            z.left = x;
            z.right = y;
            minHeap.offer(z);
        }

        HuffmanNode root = minHeap.poll();
        Map<Character, String> codes = new HashMap<>();
        codzik(root, "", codes);
        return codes;
    }

    private static void codzik(HuffmanNode node, String code, Map<Character, String> codes) {
        if (node != null) {
            if (node.ch != '\0') {
                codes.put(node.ch, code);
            }
            codzik(node.left, code + "0", codes);
            codzik(node.right, code + "1", codes);
        }
    }

    private static Map<Character, String> encrypt(String inputFilePath, String outputFilePath) {
        StringBuilder inputData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            int c;
            while ((c = reader.read()) != -1) {
                inputData.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Character, String> huffmanCodes = huffmanik(inputData.toString());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }

            String encodedText = encodeWithHuffmanik(inputData.toString(), huffmanCodes);
            int paddingSize = (8 - encodedText.length() % 8) % 8;
            String paddedEncodedText = encodedText + "0".repeat(paddingSize);
            byte[] byteArr = new byte[paddedEncodedText.length() / 8];
            for (int i = 0; i < byteArr.length; i++) {
                byteArr[i] = (byte) Integer.parseInt(paddedEncodedText.substring(i * 8, (i + 1) * 8), 2);
            }
            writer.write(new String(byteArr));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return huffmanCodes;
    }

    private static String encodeWithHuffmanik(String text, Map<Character, String> huffmanCodes) {
        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedText.append(huffmanCodes.get(c));
        }
        return encodedText.toString();
    }
}