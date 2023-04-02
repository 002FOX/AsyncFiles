package com.example.demo;

import java.util.List;

public class Response {
    
    private List<String> files;
    private List<String> directories;
    private int length;

    public Response(int length, List<String> files, List<String> directories){
        this.length = length;
        this.files = files;
        this.directories = directories;
    }

    public List<String> getFiles(){
        return files;
    }

    public List<String> getDirectories(){
        return directories;
    }
    
    public int getLength(){
        return length;
    }

    public void addFile(String filePath){
        files.add(filePath);
        length++;
    }

    public void addDirectory(String dirPath){
        directories.add(dirPath);
        length++;
    }
    
}
