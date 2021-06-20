import java.util.*;

public class HuffTree {
    private static class Node{
        Character character;
        int count;
        Node parent = null;
        Node left = null;
        Node right = null;
        boolean whichChild;

        Node(){
        }
        Node(char d){
            character = d;
        }
        public String toString(){
            return "?"+count +","+ character+"?";
        }
        public String toStringWithChildren(){
            String word = toString();
            return word+"->("+left+" & "+right+")";
        }
    }

    class MergeSort {

        void merge(Node[] nodes, int low, int mid, int high) {
            int l = mid - low + 1;
            int r = high - mid;

            Node[] left = new Node[l];
            Node[] right = new Node[r];

            for(int i = 0; i < l; i++) {
                left[i] = nodes[low + i];
            }
            for(int j = 0; j < r; j++) {
                right[j] = nodes[mid + 1 + j];
            }


            int i = 0, j = 0;
            int k = low;
            while (i < l && j < r) {
                if (left[i].count <= right[j].count) {
                    nodes[k] = left[i];
                    i++;
                } else {
                    nodes[k] = right[j];
                    j++;
                }
                k++;
            }

            while (i < l) {
                nodes[k] = left[i];
                i++;
                k++;
            }

            while (j < r) {
                nodes[k] = right[j];
                j++;
                k++;
            }
        }

        void mergesort(Node[] nodes){
            mergesort(nodes, 0, nodes.length-1);
        }
        void mergesort(Node[] nodes, int l, int r) {
            if (l < r) {
                // Find the middle point
                int m = l + (r - l) / 2;

                // Sort first and second halves
                mergesort(nodes, l, m);
                mergesort(nodes, m + 1, r);

                // Merge the sorted halves
                merge(nodes, l, m, r);
            }
        }
    }

    private ArrayList<String> textList = new ArrayList<>();
    private Node[] letterCount(){
        //extract string from source file
        Parser p = new Parser("source_text");
        textList = p.extractString();

        //Keep track of each character and how many times it repeats
        HashMap<Character, Integer> charCounts = new HashMap<>();

        for(String s : textList){
            for(int i = 0; i < s.length(); i++){
                char c = s.charAt(i);

                //if get count returns null then the character hasn't appeared before so its count is now 1
                if(charCounts.get(c) == null){
                    Integer n = 1;
                    charCounts.put(c, n);
                }else{//otherwise the count is 1 plus its current count
                    Integer n = charCounts.get(c);
                    charCounts.replace(c, n+1);
                }

            }
        }

        //make two lists of the same size that keeps track of the character and the number of times it repeats
        ArrayList<Character> letters = new ArrayList<>();
        charCounts.forEach((key, value) -> letters.add(key));
        ArrayList<Integer> numbers = new ArrayList<>();
        charCounts.forEach((key, value) -> numbers.add(value));

        //use the data from letters and numbers to make a forest of nodes that store the character and the number of
        //times it repeats
        Node[] forest = new Node[charCounts.size()];
        for(int i = 0; i < charCounts.size(); i++){
            Node node = new Node(letters.get(i));
            node.count = numbers.get(i);
            forest[i] = node;
        }

        //sort the forest according the count of each Node
        MergeSort ms = new MergeSort();
        ms.mergesort(forest);

        return forest;
    }

    Node tree;
    public HashMap<Character, String> encodingTable(){
        Node[] nodes = letterCount();

        //this is a forest of many trees
        //the goal is to make a single tree out of the many
        LinkedList<Node> forest = new LinkedList<>(Arrays.asList(nodes));

        //nodes is a list sorted from least to greatest
        /*while the number of trees in the forest is greater than 2 get the first three nodes from the list and compare
        * the first and third nodes.
        * If the first is less than or equal to third the merge the first and second nodes
        * into a single node P where P.count is the sum of first and second and the children of P are first and second.
        * remove first and second from the forest and add P in their place
        * Otherwise merge the second and third in the exact same way as before then remove the second and third nodes
        * from forest then add P in their place
        * */
        while(forest.size() > 2){
            Node first = forest.getFirst();
            Node second = forest.get(1);
            Node third = forest.get(2);

            if(first.count < third.count || first.count == third.count){
                Node n = mergeNodes(first, second);
                forest.remove(0); forest.remove(0);
                forest.add(0, n);
            }else{
                Node n = mergeNodes(second, third);
                forest.remove(1); forest.remove(1);
                forest.add(1, n);
            }
        }

        //There are two trees leftover which means they should be merged and after that there is only one tree left
        tree = mergeNodes(forest.get(0), forest.get(1));

        //Use the tree to create and encoding table
        /*Keep in mind that when two nodes are merged both of them remember their parent node
        * So for each node in the list nodes backtrack its path from leaf to root such that left is denoted 0 and right
        * is denoted 1. This path is a string that should be reversed to give the path from root to leaf of a particular
        * node. Store the character in the leaf of this path and the String path in a hashmap so that each character has
        * a map to a unique binary code
        * */
        HashMap<Character, String> encodingTable = new HashMap<>();
        for(Node n : nodes) {
            StringBuilder path = new StringBuilder();
            char character = n.character;
            while (n != null) {
                byte binary = 0;
                if (n.whichChild) {
                    binary = 1;
                }
                path.append(binary);
                n = n.parent;
            }
            path.reverse();
            encodingTable.put(character, path.toString());
        }

        return encodingTable;
    }

