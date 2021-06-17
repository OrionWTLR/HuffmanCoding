import java.util.*;

public class HuffTree {
    private static class Node{
        char data;
        int count;
        Node left = null;
        Node right = null;
        Node(){
        }
        Node(char d){
            data = d;
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

    private Node[] letterCount(){
        //extract string from source file
        Parser p = new Parser("source_text");
        ArrayList<String> textList = p.extractString();

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

        System.out.println(textList);
        System.out.println(charCounts);
        for(Node n : forest){
            System.out.print(n.data+" & "+n.count+", ");
        }

        //sort the forest according the count of each Node
        QuickSort qs = new QuickSort();
        qs.quickSort(forest);

        System.out.println();
        for(Node n : forest){
            System.out.print(n.data+" & "+n.count+", ");
        }

        return forest;
    }

    public void mergeForest(){
        Node[] nodes = letterCount();

        for(Node n : nodes){
            System.out.print(n.data+" & "+n.count+", ");
        }

        //this is a forest of many trees
        //the goal is to make a single tree out of the many
        LinkedList<Node> forest = new LinkedList<>(Arrays.asList(nodes));
        


    }

    public static void main(String[] args){
        HuffTree hf = new HuffTree();
        hf.mergeForest();

    }
}
