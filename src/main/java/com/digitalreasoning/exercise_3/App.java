package com.digitalreasoning.exercise_3;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import com.digitalreasoning.exercise_3.FileScanner;

/**
 * @author Fei Xiao
 *
 */
class Processor implements Runnable {
    
    private int id;
    private List<Pattern> dic;
    private String fileName;
    private String xmlFileName;
    
    public Processor(int id, List<Pattern> dic) {
        this.id = id;
        this.dic= dic;

        if (id<10){
        	fileName = "temp/nlp_data/d0" + id + ".txt";
        	xmlFileName =  "temp/nlp_data/d0" + id + ".xml";
        }else{
        	fileName =  "temp/nlp_data/d" +id + ".txt";
        	xmlFileName =  "temp/nlp_data/d" +id + ".xml";
        }	
        
    }
    
    public void run() {
        System.out.println("Starting process" + fileName);
        
		FileScanner fs = new FileScanner(fileName, xmlFileName);
		
	    
	    fs.setEntityDictionary(dic);
	    fs.setXmlFileName(xmlFileName);
		
		try {
			fs.processFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
        
        System.out.println("Process " + fileName + " Completed: " + id);
    }
}


public class App {

    public static void main(String[] args) throws IOException {
    	
    	
    	String sourceFile = "nlp_data.zip";
    	String destFile = "temp";
    	
    	UnzipUtility.unzip(sourceFile, destFile);

    	String entityName = "NER.txt";
		List<Pattern> dic = new ArrayList<Pattern>();

		
		//FileScanner fs = new FileScanner(fileName);
		
	    Path pathDictionary = Paths.get(entityName);
	    Scanner scannerDictionary = new Scanner(pathDictionary);
	    scannerDictionary.useDelimiter(System.getProperty("line.separator"));
        while(scannerDictionary.hasNextLine()){
        	String line = scannerDictionary.nextLine();
            if(line==null || line.length()==0 || line.trim().length()==0)
            	continue;
        	//Pattern p  = Pattern.compile(line, Pattern.CASE_INSENSITIVE);
        	Pattern p  = Pattern.compile(line);       	
        	dic.add(p);

        }
	    scannerDictionary.close();    	
    	
        
       ExecutorService executor = Executors.newFixedThreadPool(4);
        
        for(int i=1; i<=10; i++) {
            executor.submit(new Processor(i, dic));
        }
        
        executor.shutdown();
        
        System.out.println("All tasks submitted.");
        
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
        }
        
        System.out.println("All tasks completed.");
    }
}   