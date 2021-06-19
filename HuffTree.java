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
            return count +","+ character;
        }
        public String toStringWithChildren(){
            String word = toString();
            return word+"->("+left+" & "+right+")";
        }
    }

    public static class QuickSort{
        public void quickSort(Node[] list){
            quickSort(list, 0, list.length-1);
        }
        public void quickSort(Node[] list, int l, int h){
            if(l < h){
                random(list, l, h);
                Node pivot = list[h];
                int j = partition(list, l, h, pivot);
                quickSort(list, l, j - 1);
                quickSort(list, j + 1, h);
            }
        }
        private int partition(Node[] list, int l, int h, Node pivot){
            int i = (l - 1);
            int j = l;

            while(j < h){
                if(list[j].count <= pivot.count) {
                    i++;
                    swap(list, i, j);
                }
                j++;
            }

            swap(list, i + 1, h);
            return i + 1;
        }

        private void swap(Node[] list, int i, int j){
            Node tmp = list[i];
            list[i] = list[j];
            list[j] = tmp;
        }
        void random(Node[] list,int l,int h) {
            Random rand= new Random();
            int p = rand.nextInt(h-l)+l;
            swap(list, p, h);
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
        QuickSort qs = new QuickSort();
        qs.quickSort(forest);



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

    private void encode() {
        HashMap<Character, String> table = encodingTable();
        table.forEach((key, value) -> System.out.println(key+", "+value));

        /*Take each letter in file and encode them into a list of binary numbers*/
        StringBuilder encoded = new StringBuilder();

        /*Encode the tree itself into the file*/
        System.out.println(tree);

        for(String s : textList){
            for(int i = 0; i < s.length(); i++){
                char c = s.charAt(i);
                String code = table.get(c)+".";
                encoded.append(code);
            }
        }

        /*This is the STOP character that will tell the decoder to stop decoding at this point*/
        encoded.append('~');


       /* Parser p = new Parser("huff_coded_file");
        p.writeInto(encoded.toString());*/
    }

    private void decode(){

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
                System.out.println(tree+"-|");
            }
            if(tree.left != null && tree.right == null){
                System.out.println(tree+"->"+tree.left+" & "+null);
            }
            if(tree.left == null && tree.right != null){
                System.out.println(tree+"->"+null+" & "+tree.right);
            }
            if(tree.left != null && tree.right != null){
                System.out.println(tree+"->"+tree.left+" & "+tree.right);
            }

            printTree(tree.right);
        }
    }

    public static void main(String[] args) {
        HuffTree hf = new HuffTree();
        hf.encode();
    }
}
