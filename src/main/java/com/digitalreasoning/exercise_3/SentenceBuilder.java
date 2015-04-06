package com.digitalreasoning.exercise_3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.digitalreasoning.nlf.FieldType;
import com.digitalreasoning.nlf.TField;
import com.digitalreasoning.nlf.TSentence;

public class SentenceBuilder {
	
	private Map<String, Integer> wordSet;
	private Map<String, Integer> punctuationSet;
	private Integer spaceTotal;
    private List<TField> matchedEntitySet;
	
	public SentenceBuilder(Map<String, Integer> wordSet,
			Map<String, Integer> punctuationSet, Integer spaceTotal, List<TField> matchedEntitySet) {
		super();
		this.wordSet = wordSet;
		this.punctuationSet = punctuationSet;
		this.spaceTotal = spaceTotal;
		this.matchedEntitySet = matchedEntitySet;
	}
	
	public TSentence CreateSentence(){
		
		TSentence sentence = new TSentence();
		 List<TField> ls = sentence.getField();
		
		for (Map.Entry<String, Integer> entry : wordSet.entrySet()) {
			TField field = new TField();
			field.setValue(entry.getKey());
			field.setTotal(entry.getValue());
			field.setFieldtype(FieldType.WORD);
			ls.add(field);
			
		}
		
		for (Map.Entry<String, Integer> entry : punctuationSet.entrySet()) {
			TField field = new TField();
			field.setValue(entry.getKey());
			field.setTotal(entry.getValue());
			field.setFieldtype(FieldType.PUNCTUATION);
			ls.add(field);
		}
		
		TField field = new TField();
		field.setTotal(spaceTotal);
		field.setFieldtype(FieldType.WHITESPACE);
		ls.add(field);
		
		for(TField t: matchedEntitySet){
			
			ls.add(t);
		}
		
		return sentence;
	}
	
	
	

}
