package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.core.io.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/application")
public class Application {

	// get all the files of the shared folder
    @GetMapping("/files")
    public ResponseEntity<Response> getFiles() {
        try{
            final File directory = new File("ServerShare");
            List<String> allFiles = new ArrayList<String>();
            List<String> allDirectories = new ArrayList<String>();
            findFiles(directory, allFiles, allDirectories);
            Response res = new Response(allFiles.size() + allDirectories.size(), allFiles, allDirectories);
            return ResponseEntity.ok(res);
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(0, null, null));
        }
    }

    // get all the files of the shared folder
    @GetMapping("/files/{dir_name}")
    public ResponseEntity<Response> getDirectory(@PathVariable String dir_name) {
        try{
            final File directory = new File("ServerShare");
            String filePath = getPath(directory, dir_name);
            File file = new File(filePath);
            List<String> allFiles = new ArrayList<String>();
            List<String> allDirectories = new ArrayList<String>();
            findFiles(file, allFiles, allDirectories);
            Response res = new Response(allFiles.size() + allDirectories.size(), allFiles, allDirectories);
            return ResponseEntity.ok(res);
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(0, null, null));
        }
    }

	// download a file
    @GetMapping("/files/{file_name}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable String file_name) {
        try{
            final File directory = new File("ServerShare");
            String filePath = getPath(directory, file_name);
            File file = new File(filePath);
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(Paths.get(filePath)));
            return ResponseEntity.ok()
                                 .contentLength(file.length())
                                 .body(resource);
        }catch(Exception ex){
            System.out.println(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

	// upload a file 
    @PostMapping("/files/{dir_name}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable String dir_name, @RequestParam("file") MultipartFile file) {
        try {
            File server = new File("ServerShare");
            String directoryPath = null;
            if(dir_name.equals("ServerShare")){
                directoryPath = dir_name;
            }
            else{
                directoryPath = getPath(server, dir_name);
            }

            final File directory = new File(directoryPath);
            byte[] fileContent = file.getBytes();

            if (directory.exists() && directory.isDirectory()) {
                File uploadedFile = new File(directory, file.getOriginalFilename());
                FileOutputStream outputStream = new FileOutputStream(uploadedFile);
                outputStream.write(fileContent);
                outputStream.close();
                return ResponseEntity.ok("Successfully uploaded!");
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

	// rename a file or a subfolder
    @PatchMapping("/files/{file_name}/rename")
    public ResponseEntity<String> renameFile(@PathVariable String file_name, @RequestParam String new_name) {
        try {
            final File directory = new File("ServerShare");
            String filePath = getPath(directory, file_name);
            File file = new File(filePath);
            String ext = file_name.substring(file_name.lastIndexOf('.') + 1);
            File newFile = null;

            if(file.isDirectory()){
                newFile = new File(file.getParent(), new_name);
            } else {
                newFile = new File(file.getParent(), new_name + "." + ext);
            }

            if(newFile.exists()){
                return ResponseEntity.status(400).body("File name already exists!");
            }
            file.renameTo(newFile);
            return ResponseEntity.ok("Successfully renamed the file!");

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server error!");
        }
    }

	// delete a file
    @DeleteMapping("/files/{file_name}/delete")
    public ResponseEntity<String> deleteFile(@PathVariable String file_name) {
        try {

            final File directory = new File("ServerShare");
            String filePath = getPath(directory, file_name);

            if(filePath == null){
                return ResponseEntity.status(400).body("File does not exist!");
            }

            File file = new File(filePath);
            if(file.isDirectory()){
                return ResponseEntity.status(400).body("This is not a file!");
            }
            
            file.delete();
            return ResponseEntity.ok().body("Deleted file successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

    public void findFiles(final File dir, List<String> allFiles, List<String> allDirectories){
        for (final File file : dir.listFiles()){
            if (file.isDirectory()){
                allDirectories.add(file.getName());
                findFiles(file, allFiles, allDirectories);
            } else {
                allFiles.add(file.getName());
            }
        }
    }

    public String getPath(File directory, String fileName) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if ((file.isFile() || file.isDirectory()) && file.getName().equals(fileName)) {
                    return file.getPath();
                } else if (file.isDirectory()) {
                    String filePath = getPath(file, fileName);
                    if (filePath != null) {
                        return filePath;
                    }
                }
            }
        }
        return null;
    }
    

}
