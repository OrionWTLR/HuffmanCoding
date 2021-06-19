import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    String filename;
    InputStream fileIn;
    OutputStream fileOut;
    public Parser(){

    }
    public Parser(String fn){
        filename = fn;
    }

    public ArrayList<String> extractString(){
        fileIn = Parser.class.getResourceAsStream(filename);
        Scanner sc = new Scanner(fileIn);

        ArrayList<String> lineByLine = new ArrayList<>();
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            lineByLine.add(line);
        }

        sc.close();

        return lineByLine;
    }

    public void writeInto(String words) {
        try {
            FileWriter myWriter = new FileWriter("encoded.txt");
            myWriter.write(words);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Parser p = new Parser("source_text");
        ArrayList<String> txtFile = p.extractString();
        System.out.println(txtFile);
    }

}
