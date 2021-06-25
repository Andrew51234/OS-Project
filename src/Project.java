import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Project {

    private static String[] memory;

    int clk;
    ArrayList<String[]> queue;
    int[] instructionSizes = new int[]{0,0,0};

    public Project () throws IOException {
        memory = new String[initMemory("Program 1.txt", "Program 2.txt", "Program 3.txt")];
        addFile("Program 1.txt");
        addFile("Program 2.txt");
        addFile("Program 3.txt");
        clk = 0;
        queue = new ArrayList<>(instructionNumber("Program 1.txt") +
                instructionNumber("Program 2.txt") + instructionNumber("Program 3.txt"));

    }

    public int initMemory (String file1, String file2, String file3) throws IOException {
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

            this.instructionSizes[0]++;
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

            this.instructionSizes[1]++;
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

            this.instructionSizes[2]++;
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
            if(memory[i] == null){
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
                writeMem(" ");
                vars.add(instruction[1]);
            }
            current1 = br1.readLine();
        }
    }

    public void print (String value){
        if(variableInMem(value)){
            System.out.println("");
            System.out.println(getValue(value));
        }
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

    public static void setStatus (int process, boolean state){
        String num = "";
        num += process;
        for (int i = 0; i< memory.length && memory[i] != null; i++){
            if((readMem(i).charAt(0) == 'P' && readMem(i+1).equals("Status: Running")) || (readMem(i).charAt(0) == 'P' && readMem(i+1).equals("Status: notRunning"))){
                if(readMem(i).charAt(1) == num.charAt(0)){

                    if(state){
                        writeMem("Status: Running", i+1);
                    }else{
                        writeMem("Status: notRunning", i+1);
                    }
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

    public void reArrangeQueue() throws IOException {
        int p1InsNo = this.instructionSizes[0];
        int p2InsNo = this.instructionSizes[1];
        int p3InsNo = this.instructionSizes[2];

        ArrayList<String[]> sortedQueue = new ArrayList<>(this.queue.size());

        int j = 0;
        int k = p1InsNo;
        int l = p1InsNo + p2InsNo;

        int[] counters = new int[]{0,0,0};

        for(int i = 0; i < (Math.max(Math.max(p1InsNo, p2InsNo), p3InsNo))/2; i++){

            while(j < p1InsNo && counters[0] < 2){
                sortedQueue.add(this.queue.get(j));
                counters[0]++;
                j++;
            }
            counters[0] = 0;

            while(k < (p1InsNo + p2InsNo) && counters[1] < 2){
                sortedQueue.add(this.queue.get(k));
                k++;
                counters[1]++;
            }
            counters[1] = 0;

            while(l < this.queue.size() && counters[2] < 2){
                sortedQueue.add(this.queue.get(l));
                l++;
                counters[2]++;
            }
            counters[2] = 0;
        }
        this.queue = sortedQueue;
    }

    /* 0 = assign ----> input
    0.5 = assign ----> readFile
    1 = print
    2 = readfile
    3 = writeFile
    4 = add
    */

    public void schedulerRR() throws IOException {
        int programNo = 0;

        for(int i = 0; i < memory.length && memory[i] != null; i++){

            String[] instruction = readMem(i).split(" ");

            if(instruction.length == 0)
                continue;

            if(instruction[0].charAt(0) == 'P' && instruction[0].length() == 2){
                programNo = Integer.parseInt("" + instruction[0].charAt(1));
            }

            if(!instruction[0].equals("Instruction"))
                continue;

            switch(instruction[2]){
                case "assign":
                    if(instruction[2+2].equals("input")){
                        this.queue.add(new String[]{"0", instruction[2+1], ""+programNo, ""+i}); break;
                    }
                    if(instruction[2+2].equals("readFile")){
                        this.queue.add(new String[]{"0.5", instruction[2+1], instruction[2+3], ""+programNo, ""+i}); break;
                    }


                case "print":
                    this.queue.add(new String[]{"1", instruction[2+1], ""+programNo, ""+i}); break;

                case "readFile":
                    this.queue.add(new String[]{"2", getValue(instruction[2 + 1]), ""+programNo, ""+i}); break;

                case "writeFile":
                    this.queue.add(new String[]{"3", instruction[2+1], instruction[2+2], ""+programNo, ""+i}); break;

                case "add": this.queue.add(new String[]{"4", instruction[2+1],instruction[2+2], ""+programNo, ""+i}); break;
            }
        }

        this.reArrangeQueue();
    }

    public void dequeue() throws IOException {

        int counter = 1;
        int p1Quanta = 0;
        int p2Quanta = 0;
        int p3Quanta = 0;

        boolean p1Done = false;
        boolean p2Done = false;
        boolean p3Done = false;

        System.out.println("--------------------------------------------------------------");
        System.out.println("Going To First Slice; Slice No. " + ((this.clk /2) + 1));
        System.out.println("--------------------------------------------------------------");

        while(!this.queue.isEmpty()){

            System.out.println("Current CLK is: " + this.clk);
            String[] instruction = this.queue.get(0);
            this.queue.remove(0);
            int index = Integer.parseInt(instruction[instruction.length - 1]);

            System.out.println("Index No.: " + index + " -------> " + readMem(index));
            System.out.println("Process ID: " + getPCB(Integer.parseInt(instruction[instruction.length - 2]))[0].charAt(1));

            if(Integer.parseInt(instruction[instruction.length - 2]) == 1){
                p1Quanta++;
            }
            if(Integer.parseInt(instruction[instruction.length - 2]) == 2){
                p2Quanta++;
            }
            if(Integer.parseInt(instruction[instruction.length - 2]) == 3){
                p3Quanta++;
            }

            int current = Integer.parseInt(instruction[instruction.length - 2]);

            if(current == 1){
                incrementPC(current);
                setStatus(current, true);
                setStatus(2, true);
                setStatus(3, true);
            }else if(current == 2) {
                incrementPC(current);
                setStatus(current, true);
                setStatus(1, true);
                setStatus(3, true);
            }else{
                incrementPC(current);
                setStatus(current, true);
                setStatus(1, true);
                setStatus(2, true);
            }

            switch(instruction[0]){
                case "0":
                    assign2(instruction[1], input(), Integer.parseInt(instruction[2]));
                    break;

                case "0.5":
                    index = indexOf2(instruction[1], Integer.parseInt(instruction[3]));
                    if(index == -1)
                        index = indexOf2(" ", Integer.parseInt(instruction[3]));
                    assign2(instruction[1], readFile(getValue(instruction[2])), Integer.parseInt(instruction[3])); break;

                case "1":
                    print2(instruction[1], Integer.parseInt(instruction[2])); break;

                case "2":
                    readFile2(instruction[1], Integer.parseInt(instruction[2])); break;

                case "3":
                    writeFile(getValue2(instruction[1], Integer.parseInt(instruction[3])),
                            getValue2(instruction[2], Integer.parseInt(instruction[3]))); break;

                case "4":
                    add2(instruction[1], instruction[2], Integer.parseInt(instruction[3])); break;
            }

            if(p1Quanta == this.instructionSizes[0] && !p1Done){
                System.out.println("************************************");
                System.out.println("Program 1 with ID of "+ getPCB(current)[0].substring(1)
                        +" has ended. It took " + (this.clk + 1 ) + " clk cycles and " + p1Quanta +" quantas");
                System.out.println("************************************");
                p1Done = true;
            }
            if(p2Quanta == this.instructionSizes[1] && !p2Done){
                System.out.println("************************************");
                System.out.println("Program 2 with ID of "+ getPCB(current)[0].substring(1)
                        +" has ended. It took " + (this.clk + 1 ) + " clk cycles and " + p2Quanta +" quantas");
                System.out.println("************************************");
                p2Done = true;
            }
            if(p3Quanta == this.instructionSizes[2] && !p3Done){
                System.out.println("************************************");
                System.out.println("Program 3 with ID of "+ getPCB(current)[0].substring(1)
                        +" has ended. It took " + (this.clk + 1 ) + " clk cycles and " + p3Quanta +" quantas");
                System.out.println("************************************");
                p3Done = true;
            }

            if(counter == 2 && !this.queue.isEmpty()){
                counter = 0;
                System.out.println("--------------------------------------------------------------");
                System.out.println("Going To Next Slice; Slice No. " + ((this.clk /2) + 2));
                System.out.println("--------------------------------------------------------------");
            }else if(this.queue.isEmpty()){
                System.out.println("--------------------------------------------------------------");
                System.out.println("Finished.");
                System.out.println("--------------------------------------------------------------");
            }else{
                System.out.println("--------------------");
            }
            counter++;
            this.clk++;

        }
    }

    public void assign2(String variable, Object value, int process){
        int index = indexOf2(variable, process);

        if(index > -1){
            writeMem(variable+" = "+value, index);
        }
        else{
            index = indexOf2(" ", process);
            writeMem(variable + " = " + value, index);
        }
        System.out.println("Output: " + readMem(index));
    }

    public int indexOf2 (String variable, int process){

        int lowerBound = getMinBoundary(process);
        int upperBound = getMaxBoundary(process);

        while(lowerBound <= upperBound){

            String[] curr = memory[lowerBound].split(" = ");
            if(curr[0].equals(variable)){
                return lowerBound;
            }
            lowerBound++;
        }
        return -1;
    }
    public void add2 (String x, String y, int process){
        double result = Double.parseDouble(getValue2(x, process)) + Double.parseDouble(getValue2(y, process));
        writeMem(x + " = " + result, indexOf2(x, process));
    }
    public void print2 (String value, int process){
        int index = indexOf2(value, process);
        if(index > 1){
            System.out.println("Output: " + getValue2(value, process));
        }
        else
            System.out.println("Output: " + value);
    }
    public String readFile2 (String f, int process) throws IOException {
        int index = indexOf2(f, process);

        System.out.println("Index No.: " + index + " ReadFile "+ f);
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
    public String getValue2 (String variable, int process){

        int lowerBound = getMinBoundary(process);
        int upperBound = getMaxBoundary(process);

        while(lowerBound <= upperBound){
            String[] curr = memory[lowerBound].split(" = ");
            if(curr[0].equals(variable))
                return curr[1];
            lowerBound++;
        }
        return null;
    }

    public static int instructionNumber(String file) throws IOException {
        int size = 0;
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String current = br.readLine();
        while(current != null) {
            size++;
            current = br.readLine();
        }
        return size;
    }

    public void printQueue(){

        System.out.print("{");
        for(int i = 0; i < this.queue.size(); i++){
            System.out.print("[");
            for(int j = 0; j < this.queue.get(i).length; j++){
                System.out.print(this.queue.get(i)[j]);
                if((j + 1) < this.queue.get(i).length){
                    System.out.print(", ");
                }
            }
            System.out.print("]");
            if((i + 1) < this.queue.size()){
                System.out.print(", ");
            }
        }
        System.out.print("}");
        System.out.println();
    }



    //MAIN METHOD
    public static void main(String [] args) throws IOException {
        Project pr = new Project();
        //System.out.println(instructionNumber("Program 1.txt"));
        pr.printMem();
        pr.schedulerRR();
        pr.dequeue();
        //System.out.println(pr.instructionSizes[0] + " " + pr.instructionSizes[1] + " " + pr.instructionSizes[2]);
        //pr.printQueue();
        //pr.printMem();




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
