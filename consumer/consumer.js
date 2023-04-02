var prompt = require('prompt');
const { getFiles, deleteFile, downloadFile, renameFile, uploadFile } = require('./functions.js');

    prompt.start();
    console.log("\n------ MENU ------\n");
    console.log("1. Get list of files\n");
    console.log("2. Rename a file\n");
    console.log("3. Delete a file\n");
    console.log("4. Download a file\n");
    console.log("5. Upload a file\n");
    console.log("6. Exit");

    const schema = {
        properties: {
          option: {
            type: 'number',
          }
        }
      };
    prompt.get(schema).then( res => {
        switch (res.option) {
            case 1:
                getFiles();
                break;
            case 2:
                renameFile();
                break;
            case 3:
                deleteFile(); 
                break;
            case 4:
                downloadFile();
                break;
            case 5:
                uploadFile();
                break;
            case 6:
                process.exit(0);
                break;
            default:
                console.log("Invalid option. Please try again.");
        }
    } ).catch(error => {
        console.log(error);
    })

    
