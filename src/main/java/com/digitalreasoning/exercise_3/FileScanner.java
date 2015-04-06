/**
 * 
 */
package com.digitalreasoning.exercise_3;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.digitalreasoning.nlf.FieldType;
import com.digitalreasoning.nlf.ObjectFactory;
import com.digitalreasoning.nlf.Sentenceset;
import com.digitalreasoning.nlf.TField;
import com.digitalreasoning.nlf.TSentence;

/**
 * @author Fei Xiao
 *
 */
public class FileScanner {
	
	
    private StringBuilder sb = new StringBuilder();
    private String fileName;
    private String xmlFileName;
    private List<Pattern> entityDictionary; 

    
   

	public FileScanner(String fileName, String xmlFileName) {
		super();
		this.fileName = fileName;
		this.xmlFileName= xmlFileName;
	}
	

	
	public void setEntityDictionary(List<Pattern> entityDictionary) {
		this.entityDictionary = entityDictionary;
	}


	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}



	public void processFile() throws IOException, JAXBException{
		
		
		   JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		   Sentenceset sentenceSet = new Sentenceset();
		   
	   

		   
	       Path path = Paths.get(fileName);
	        Scanner scanner = new Scanner(path);
	        scanner.useDelimiter(System.getProperty("line.separator"));
	        while(scanner.hasNextLine()){
	        	String line = scanner.nextLine();
	            //System.out.println("Lines: "+ line);
	            TSentence sentence =  parseLine(line);
	            if(sentence!=null)
	            	sentenceSet.getSentence().add(sentence);
	        }
	        scanner.close();
	        Marshaller marshaller = jaxbContext.createMarshaller();
		    marshaller.marshal(sentenceSet, new File(xmlFileName));

	}


	
    private  TSentence parseLine(String line) {
    	
        if(line==null || line.length()==0 || line.trim().length()==0)
        	return null;
 
        System.out.println(line);
    	List<String> words = new ArrayList<String>();
        List<String> punctuations = new ArrayList<String>();
        Map<String, Integer> wordSet = new HashMap<String, Integer>();
        Map<String, Integer> punctuationSet = new HashMap<String, Integer>();
        List<TField> matchedEntitySet = new ArrayList<TField>();
        Integer totalSpace = new Integer(0);
 
        
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter("\\s* \\s*");
        List<String> ls = new ArrayList<String>();
        while(scanner.hasNext()){
        	totalSpace++;
            String field = scanner.next();
        	parseWord(field, words, punctuations, totalSpace);
        }

        
        for (String field: words){
        	Integer count = wordSet.get(field);
        	if(count!=null)
        		wordSet.put(field, new Integer(count++));
        	else 
        		wordSet.put(field, new Integer(1));
        }
        
        for (String field: punctuations){
        	Integer count = punctuationSet.get(field);
        	if(count!=null)
        		punctuationSet.put(field, new Integer(count++));
        	else 
        		punctuationSet.put(field, new Integer(1));
        }
        
        for (Pattern p: entityDictionary){
        	
        	Matcher m = p.matcher(line);
        	while(m.find()){
        		TField matchedField = new TField();
        		if(m.group()!=null && m.group().trim()!=""){
	        		matchedField.setFieldtype(FieldType.NAMED_ENTITY);
	        		matchedField.setValue(m.group());
	        		matchedField.setStartIndex(m.start());
	        		matchedField.setEndIndex(m.end());
	        		matchedEntitySet.add(matchedField);
        		}
        	}
        	
        }
        
        
        SentenceBuilder sBuilder = new SentenceBuilder(wordSet, punctuationSet, totalSpace, matchedEntitySet);
        TSentence sentence = sBuilder.CreateSentence();
        return sentence; 
  }
    
   private void parseWord(String word, List<String> words, List<String> punctuations, Integer totalSpace) {
	   
	   char[] charArray;
	   charArray = word.toCharArray();
	   
	   String slim = word.trim();
	   
	   if(word.length()==0 || slim.length()==0){
		   totalSpace++;
		   return;
	   }else if (word.length()==1){
		   if (Character.isLetterOrDigit(charArray[0]))
			   words.add(word);
		   else
			   punctuations.add(word);
		   return;
	   }
	   
       boolean wholePunctuation = true;
	   for (char temp: charArray){
		   if(Character.isLetterOrDigit(temp)){
			   wholePunctuation = false;
			   break;
		   }
	   }
	   if (wholePunctuation){
		   punctuations.add(word);
		   return;
	   }
	   
	   sb.setLength(0);

	   for (char temp: charArray){
		  if (Character.isLetterOrDigit(temp))
			  sb.append(temp);
		  else if (Character.isWhitespace(temp))
			  totalSpace++;
		  else
			  punctuations.add(Character.toString(temp));
		   
	   }   
	   words.add(sb.toString());
	}
	  
   
   
	public void processFileEntity() throws IOException, JAXBException{
		
		
		   JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		   Sentenceset sentenceSet = new Sentenceset();

		   
	       Path path = Paths.get(fileName);
	        Scanner scanner = new Scanner(path);
	        scanner.useDelimiter(System.getProperty("line.separator"));
	        while(scanner.hasNextLine()){
	        	String line = scanner.nextLine();
	            //System.out.println("Lines: "+ line);
	            TSentence sentence =  parseLine(line);
	            if(sentence!=null)
	            	sentenceSet.getSentence().add(sentence);
	        }
	        scanner.close();
	        Marshaller marshaller = jaxbContext.createMarshaller();
		    marshaller.marshal(sentenceSet, new File("nlp_data.xml"));

	}
   
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, JAXBException{
		// TODO Auto-generated method stub
		
		String fileName = "nlp_data.txt";
		String entityName = "NER.txt";
		String xmlFileName = "nlp_data.xml";
		List<Pattern> dic = new ArrayList<Pattern>();

		
		FileScanner fs = new FileScanner(fileName, xmlFileName);
		
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
	    
	    fs.setEntityDictionary(dic);
		
		fs.processFile();

	}
	


}
