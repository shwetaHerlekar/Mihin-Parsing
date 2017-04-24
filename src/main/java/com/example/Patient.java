package com.example;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Preconditions;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;
import com.google.api.client.http.InputStreamContent;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.InputStream;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.ByteArrayInputStream;
/*class Patient{
        public String name,city,state, postal_code,bdate,gender,patient_id,all_json;
}*/

public class Patient{
		
	public static StringBuilder fileContent = new StringBuilder();
	public static String comma = ",";
	public static String new_line="\n";

	public static StorageObject uploadSimple(Storage storage, String bucketName, String objectName,
         InputStream data, String contentType) throws IOException {
        InputStreamContent mediaContent = new InputStreamContent(contentType, data);
        Storage.Objects.Insert insertObject = storage.objects().insert(bucketName, null, mediaContent).setName(objectName);
        insertObject.getMediaHttpUploader().setDisableGZipContent(true);
        return insertObject.execute();
        }

	 public static void main(String[] args) {

		try
		{		
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        		GoogleCredential credential = GoogleCredential.getApplicationDefault();
        		Storage storage = new Storage.Builder(httpTransport, jsonFactory, credential)
        		.setApplicationName("Google-ObjectsListExample/1.0").build();
         		String BUCKET_NAME = "mihin-data";
         		String objectFileName = "Patient_entry.txt";
         		Storage.Objects.Get obj = storage.objects().get(BUCKET_NAME, objectFileName);
         		HttpResponse response = obj.executeMedia();
			String file=response.parseAsString();
			//System.out.println(file);
			 //JSON Parsing of data

	     
	       JSONParser parser = new JSONParser();
                        Object obj1 = parser.parse(file);
	      
	      JSONObject jsonObject = (JSONObject) obj1;
              JSONArray resource = (JSONArray) jsonObject.get("resources");
		

		 for (int i = 0; i<resource.size(); i++) {
            	
        		JSONObject jsonObject1 = (JSONObject) parser.parse(resource.get(i).toString());
    			//System.out.println(jsonObject);
			
    			HashMap map = (HashMap) jsonObject1.get("resource");
			//p.all_json=String.valueOf(map);
			fileContent.append(String.valueOf(map));
			fileContent.append(comma);
    			JSONArray FullnameArray  = (JSONArray) map.get("name");
    		 	JSONObject nameObject  = (JSONObject) parser.parse(FullnameArray.get(0).toString());
    			JSONArray nameArray = (JSONArray)(nameObject.get("given"));
			String t="";
    			for(int j=0;j<nameArray.size();j++)
			{
				if(j==0)
					t=String.valueOf(nameArray.get(j))+" ";
				else if(j==(nameArray.size()-1))
					t+=nameArray.get(j);
				else
				t=nameArray.get(j)+" ";	
			}
    			fileContent.append(t);
			fileContent.append(comma);
    			if ( map.get("address") != null) {
    			  
       				JSONObject addressObject  = (JSONObject) parser.parse(((JSONArray) map.get("address")).get(0).toString());
        				//p.city=String.valueOf(addressObject.get("city"));
        				fileContent.append(String.valueOf(addressObject.get("city")));
                        		fileContent.append(comma);
					//p.state=String.valueOf(addressObject.get("state"));
       					 fileContent.append(String.valueOf(addressObject.get("state")));
                                        fileContent.append(comma);
					//p.postal_code=String.valueOf(addressObject.get("postalCode"));
					 fileContent.append(String.valueOf(addressObject.get("postalCode")));
                                        fileContent.append(comma);
			}
    			//p.bdate=String.valueOf(map.get("birthDate"));
			 fileContent.append(String.valueOf(map.get("birthDate")));
                         fileContent.append(comma);
    			//p.gender=String.valueOf(map.get("gender"));
			 fileContent.append(String.valueOf(map.get("gender")));
                         fileContent.append(comma);
    			//p.patient_id=String.valueOf(map.get("id"));
			 fileContent.append(String.valueOf(map.get("id")));
                         fileContent.append(new_line);
		}
		System.out.println("Parsing done");
		StorageObject writeObject = uploadSimple(storage, "mihin-data", "Patient_entry.csv", new ByteArrayInputStream(fileContent.toString().getBytes("UTF-8")), "text/csv");
	}
		catch(Exception e){
			System.out.println(e);
		}
	}


}
