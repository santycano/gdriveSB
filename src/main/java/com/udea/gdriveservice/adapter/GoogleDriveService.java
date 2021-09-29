package com.udea.gdriveservice.adapter;

import java.security.Principal;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

public class GoogleDriveService {

	private static final String APPLICATION_NAME = "Drive API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport HTTP_TRANSPORT;

    public static Drive get(Principal principal){
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
        credential.setAccessToken(GoogleDriveService.getAccessToken(principal));
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

	static private String getAccessToken(Principal principal){
		OAuth2Authentication authentication = (OAuth2Authentication)principal;
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails)authentication.getDetails();
		return details.getTokenValue();
	}
    public static File searchFile(String fileName, String parentId, Drive driveService) throws Exception {
        File file = null;
        StringBuffer fileQuery = new StringBuffer();
        fileQuery.append( "name = '" + fileName + "'" );
        if ( parentId != null ) {
            fileQuery.append( " and '" + parentId + "' in parents and trashed=false" );
        }
        FileList fileList = driveService.files().list().setQ( fileQuery.toString() ).execute();
        if ( !fileList.getFiles().isEmpty() ) {
            file = fileList.getFiles().get( 0 );
        }
        return file;
    }
}
