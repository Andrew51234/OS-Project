import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Project {

    private static String[] memory;

    public Project () throws IOException {
        memory = new String[initMemory("Program 1.txt", "Program 2.txt", "Program 3.txt")];
        //addFile("Program 1.txt", 1);
        //addFile("Program 2.txt", 2);
        //addFile("Program 3.txt", 3);
    }

    public static int initMemory (String file1, String file2, String file3) throws IOException {
        int memSize = 0;
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String current1 = br1.readLine();
        while(current1 != null) {
            String[] instruction = current1.split(" ");
            if(instruction[0].equals("assign"))
                memSize++;
            memSize++;
            current1 = br1.readLine();
        }

        FileReader fr2 = new FileReader(file2);
        BufferedReader br2 = new BufferedReader(fr2);
        String current2 = br2.readLine();
        while(current2 != null) {
            String[] instruction = current2.split(" ");
            if(instruction[0].equals("assign"))
                memSize++;
            memSize++;
            current2 = br2.readLine();
        }

        FileReader fr3 = new FileReader(file3);
        BufferedReader br3 = new BufferedReader(fr3);
        String current3 = br3.readLine();
        while(current3 != null) {
            String[] instruction = current3.split(" ");
            if(instruction[0].equals("assign"))
                memSize++;
            memSize++;
            current3 = br3.readLine();
        }
        return memSize + 3;
    }

    public boolean variableInMem (String value){
        for(int i=0; i< memory.length && memory[i] != null; i++){
            String[] curr = memory[i].split(" = ");
                if(curr[0].equals(value))
                    return true;
        }
        return false;
    }

    public String getValue (String variable){
        for(int i=0; i< memory.length && memory[i] != null; i++){
            String[] curr = memory[i].split(" = ");
            if(curr[0].equals(variable))
                return curr[1];
        }
        return null;
    }

    public int indexOf (String variable){
        for(int i=0; i< memory.length && memory[i] != null; i++){
            String[] curr = memory[i].split(" = ");
            if(curr[0].equals(variable))
                return i;
        }
        return -1;
    }

    public static void writeMem(String value){
        for(int i =0; i< memory.length; i++){
            if(memory[i]== null){
                memory[i] = value;
                break;
            }
        }
    }

    public static void writeMem(String value, int location){
        memory[location] = value;
    }

    public static String readMem(int location){
        return memory[location];
    }

    public void printMem (){
        System.out.println("Memory content: ");
        for(int i=0; i< memory.length && memory[i] != null; i++){
            System.out.println(memory[i]);
        }
    }

    public void addFile (String file, int fileNo) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String current = br.readLine();
        int counter = 1;
        writeMem("Process " + fileNo);
        while(current != null){
            writeMem("Instruction " + counter++ + " " + current);
            current = br.readLine();
        }
    }

    public void print (String value){
        if(variableInMem(value))
            System.out.println(getValue(value));
        else
            System.out.println(value);
    }

    public String input (){
        Scanner sc = new Scanner(System.in);
        System.out.println("Input: ");
        String input = sc.nextLine();
        return input;
    }

    public void assign(String variable, Object value){
        if(variableInMem(variable)){
            writeMem(variable+" = "+value, indexOf(variable));
        }
        else
            writeMem(variable+" = "+value);
    }

    public void writeFile(String file, String s){
        try {
            FileWriter myWriter = new FileWriter(file,true);
            myWriter.write(s);
            myWriter.write("\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String readFile (String f) throws IOException {
        String file = f;
        Path path = Paths.get(file);
        BufferedReader br = Files.newBufferedReader(path);
        String instructions = "";
        String current = br.readLine();
        while(current != null){
            instructions += current;
            instructions += "\n";
            current = br.readLine();
        }
        br.close();
        return instructions;
    }

    public void add (String x, String y){
        double result = Double.parseDouble(getValue(x)) + Double.parseDouble(getValue(y));
        writeMem(x+" = "+result, indexOf(x));
    }


    public void parser() throws IOException {
        for(int i = 0; i< memory.length && memory[i] != null; i++){
            String[] instruction = readMem(i).split(" ");
            if(instruction[0].contains("Process"))
                continue;

            for(int j = 2; j<instruction.length; j++){
                if(instruction[j].equals("assign")) {
                    if(instruction[j+2].equals("input")){
                        assign(instruction[j+1], input());
                    }
                    if(instruction[j+2].equals("readFile"))
                        assign(instruction[j+1], readFile(getValue(instruction[j+3])));
                }

                else if(instruction[j].equals("print")){
                    print(instruction[j+1]);
                }

                else if(instruction[j].equals("readFile")){
                    readFile(getValue(instruction[j + 1]));
                }

                else if(instruction[j].equals("writeFile")){
                    writeFile(getValue(instruction[j+1]), getValue(instruction[j+2]));
                }

                else if(instruction[j].equals("add")){
                    add(instruction[j+1],instruction[j+2]);
                }
            }
        }
    }


    //MAIN METHOD
    public static void main(String [] args) throws IOException {
        Project pr = new Project();
        // pr.parser();
        // pr.printMem();
    }
}
