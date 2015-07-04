package codetest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import codetest.domain.Student;

/** A class to read all the records from the Student CSV file. */
@Service
@Qualifier("studentService")
public class ProcessStudentServiceImpl extends AbstractProcessRecordService<Student> {
	@Override
	protected Student processRecord(String line, Integer[] seqs) {
		String[] fields = line.split(DELIMITOR);
		return new Student(fields, seqs);
	}

	@Override
	protected List<String> getMetadata() {
		return Student.METADATA;
	}
}