    HashMap<Character, String> charToBinary = new HashMap<>();
    HashMap<String, Character> binaryToChar = new HashMap<>();
    private void encode() {
        charToBinary = encodingTable();
        Set<Character> keys =  charToBinary.keySet();
        ArrayList<String> values = new ArrayList<>();
        for(char c : keys){
            values.add(charToBinary.get(c));
        }

        int increment = 0;
        for(char c : keys){
            binaryToChar.put(values.get(increment), c);
            increment++;
        }

        charToBinary.forEach((key, value) -> System.out.println(key+", "+value));
        System.out.println();
        binaryToChar.forEach((key, value) -> System.out.println(key+", "+value));

        treeToArray(tree);

        /*Encode each String element in text list */

        ArrayList<String> encoded = new ArrayList<>();
        for(String t : textList){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < t.length(); i++){
                sb.append(charToBinary.get(t.charAt(i))).append(".");
            }
            encoded.add(sb.toString());
        }

        /*This is the STOP character that will tell the decoder to stop decoding at this point*/

        Parser p = new Parser("huff_coded");

        for(String s : encoded){
            System.out.println(s);
        }
        System.out.println();

        p.writeInto(encoded);
    }

    private void decode(){
        Parser p = new Parser("huff_coded");
        ArrayList<String> encoded = p.extractString();
        ArrayList<String> plaintext = new ArrayList<>();

        String line = encoded.get(0);
        System.out.println(line);

        ArrayList<String> codeArray = new ArrayList<>();
        int i = 0; char c = line.charAt(i);
        StringBuilder sb = new StringBuilder();
        while(c != '.'){
            sb.append(c);
            i++; c = line.charAt(i);
            if(c == '.' && i < line.length()-1){
                codeArray.add(sb.toString());
                sb.delete(0, line.length());
                i++; c = line.charAt(i);
            }
        }

        StringBuilder plain = new StringBuilder();
        for(String code : codeArray){
            plain.append(binaryToChar.get(code));
        }
        System.out.println(plain);

    }

    private Node mergeNodes(Node n1, Node n2){
        Node parent = new Node();
        parent.left = n1; parent.right = n2;
        parent.count = n1.count+n2.count;

        parent.left.parent = parent; parent.right.parent = parent;
        parent.left.whichChild = false; parent.right.whichChild = true;

        return parent;
    }

    private void printTree(Node tree) {
        if (tree != null) {
            printTree(tree.left);

            if(tree.left == null && tree.right == null){
                System.out.println(tree+"|");
            }
            if(tree.left != null && tree.right == null){
                System.out.println(tree+">"+tree.left+"&"+null);
            }
            if(tree.left == null && tree.right != null){
                System.out.println(tree+">"+null+"&"+tree.right);
            }
            if(tree.left != null && tree.right != null){
                System.out.println(tree+">"+tree.left+"&"+tree.right);
            }

            printTree(tree.right);
        }
    }

    ArrayList<String> arrayedTree = new ArrayList<>();
    private ArrayList<String> treeToArray(Node tree){
        if(tree != null){
            String s = "";
            treeToArray(tree.left);
            if(tree.left == null && tree.right == null){
                s = tree+"|";
            }
            if(tree.left != null && tree.right == null){
                s = tree+">"+tree.left+"&"+null;
            }
            if(tree.left == null && tree.right != null){
                s = tree+">"+null+"&"+tree.right;
            }
            if(tree.left != null && tree.right != null){
                s = tree+">"+tree.left+"&"+tree.right;
            }
            arrayedTree.add(s);
            treeToArray(tree.right);
        }
        return arrayedTree;
    }

    public static void main(String[] args) {
        HuffTree hf = new HuffTree();
        hf.encode();
        hf.decode();
    }
}
