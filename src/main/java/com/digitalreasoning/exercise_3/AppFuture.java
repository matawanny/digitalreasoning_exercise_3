package com.digitalreasoning.exercise_3;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.digitalreasoning.nlf.ObjectFactory;
import com.digitalreasoning.nlf.Sentenceset;
import com.digitalreasoning.nlf.TSentence;

/**
 * @author Fei Xiao
 *
 */
class CallableProcessor implements Callable<Integer> {
    
    private int id;
    private List<Pattern> dic;
    private String fileName;
    private String xmlFileName;
    
    public CallableProcessor(int id, List<Pattern> dic) {
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
    
    public Integer call() throws Exception {
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
        
        return id;
    }
}


public class AppFuture {

    public static void main(String[] args) throws IOException, JAXBException {
    	
    	
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
       
       List<Future> ls = new ArrayList<Future>();
        
        for(int i=1; i<=10; i++) {
        	Future<Integer> future = executor.submit(new CallableProcessor(i, dic));
        	ls.add(future);
        }
        
        executor.shutdown();
        
        System.out.println("All tasks submitted.");
        
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
	    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
	    String xmlFileName = null;
		Sentenceset merged = new Sentenceset();
        try {
        	for(Future future: ls){
        		Integer result = (Integer)future.get(); 
                System.out.println("Result is: " + result);
                
                if (result<10)
                	xmlFileName =  "temp/nlp_data/d0" + result + ".xml";
                else
                	xmlFileName =  "temp/nlp_data/d" + result + ".xml";
   		        
                Sentenceset sentenceset = (Sentenceset) unmarshaller.unmarshal(new File(xmlFileName));
   		    
	   		    List<TSentence> sentences = sentenceset.getSentence();
	   		    if (sentences!=null && sentences.size()>0){
	   	   		    merged.getSentence().addAll(sentences);
	   		    }
        	}
        	
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }
        
        Marshaller marshaller = jaxbContext.createMarshaller();
	    marshaller.marshal(merged, new File("nlp_data.xml"));
        
        System.out.println("All tasks completed.");
    }
}   