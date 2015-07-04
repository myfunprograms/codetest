package codetest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import codetest.domain.Course;

/** A class to read all the records from the course CSV file. */
@Service
@Qualifier("courseService")
public class ProcessCourseServiceImpl extends AbstractProcessRecordService<Course> {
	@Override
	protected Course processRecord(String line, Integer[] seqs) {
		String[] fields = line.split(DELIMITOR);
		return new Course(fields, seqs);
	}

	@Override
	protected List<String> getMetadata() {
		return Course.METADATA;
	}	
}
