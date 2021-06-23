import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Project {

    private static String[] memory;

    public Project () throws IOException {
        memory = new String[initMemory("Program 1.txt", "Program 2.txt", "Program 3.txt")];
        //addFile("Program 1.txt");
        //addFile("Program 2.txt");
        //addFile("Program 3.txt");
    }

    public static int initMemory (String file1, String file2, String file3) throws IOException {
        int memSize = 0;
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String current1 = br1.readLine();
        ArrayList<String> vars1 = new ArrayList<String>(fileSize(file1));
        while(current1 != null) {
            String[] instruction = current1.split(" ");
            if(instruction[0].equals("assign")){
                if(!vars1.contains(instruction[1])){
                    memSize++;
                    vars1.add(instruction[1]);
                }
            }
            memSize++;
            current1 = br1.readLine();
        }

        FileReader fr2 = new FileReader(file2);
        BufferedReader br2 = new BufferedReader(fr2);
        String current2 = br2.readLine();
        ArrayList<String> vars2 = new ArrayList<String>(fileSize(file2));
        while(current2 != null) {
            String[] instruction = current2.split(" ");
            if(instruction[0].equals("assign")) {
                if (!vars2.contains(instruction[1])) {
                    memSize++;
                    vars2.add(instruction[1]);
                }
            }
            memSize++;
            current2 = br2.readLine();
        }

        FileReader fr3 = new FileReader(file3);
        BufferedReader br3 = new BufferedReader(fr3);
        String current3 = br3.readLine();
        ArrayList<String> vars3 = new ArrayList<String>(fileSize(file3));
        while(current3 != null) {
            String[] instruction = current3.split(" ");
            if(instruction[0].equals("assign")) {
                if (!vars3.contains(instruction[1])) {
                    memSize++;
                    vars3.add(instruction[1]);
                }
            }
            memSize++;
            current3 = br3.readLine();
        }
        return memSize + 15; // instructions + variables + the 3 PCBs
    }

    public static int fileSize(String file) throws IOException {
        int size = 0;
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String current = br.readLine();
        ArrayList<String> vars = new ArrayList<String>();
        while(current != null) {
            String[] instruction = current.split(" ");
            if(instruction[0].equals("assign")) {
                if (!vars.contains(instruction[1])) {
                    size++;
                    vars.add(instruction[1]);
                }
            }
            size++;
            current = br.readLine();
        }
        return size + 5;
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

    public void addFile (String file) throws IOException {
        // determining file number
        int fileNo;
        if(getPCB(1)[0] == null){
            fileNo = 1;
        }
        else if(getPCB(2)[0] == null){
            fileNo = 2;
        }
        else{
            fileNo = 3;
        }

        // add PCB attributes
        int firstEmpty = 0;
        while(readMem(firstEmpty) != null)
            firstEmpty++;
        writeMem("P"+fileNo);
        writeMem("Status: notRunning");
        writeMem("PC: 1");

        writeMem("Min Boundary: "+firstEmpty);
        writeMem("Max Boundary: "+(firstEmpty +fileSize(file)-1));

        // add instructions
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String current = br.readLine();
        int counter = 1;
        while(current != null){
            writeMem("Instruction " + counter++ + ": " + current);
            current = br.readLine();
        }

        //add variables
        FileReader fr1 = new FileReader(file);
        BufferedReader br1 = new BufferedReader(fr1);
        String current1 = br1.readLine();
        ArrayList<String> vars = new ArrayList<String>();
        while(current1 != null) {
            String[] instruction = current1.split(" ");
            if(instruction[0].equals("assign")){
                if (!vars.contains(instruction[1])) {
                    writeMem(instruction[1] + " = ");
                    vars.add(instruction[1]);
                }
            }
            current1 = br1.readLine();
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
            if(instruction[0].contains("P"))
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


    // Milestone 2

    public static String[] getPCB (int process){
        String num = "";
        num += process;
        String[] PCB = new String[5];
        for (int i = 0; i< memory.length && memory[i] != null; i++){
            if((readMem(i).charAt(0) == 'P' && readMem(i+1).equals("Status: Running")) || (readMem(i).charAt(0) == 'P' && readMem(i+1).equals("Status: notRunning"))){
                if(readMem(i).charAt(1) == num.charAt(0)){
                    for(int j = 0; j < 5; j++)
                        PCB[j] = readMem(i+j);
                }

            }
        }
        return PCB;
    }

    public static String getStatus (int process){
        String[] PCB = getPCB(process);
        return PCB[1].substring(8);
    }

    public static int getPC (int process){
        String[] PCB = getPCB(process);
        return Integer.parseInt(PCB[2].substring(4));
    }

    public static int getMinBoundary (int process){
        String[] PCB = getPCB(process);
        return Integer.parseInt(PCB[3].substring(14));
    }

    public static int getMaxBoundary (int process){
        String[] PCB = getPCB(process);
        return Integer.parseInt(PCB[4].substring(14));
    }

    public static void setStatus (int process){
        String num = "";
        num += process;
        for (int i = 0; i< memory.length && memory[i] != null; i++){
            if((readMem(i).charAt(0) == 'P' && readMem(i+1).equals("Status: Running")) || (readMem(i).charAt(0) == 'P' && readMem(i+1).equals("Status: notRunning"))){
                if(readMem(i).charAt(1) == num.charAt(0)){
                    if(readMem(i+1).equals("Status: Running"))
                        writeMem("Status: notRunning", i+1);

                    else if(readMem(i+1).equals("Status: notRunning"))
                        writeMem("Status: Running", i+1);

                }
            }
        }
    }

    public static void incrementPC (int process){
        String num = "";
        num += process;
        for (int i = 0; i< memory.length && memory[i] != null; i++){
            if((readMem(i).charAt(0) == 'P' && readMem(i+1).equals("Running")) || (readMem(i).charAt(0) == 'P' && readMem(i+1).equals("notRunning"))){
                if(readMem(i).charAt(1) == num.charAt(0)){
                    writeMem(""+(getPC(process)+1), i+2);
                }
            }
        }
    }





    //MAIN METHOD
    public static void main(String [] args) throws IOException {
        Project pr = new Project();

//        pr.addFile("Program 1.txt");
//        pr.writeMem("P1");
//        pr.writeMem("notRunning");
//        pr.writeMem("43");
//        pr.writeMem("0");
//        pr.writeMem("2");
//        pr.addFile("Program 2.txt");
//        pr.writeMem("P2");
//        pr.writeMem("running");
//        pr.writeMem("23");
//        pr.writeMem("51");
//        pr.writeMem("7");
//
//          pr.addFile("Program 1.txt");
//          pr.addFile("Program 2.txt");
//          pr.addFile("Program 3.txt");

//
//        System.out.println("Old memory: ");
//        pr.printMem();
//
//        pr.incrementPC(1);
//        System.out.println("new memory: ");
//        pr.printMem();

//        System.out.println(pr.getStatus(1));
//        System.out.println(pr.getStatus(2));
//
//        System.out.println(pr.getPC(1));
//        System.out.println(pr.getPC(2));
//
//        System.out.println(pr.getMinBoundary(1));
//        System.out.println(pr.getMinBoundary(2));

//        System.out.println(pr.getMaxBoundary(1));
//        System.out.println(pr.getMaxBoundary(2));
//
//
//        String[] PCB1 = getPCB(6);
//        String[] PCB2 = getPCB(2);
//
//        for(int i=0; i<PCB1.length; i++)
//            System.out.println("PCB 1 " + PCB1[i]);
//
//        for(int i=0; i<PCB2.length; i++)
//            System.out.println("PCB 2 " + PCB2[i]);
        //pr.printMem();
        // pr.parser();

    }
}
