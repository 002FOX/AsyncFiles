var prompt = require('prompt');
var fs = require('fs');
var FormData = require('form-data');
var axios = require('axios');
const { Console } = require('console');


const getFiles = async () => {

    try {

        prompt.start()
        console.log("1. See all files and directories of the server\n2. See all files and directories within a directory");
        const { input } = await prompt.get(['input']);
        if (input == "1") {
            response = await (await fetch("http://localhost:8080/application/files")).json();
        } else if (input == "2") {
            console.log("Enter the directory you want to explore\n");
            const { directory } = await prompt.get(['directory']);
            response = await (await fetch(`http://localhost:8080/application/files/${directory}`)).json();
        } else {
            console.log("Wrong input");
            return
        }
        if (response.length > 0) {
            console.log(`All the files in this directory are: ${response.files.length ? response.files : "No files!"}\nAll the directories in this directory are: ${response.directories.length ? response.directories : "No directories!"}`);
        } else {
            console.log("This directory is empty!");
        }
        prompt.stop();

    } catch (error) {
        console.log(error)
    }
}

const renameFile = async () => {

    try {
        
        prompt.start();
        console.log("Enter the old name of the file (with extension) followed by the new name (without the extension)")
        const { old_name, new_name } = await prompt.get(['old_name', 'new_name']);
        response = await (await fetch(`http://localhost:8080/application/files/${old_name}/rename?new_name=${new_name}`, { method: "PATCH" })).text();
        console.log(response);
        prompt.stop();

    } catch (error) {
        console.log(error)
    }
}

const deleteFile = async () => {

    try {

        prompt.start();
        const { file_to_delete } = await prompt.get(['file_to_delete']);
        response = await (await fetch(`http://localhost:8080/application/files/${file_to_delete}/delete`, { method: "DELETE" })).text();
        console.log(response);
        prompt.stop();

    } catch (error) {
        console.log(error)
    }
}

const downloadFile = async () => {

    try {

        prompt.start();
        console.log("Enter the full name of the file to download (with extension)\n");
        const { file_to_download } = await prompt.get(['file_to_download']);
        response = await fetch(`http://localhost:8080/application/files/${file_to_download}/download`);
        if(response.ok){
        const blob = await response.blob();
        const buffer = await blob.arrayBuffer();
        fs.writeFileSync(`./ClientShare/${file_to_download}`, Buffer.from(buffer));
        console.log(`File ${file_to_download} downloaded`);
        } else {
            console.log("Error!")
        }
        prompt.stop();

    } catch (error) {
        console.log(error)
    }
}

const uploadFile = async () => {

    try {

        prompt.start();
        console.log("Enter the directory in where you want to upload the file on the server, and the path of the file within ClientShare:\nIf you want to upload to the main directory, enter ServerShare\n");
        const { directory, filePath } = await prompt.get(['directory', 'filePath']);
        var formdata = new FormData();
        formdata.append("file", fs.createReadStream(`./ClientShare/${filePath}`));
        
        let config = {
            method: 'post',
            maxBodyLength: Infinity,
            url: `http://localhost:8080/application/files/${directory}/upload`,
            headers: { 
              ...formdata.getHeaders()
            },
            data : formdata
        };

        response = await axios.request(config);
        console.log(response.data)
        prompt.stop();

    } catch (error) {
        console.log(error)
    }
}


module.exports = { getFiles, deleteFile, downloadFile, renameFile, uploadFile };