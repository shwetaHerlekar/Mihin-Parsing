package com.example;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Preconditions;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileWriter;
class Patient{
        public String name,city,state, postal_code,bdate,gender,patient_id,all_json;
}

public class Demo{

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
		
		Patient[] patients = new Patient[resource.size()];


		 for (int i = 0; i<resource.size(); i++) {
            	
        			Patient p = new Patient();
                		JSONObject jsonObject1 = (JSONObject) parser.parse(resource.get(i).toString());
    			//System.out.println(jsonObject);
			
    			HashMap map = (HashMap) jsonObject1.get("resource");
			p.all_json=String.valueOf(map);
    			JSONArray FullnameArray  = (JSONArray) map.get("name");
    		 	JSONObject nameObject  = (JSONObject) parser.parse(FullnameArray.get(0).toString());
    			JSONArray nameArray = (JSONArray)(nameObject.get("given"));
    			for(int j=0;j<nameArray.size();j++)
			{
				if(j==0)
					p.name=String.valueOf(nameArray.get(j))+" ";
				else if(j==(nameArray.size()-1))
					p.name+=nameArray.get(j);
				else
				p.name+=nameArray.get(j)+" ";	
			}
    			
    			if ( map.get("address") != null) {
    			  
       				JSONObject addressObject  = (JSONObject) parser.parse(((JSONArray) map.get("address")).get(0).toString());
        				p.city=String.valueOf(addressObject.get("city"));
        				p.state=String.valueOf(addressObject.get("state"));
       				p.postal_code=String.valueOf(addressObject.get("postalCode"));
			}
    			p.bdate=String.valueOf(map.get("birthDate"));
    			p.gender=String.valueOf(map.get("gender"));
    			p.patient_id=String.valueOf(map.get("id"));
			patients[i]=p;
		}
		/*for(int i=0;i<patients.length;i++)
		{
			System.out.println(patients[i].name);
			System.out.println(patients[i].city);
			System.out.println(patients[i].state);
			System.out.println(patients[i].postal_code);
			System.out.println(patients[i].bdate);
			System.out.println(patients[i].gender);
			System.out.println(patients[i].patient_id);
			System.out.println(patients[i].all_json);
		}*/

		String COMMA_DELIMITER = ",";
	        String NEW_LINE_SEPARATOR = "\n";
		String fileName="Patient_entry.csv";
		FileWriter fileWriter = null;
		fileWriter = new FileWriter(fileName);
		for(int i=0;i<patients.length;i++)
                {
                        fileWriter.append(patients[i].name);
			fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(patients[i].city);
			fileWriter.append(COMMA_DELIMITER);
                       	fileWriter.append(patients[i].state);
			fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(patients[i].postal_code);
			fileWriter.append(COMMA_DELIMITER);
                     	fileWriter.append(patients[i].bdate);
			fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(patients[i].gender);
			fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(patients[i].patient_id);
			fileWriter.append(COMMA_DELIMITER);
                        fileWriter.append(patients[i].all_json);
			fileWriter.append(NEW_LINE_SEPARATOR);
                }	
		fileWriter.flush();
		fileWriter.close();
	}
		catch(Exception e){
			System.out.println(e);
		}
	}


}
