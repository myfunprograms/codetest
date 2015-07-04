package codetest.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import codetest.domain.StateCode;
import codetest.domain.Student;

public class ProcessStudentServiceTest {
	private ProcessStudentServiceImpl service = new ProcessStudentServiceImpl();
	
	private final static Integer[] SEQ_IN_ORDER = new Integer[]{0, 1, 2, 3};
	
	/////////////////////////////////////////////////
	// Blackbox test the interface method
	@Test
	public void processFileShouldReturnStudentListWhenInOrder() throws IOException {
		File file = new ClassPathResource("samplefile/StudentInOrderActive.csv").getFile();
		List<Student> result = service.processFile(file);
		
		assertEquals(5, result.size());
		
		Student[] expected = new Student[]{
			new Student(100L, 11L, "User 100", StateCode.INACTIVE),	
			new Student(100L, 11L, "User 100", StateCode.ACTIVE),	
			new Student(100L, 12L, "User 100", StateCode.ACTIVE),	
			new Student(101L, 11L, "User 101", StateCode.ACTIVE),	
			new Student(101L, 12L, "User 101", StateCode.ACTIVE),	
		};
		assertArrayEquals(expected, result.toArray());
	}

	@Test
	public void processFileShouldReturnStudentListWhenNotInOrder() throws IOException {
		File file = new ClassPathResource("samplefile/StudentNotInOrderActive.csv").getFile();
		List<Student> result = service.processFile(file);
		
		assertEquals(5, result.size());
		
		Student[] expected = new Student[]{
			new Student(300L, 11L, "User 300", StateCode.INACTIVE),	
			new Student(300L, 11L, "User 300", StateCode.ACTIVE),	
			new Student(300L, 12L, "User 300", StateCode.ACTIVE),	
			new Student(301L, 11L, "User 301", StateCode.ACTIVE),	
			new Student(301L, 12L, "User 301", StateCode.ACTIVE),	
		};
		assertArrayEquals(expected, result.toArray());
	}

	/////////////////////////////////////////////////
	// Whitebox test the helper methods
	@Test
	public void processHeaderShouldReturnSeqsInOrder() {
		String firstLine = "user_id, course_id,  user_name, state";
		Integer[] result = service.processHeader(firstLine, Student.METADATA);
		
		assertArrayEquals(SEQ_IN_ORDER, result);
	}
	
	@Test
	public void processHeaderShouldReturnSeqsNotInOrder() {
		String firstLine = "user_id,   user_name, course_id, state";
		Integer[] result = service.processHeader(firstLine, Student.METADATA);
		
		assertArrayEquals(new Integer[]{0, 2, 1, 3}, result);
	}

	@Test
	public void processRecordShouldReturnStudentWhenInOrder() {
		String line = "4, 2, Jon Doe, active";
		Student expected = new Student(4L, 2L, "Jon Doe", StateCode.ACTIVE);
				
		Student student = service.processRecord(line, SEQ_IN_ORDER);
		assertEquals(expected, student);
	}
	
	@Test
	public void processRecordShouldReturnStudentWhenNotInOrder() {
		String line = "4, Jon Doe, 2, active";
		Student expected = new Student(4L, 2L, "Jon Doe", StateCode.ACTIVE);
				
		Student student = service.processRecord(line, new Integer[]{0, 2, 1, 3});
		assertEquals(expected, student);
	}
}
