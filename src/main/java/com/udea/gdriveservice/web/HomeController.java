package com.udea.gdriveservice.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.udea.gdriveservice.adapter.GoogleDriveService;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String home(Principal principal,Model model) throws Exception {
        Drive service = GoogleDriveService.get(principal);
		File result1 = GoogleDriveService.searchFile("Invoices", null, service);
		String id = null;

		if(result1 == null){
			File fileMetadata = new File();
			fileMetadata.setName("Invoices");
			fileMetadata.setMimeType("application/vnd.google-apps.folder");
			service.files().list();

			File file = service.files().create(fileMetadata)
					.setFields("id")
					.execute();
			id = file.getId();
		}
		else id = result1.getId();

		String fileQuery = "'" + id + "' in parents and trashed=false";
        FileList result = service.files().list().setQ( fileQuery ).setPageSize(50).setFields("nextPageToken, files(id, name)").execute();
        List<File> files = result.getFiles();
        model.addAttribute("fileNames",this.toStringList(files));
		return "home";
	}

	@RequestMapping("/loginroot")
	public String login(){
		return "login";
	}

	private List<String> toStringList(List<File> files){
		List<String> fileNames = new ArrayList<>();
		if(files!=null){
			for(File file:files){
				fileNames.add(file.getName());
			}
		}
		return fileNames;
	}



}
