package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


class item{
    private String name;
    private int StartLocation;
    private int EndLocation;
    private boolean UseLess=true;
    private boolean copied=false;
    private List<item> pointers =new LinkedList<>();

    public List<item> getPointers() {
        return this.pointers;
    }

    public String getName() {
        return name;
    }

    public int getStartLocation() {
        return StartLocation;
    }

    public void setStartLocation(int startLocation) {
        StartLocation = startLocation;
    }

    public void setEndLocation(int endLocation) {
        EndLocation = endLocation;
    }

    public void setUseLess(boolean useLess) {
        UseLess = useLess;
    }

    public int getEndLocation() {
        return EndLocation;
    }

    public boolean isCopied(){
        return copied;
    }

    public void copied() {
        this.copied = true;
    }

    public boolean isUseLess() {
        return UseLess;
    }
    /**Cunstructor for any item have Name , start location in memory, end location in memory*/
    item(String name, int StartLocation, int EndLocation){
        this.name=name;
        this.EndLocation=EndLocation;
        this.StartLocation=StartLocation;
    }
    /**This function to link any item with another item**/
    public void Link(item t){
        this.pointers.add(t);
    }
    /**This function start DFS in all items begin from specific root */
    public void GoDeep(){
        if(this.pointers.size()==0){return;}
      for(int i = 0; i< pointers.size(); i++){
          if(pointers.get(i).isUseLess()){
          pointers.get(i).setUseLess(false);
          pointers.get(i).GoDeep();}
      }
    }
}
/** Garbage Collector Class which use MarkAndCombact Algorithm*/
class MarkAndCompact{
    //This HashMap contains all items in memory*/
    HashMap<String,item>items=new HashMap<>();
    //This LinkedList contains all dependencies between all items//
    List<String>pointers=new LinkedList<>();
    //This  LinkedList contains all roots in that DFS begin from it//
    List<item>roots=new LinkedList<>();
    //Linked list Contains all keys of all items that makes as able to get it from Hashmap
    List<String>ItemsKeys=new LinkedList<>();

    public MarkAndCompact() {
    }
    /*This function takes the names of 3 files to read data from */
    public void ReadFiles(String heap, String pointers, String roots) throws IOException {
        //Read heap.csv file to construct Hashmap and all item keys

        BufferedReader csvReader = new BufferedReader(new FileReader(heap));
        String row;
        while (( row = csvReader.readLine()) != null) {
            //read row from file
            String[] data = row.split(",");
            data[0]=data[0].replaceFirst("ï»¿", "");
            System.out.println();
            //Make new Item from that row
            item i=new item((data[0].trim()),Integer.parseInt(data[1]),Integer.parseInt(data[2]));
            //put that item in HashMap
            this.items.put((data[0].trim()),i);
            //store it's key in LinkedList
            this.ItemsKeys.add((data[0]));
        }
        csvReader.close();
        //read pointers.csv file to construct pointers LinkedList
        csvReader = new BufferedReader(new FileReader(pointers));
        while (( row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            data[0]=data[0].replaceFirst("ï»¿", "");
            this.pointers.add(data[0].trim());
            this.pointers.add((data[1]).trim());
        }
        // read roots.txt file
        csvReader.close();
        csvReader = new BufferedReader(new FileReader(roots));
        while (( row = csvReader.readLine()) != null) {
                this.roots.add(items.get((row)));
                items.get(row).setUseLess(false);
        }
        csvReader.close();
    }
    //Start Mark and Compact algorithm in GC function
    public void GC(){
        //Link items from pointers Linked list
        for(int i=0;i<this.pointers.size();i++){
           item pointer1= items.get(this.pointers.get(i));
           item pointer2= items.get(this.pointers.get(i+1));
           pointer1.Link(pointer2);
           i++;
        }
        // Starting DFS from each root
        for(int i=0;i<roots.size();i++){
            roots.get(i).GoDeep();
        }
        /// Start remove Items that is Useless
        for(int i=0;i<ItemsKeys.size();i++){
            if(items.get(ItemsKeys.get(i)).isUseLess()){
                items.remove(ItemsKeys.get(i));
                ItemsKeys.remove(i);
                i--;
            }
        }
        }
        /// Compute New Locations in memory for new heap
    public void NewLocations(){
        int LastIndex=-1;//last index in memory
        for(int i=0;i<ItemsKeys.size();i++){
         int itemStart=items.get(ItemsKeys.get(i)).getStartLocation();
         int itemEnd=items.get(ItemsKeys.get(i)).getEndLocation();
         items.get(ItemsKeys.get(i)).setStartLocation(LastIndex+1);
         LastIndex=LastIndex+1+(itemEnd-itemStart);
         items.get(ItemsKeys.get(i)).setEndLocation(LastIndex);
        }
    }
    /// Generate file
    public void GenerateFile(String result) throws IOException {
        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(result+"/Mark&Compact.csv");
        }catch (Exception e){
            csvWriter=new FileWriter("Mark&Compact.csv");
        }

        for(int i=0;i<ItemsKeys.size();i++){
           item CurrentItem =items.get(ItemsKeys.get(i));
           csvWriter.append((CurrentItem.getName()));
           csvWriter.append(",");
           csvWriter.append(Integer.toString(CurrentItem.getStartLocation()));
           csvWriter.append(",");
           csvWriter.append(Integer.toString(CurrentItem.getEndLocation()));
           csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }
    public void RunAll(String heap,String pointers,String roots,String result) throws IOException {
        this.ReadFiles(heap,pointers,roots);
        this.GC();
        this.NewLocations();
        this.GenerateFile(result);
    }
    }

public class Main {
    public static void main(String[] args) throws IOException {

            MarkAndCompact m=new MarkAndCompact();
            if(args.length==3){m.RunAll(args[0],args[2],args[1],"");}
            else {
            m.RunAll(args[0],args[2],args[1],args[3]);}



    }
}
class Main2{
    public static void readFiles(String heapFile, String pointersFile, String rootsFile, HashMap heapObjs, List roots) throws IOException {
        // read the heap csv file and store them in hash
        BufferedReader csvReader = new BufferedReader(new FileReader(heapFile));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] data = row.split(",");
            data[0]=data[0].replaceFirst("ï»¿", "");
           // if (data[0].length() > 6){ data[0] = data[0].substring(1); }
            item obj = new item(data[0],Integer.parseInt(data[1]) ,Integer.parseInt(data[2]));
            heapObjs.put(obj.getName(),obj);
        }
        csvReader.close();

