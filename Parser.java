import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    String filename;
    InputStream file;
    public Parser(){

    }
    public Parser(String fn){
        filename = fn;
    }

    public ArrayList<String> extractString(){
        file = Parser.class.getResourceAsStream(filename);
        Scanner sc = new Scanner(file);

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
           os = new FileOutputStream("/Users/Hunter/IdeaProjects/HuffmanTree/src/huff_coded");
           for(String s : text) {
               os.write(s.getBytes(), 0, s.length());
               os.write("\n".getBytes(), 0, "\n".length());
           }
       } catch (IOException e ){
           e.printStackTrace();
       } finally {
           try{
               assert os != null;
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
