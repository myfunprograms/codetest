package codetest.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codetest.domain.DomainObject;

/** A super class that implements the interface method to process a file */
public abstract class AbstractProcessRecordService<T extends DomainObject> implements ProcessRecordService<T>{
	private final static Logger log = LoggerFactory.getLogger(AbstractProcessRecordService.class);
	
	protected final static String DELIMITOR = ",";

	@Override
	public List<T> processFile(File file) throws IOException {
		List<T> domainObjects = new ArrayList<T>();
		String line = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));

			String firstLine = reader.readLine();
			Integer[] seqs = processHeader(firstLine, getMetadata());
			
			while ((line = reader.readLine()) != null) {
				log.debug("Course line in file =" + line);
				T domainObject = processRecord(line, seqs);
				domainObjects.add(domainObject);
			}	
			
			return domainObjects;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}
	}
	
	protected Integer[] processHeader(String firstLine, List<String> metadata) {
		if (firstLine == null || firstLine.isEmpty())
			throw new RuntimeException();
			
		String[] fields = firstLine.split(DELIMITOR);
		
		if (fields.length != metadata.size()) 
			throw new RuntimeException();
			
	    Integer[] columnsSequence = getColumnsSequence(metadata, fields);
		return columnsSequence;
	}

	private Integer[] getColumnsSequence(List<String> metadata, String[] fields) {
		List<Integer> seqs = new ArrayList<Integer>();
		for (int i = 0; i < fields.length; i++) {
			seqs.add(metadata.indexOf(fields[i].trim()));
		}
		
		Integer[] resultSeqs = new Integer[seqs.size()];
		for (int i = 0; i < resultSeqs.length; i++) {
			resultSeqs[i] = seqs.indexOf(i);
		}
		return resultSeqs;
	}
	
	abstract protected T processRecord(String line, Integer[] seqs);
	
	abstract protected List<String> getMetadata();
}