        // traverse the pointers csv file and edit the pointers of each item
        BufferedReader csvReader2 = new BufferedReader(new FileReader(pointersFile));
        while ((row = csvReader2.readLine()) != null) {
            String[] data = row.split((","));
            data[0]=data[0].replaceFirst("ï»¿", "");
           // if (data[0].length() > 6){ data[0] = data[0].substring(1); }
            item obj = (item) heapObjs.get(data[0]);
            if (data[1].length() > 6){ data[1] = data[1].substring(1); }
            obj.Link( (item) heapObjs.get(data[1]) );
        }
        csvReader2.close();

        // read the roots txt file and store them in a root list
        BufferedReader reader = new BufferedReader(new FileReader(rootsFile));
        while ((row = reader.readLine()) != null) {
           // if (row.length() > 6){ row = row.substring(1); }
            item obj = (item) heapObjs.get(row);
            roots.add(obj);
        }
        reader.close();
    }

    public static void makeFile(String resultFile, List new_heap) throws IOException {
        // write the new heap in Copy_new-heap csv file
        FileWriter csvWriter ;
        try {
            csvWriter= new FileWriter(resultFile+"/Copy_new-heap.csv");
        }
        catch (Exception e){
            csvWriter= new FileWriter("Copy_new-heap.csv");
        }   for (int i = 0 ; i < new_heap.size() ; i++) {
            item obj = (item) new_heap.get(i);
            csvWriter.append(obj.getName());
            csvWriter.append(",");
            csvWriter.append(Integer.toString(obj.getStartLocation()));
            csvWriter.append(",");
            csvWriter.append(Integer.toString(obj.getEndLocation()));
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }
    public static void  main(String[]args)throws IOException{
        HashMap heapObjs = new HashMap();

        List roots = new ArrayList<item>();
        readFiles(args[0],args[2],args[1],heapObjs,roots);
        ///////// call the copy_gc and pass the roots
        Copy_GC CGC = new Copy_GC(roots);
        List Copy_new_heap = CGC.collect_garbage();
        if(args.length==3){makeFile("",Copy_new_heap);}
        else {
        makeFile(args[3],Copy_new_heap);}
    }
}
