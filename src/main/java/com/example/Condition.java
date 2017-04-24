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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.ByteArrayInputStream;

public class Condition{


	public static StringBuilder fileContent=new StringBuilder();
	public static String comma=",";
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
         		String objectFileName = "Condition_entry.txt";
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
    			JSONObject cond  = (JSONObject) map.get("code");
			if(cond!=null)
			{
				JSONArray coding  = (JSONArray) cond.get("coding");
				if(coding!=null)
				{
					JSONObject code1  = (JSONObject) parser.parse(coding.get(0).toString());
					if(code1!=null)
					{
						String s = String.valueOf(code1.get("display"));
						String[] temp=s.split(",");
						String t="";
						for(int k=0; k<temp.length;k++)
						{
							if(k==temp.length-1)
								t+=temp[k];
							else
								t+=temp[k]+" ";
						}
						fileContent.append(t);
						fileContent.append(comma);
					}
				}
				
				fileContent.append(String.valueOf(cond.get("text")));
				fileContent.append(comma);
			}
			//System.out.println(c.condition);
			
			fileContent.append(String.valueOf(map.get("id")));
			fileContent.append(comma);	
			JSONObject cond1 = (JSONObject) map.get("patient");
			if(cond1!=null)
			{
			String t =String.valueOf(cond1.get("reference"));	
			
			fileContent.append(t.substring(8,t.length()));
			fileContent.append(comma);
			}
			JSONObject cond2  = (JSONObject) map.get("severity");
			if(cond2!=null)
			{
                        	JSONArray coding  = (JSONArray) cond2.get("coding");
				if(coding!=null)
				{
                       			JSONObject code1  = (JSONObject) parser.parse(coding.get(0).toString());
					if(code1!=null)
					fileContent.append(String.valueOf(code1.get("display")));
					fileContent.append(new_line);
                        		
				}
			}
		
			
		}
		System.out.println("parsing done");
		StorageObject writeObject = uploadSimple(storage, "mihin-data", "Condition_entry.csv", new ByteArrayInputStream(fileContent.toString().getBytes("UTF-8")), "text/csv");
		//System.out.println("Object written successfully");
	}
		catch(Exception e){
			e.printStackTrace();
		}
	}


}
