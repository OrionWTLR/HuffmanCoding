import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    String filename;
    InputStream file;
    public Parser(){

    }
    public Parser(String fn){
        filename = fn;
        file = Parser.class.getResourceAsStream(filename);
    }

    public ArrayList<String> extractString(){
        Scanner sc = new Scanner(file);

        ArrayList<String> lineByLine = new ArrayList<>();
        while(sc.hasNextLine()){
            String line = sc.nextLine();
            lineByLine.add(line);
        }

        sc.close();

        return lineByLine;
    }

    public static void main(String[] args){
        Parser p = new Parser("source_text");
        ArrayList<String> txtFile = p.extractString();
        System.out.println(txtFile);
    }

}
