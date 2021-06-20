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

    public void writeInto(ArrayList<String> text) {
       OutputStream os = null;
       try{
           os = new FileOutputStream(new File("/Users/Hunter/IdeaProjects/HuffmanTree/src/huff_coded"));
           for(String s : text) {
               os.write(s.getBytes(), 0, s.length());
           }
       } catch (IOException e ){
           e.printStackTrace();
       } finally {
           try{
               os.close();
           } catch (IOException e){
               e.printStackTrace();
           }
       }
    }

    public static void main(String[] args){
        Parser p = new Parser("source_text");
        ArrayList<String> txtFile = p.extractString();
        System.out.println(txtFile);
    }

}
