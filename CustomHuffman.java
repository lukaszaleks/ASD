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

class MinHeap {
    List<HuffmanNode> heap;

    MinHeap() {
        this.heap = new ArrayList<>();
    }

    private void swap(int i, int j) {
        HuffmanNode temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int leftChild(int i) {
        return 2 * i + 1;
    }

    private int rightChild(int i) {
        return 2 * i + 2;
    }

    private void heapifyUp(int i) {
        while (i > 0 && heap.get(parent(i)).freq > heap.get(i).freq) {
            swap(parent(i), i);
            i = parent(i);
        }
    }

    private void heapifyDown(int i) {
        int minIndex = i;
        int left = leftChild(i);
        int right = rightChild(i);

        if (left < heap.size() && heap.get(left).freq < heap.get(minIndex).freq) {
            minIndex = left;
        }

        if (right < heap.size() && heap.get(right).freq < heap.get(minIndex).freq) {
            minIndex = right;
        }

        if (i != minIndex) {
            swap(i, minIndex);
            heapifyDown(minIndex);
        }
    }

    void insert(HuffmanNode value) {
        heap.add(value);
        heapifyUp(heap.size() - 1);
    }

    HuffmanNode extractMin() {
        if (heap.isEmpty()) {
            throw new IllegalStateException("Heap is empty");
        }

        HuffmanNode min = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);
        heapifyDown(0);
        return min;
    }

    boolean isEmpty() {
        return heap.isEmpty();
    }
}

public class CustomHuffman {

    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\l.aleksandrowicz\\Downloads\\ASD\\src\\main\\java\\input.txt";
        String outputFilePath = "C:\\Users\\l.aleksandrowicz\\Downloads\\ASD\\src\\main\\java\\output.bin";
        Map<Character, String> huffmanCodes = encrypt(inputFilePath, outputFilePath);
        System.out.println("Huffman:");
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static Map<Character, Integer> countChars(String text) {
        Map<Character, Integer> charCounts = new HashMap<>();
        for (char c : text.toCharArray()) {
            charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
        }
        return charCounts;
    }

    private static Map<Character, String> huffman(Map<Character, Integer> charCounts) {
        MinHeap minHeap = new MinHeap();
        for (Map.Entry<Character, Integer> entry : charCounts.entrySet()) {
            minHeap.insert(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (minHeap.heap.size() > 1) {
            HuffmanNode x = minHeap.extractMin();
            HuffmanNode y = minHeap.extractMin();
            HuffmanNode z = new HuffmanNode('\0', x.freq + y.freq);
            z.left = x;
            z.right = y;
            minHeap.insert(z);
        }

        HuffmanNode root = minHeap.extractMin();
        Map<Character, String> codes = new HashMap<>();
        generateCodes(root, "", codes);
        return codes;
    }

    private static void generateCodes(HuffmanNode node, String code, Map<Character, String> codes) {
        if (node != null) {
            if (node.ch != '\0') {
                codes.put(node.ch, code);
            }
            generateCodes(node.left, code + "0", codes);
            generateCodes(node.right, code + "1", codes);
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

        Map<Character, Integer> charCounts = countChars(inputData.toString());
        Map<Character, String> huffmanCodes = huffman(charCounts);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }

            String encodedText = encodeWithHuffman(inputData.toString(), huffmanCodes);
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

    private static String encodeWithHuffman(String text, Map<Character, String> huffmanCodes) {
        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedText.append(huffmanCodes.get(c));
        }
        return encodedText.toString();
    }
}
