package com.udea.gdriveservice.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.udea.gdriveservice.adapter.GoogleDriveService;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

@Controller
public class UploadController {

    private java.io.File convert(MultipartFile file) throws IOException {
    	java.io.File convFile = new java.io.File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

	@RequestMapping(value = "/upload", method=RequestMethod.POST)
	public  String uploadMultipleFiles(Principal principal,@RequestParam("file") MultipartFile[] files) throws Exception {
        Drive driveService = GoogleDriveService.get(principal);
        String folderId = null;

        File result = GoogleDriveService.searchFile("Invoices", null, driveService);

        folderId = result.getId();

        for(MultipartFile multipartFile:files){
        	File fileMetadata1 = new File();
            fileMetadata1.setName(multipartFile.getOriginalFilename());
            fileMetadata1.setParents(Collections.singletonList(folderId));
            java.io.File file1 = this.convert(multipartFile);
        	FileContent mediaContent = new FileContent("image/*", this.convert(multipartFile));
        	driveService.files().create(fileMetadata1, mediaContent).setFields("id, parents").execute();
        	file1.delete();
        }
        return "redirect:/";
	}

}
