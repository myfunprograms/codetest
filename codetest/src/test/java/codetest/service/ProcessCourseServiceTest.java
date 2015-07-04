package codetest.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import codetest.domain.Course;
import codetest.domain.StateCode;

public class ProcessCourseServiceTest {
	private ProcessCourseServiceImpl service = new ProcessCourseServiceImpl();
	
	private final static Integer[] SEQ_IN_ORDER = new Integer[]{0, 1, 2};
	
	/////////////////////////////////////////////////
	// Blackbox test the interface method
	@Test
	public void processFileShouldReturnCourseListWhenInOrder() throws IOException {
		File file = new ClassPathResource("samplefile/CourseInOrderActive.csv").getFile();
		List<Course> result = service.processFile(file);
		
		assertEquals(3, result.size());
		
		Course[] expected = new Course[]{
			new Course(11L, "110", StateCode.INACTIVE),	
			new Course(12L, "120", StateCode.ACTIVE),	
			new Course(13L, "130", StateCode.ACTIVE),	
		};
		assertArrayEquals(expected, result.toArray());
	}

	@Test
	public void processFileShouldReturnCourseListWhenNotInOrder() throws IOException {
		File file = new ClassPathResource("samplefile/CourseNotInOrderActive.csv").getFile();
		List<Course> result = service.processFile(file);
		
		assertEquals(3, result.size());

		Course[] expected = new Course[]{
				new Course(31L, "310", StateCode.INACTIVE),	
				new Course(32L, "320", StateCode.ACTIVE),	
				new Course(33L, "330", StateCode.ACTIVE),	
			};
		assertArrayEquals(expected, result.toArray());
}

	/////////////////////////////////////////////////
	// Whitebox test to make sure the helper methods work
	@Test
	public void processHeaderShouldReturnSeqsInOrder() {
		String firstLine = "course_id, course_name, state";
		Integer[] result = service.processHeader(firstLine, Course.METADATA);
		
		assertArrayEquals(SEQ_IN_ORDER, result);
	}
	
	@Test
	public void processHeaderShouldReturnSeqsNotInOrder() {
		String firstLine = "course_name, state, course_id";
		Integer[] result = service.processHeader(firstLine, Course.METADATA);
		
		assertArrayEquals(new Integer[]{2, 0, 1}, result);
	}

	@Test
	public void processRecordShouldReturnCourseWhenInOrder() {
		String line = "2, Operating Systems, active";
		Course expected = new Course(2L, "Operating Systems", StateCode.ACTIVE);
				
		Course course = service.processRecord(line, SEQ_IN_ORDER);
		assertEquals(expected, course);
	}
	
	@Test
	public void processRecordShouldReturnCourseWhenNotInOrder() {
		String line = "Operating Systems, active, 2";
		Course expected = new Course(2L, "Operating Systems", StateCode.ACTIVE);
				
		Course course = service.processRecord(line, new Integer[]{2, 0, 1});
		assertEquals(expected, course);
	}
}
