package com.company;

import java.util.LinkedList;
import java.util.List;

public class Copy_GC {
    private List ToSpace = new LinkedList();
    private List roots;

    Copy_GC(List r){
        this.roots = r;
    }

    public List collect_garbage(){
        int sloc = 0 , eloc = 0;
        int ci = 0 , fo = 0;
        // traverse root and put them at list and edit his start and end
        for (int i = 0 ; i < roots.size(); i++){
            item obj = (item) roots.get(i);
            eloc = sloc + (obj.getEndLocation() - obj.getStartLocation());
            obj.setStartLocation(sloc);
            obj.setEndLocation(eloc++);
            ToSpace.add(obj);
            obj.copied();
            fo++;
            sloc = eloc;
        }
        // traverse the ToSpace list with cheney's algorithm
        int j =0;
        while ( ci != fo && j < ToSpace.size()){
            item obj = (item) ToSpace.get(j);
            List pointers = obj.getPointers();
            for (int i = 0 ; i < pointers.size() ; i++){
                item pointedObj = (item) pointers.get(i);
                if (!pointedObj.isCopied()) {
                    eloc = sloc + (pointedObj.getEndLocation() - pointedObj.getStartLocation());
                    pointedObj.setStartLocation(sloc);
                    pointedObj.setEndLocation(eloc++);
                    ToSpace.add(pointedObj);
                    pointedObj.copied();
                    fo++;
                    sloc = eloc;
                }
            }
            ci++;
            j++;
        }

        return ToSpace;
    }



}
