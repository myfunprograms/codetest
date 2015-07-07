package codetest.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;
import java.io.FileInputStream;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import codetest.Application;
import codetest.domain.CannotReadFileRuntimeException;
import codetest.domain.StateCode;
import codetest.domain.Student;

/** Integration test that tests the controller */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0") 
@Transactional
public class ProcessFileControllerIntegrationTest {
	private MockMvc mvc;

	@Autowired
    private WebApplicationContext webApplicationContext;
    
    @Before
    public void setup() throws Exception {
        mvc = webAppContextSetup(webApplicationContext).build();
    }    

    //////////////////////////////////////////////
    // Test navigation
	@Test
	public void getHome() throws Exception {
		mvc.perform(get(ProcessFileController.PATH)
						.accept(MediaType.TEXT_HTML))
						.andExpect(status().isOk())
		                .andExpect(view().name(ProcessFileController.HOME_PAGE))
						.andExpect(status().is(200));
	}
	
	@Test
	public void getResults() throws Exception {
		mvc.perform(post(ProcessFileController.PATH)
				.accept(MediaType.TEXT_HTML)
				.param(ProcessFileController.VIEW_RESULT, ""))
				.andExpect(status().isOk())
				.andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(model().attribute(ProcessFileController.QUERY_RESULT, hasSize(0)));
	}

	@Test
	public void uploadCoursesWithEmptyFile() throws Exception {
	    File file = new ClassPathResource("samplefile/Empty.csv").getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile courseFile = new MockMultipartFile("courseFile", 
				"samplefile/Empty.csv", 
				"text/csv", 
				inputStream);
  		mvc.perform(MockMvcRequestBuilders.fileUpload(ProcessFileController.PATH)
		                .file(courseFile)
		                .param(ProcessFileController.UPLOAD_COURSES, "")
		                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().isOk())
  				.andExpect(model().attribute(ProcessFileController.MESSAGE, ProcessFileController.MSG_EMPTY_FILE));
	}

	@Test
	public void uploadCourses() throws Exception {
	    File file = new ClassPathResource("samplefile/CourseInOrderActive.csv").getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile courseFile = new MockMultipartFile("courseFile", 
				"samplefile/CourseInOrderActive.csv", 
				"text/csv", 
				inputStream);
  		mvc.perform(MockMvcRequestBuilders.fileUpload(ProcessFileController.PATH)
		                .file(courseFile)
		                .param(ProcessFileController.UPLOAD_COURSES, "")
		                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().is(200));
	}
	
	// Correct number of string in the header but the content is wrong. 
	@Test
	public void uploadCoursesWithInvalidFileHeader() throws Exception {
	    File file = new ClassPathResource("samplefile/CoursesInvalid.csv").getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile courseFile = new MockMultipartFile("courseFile", 
				"samplefile/CoursesInvalid.csv", 
				"text/csv", 
				inputStream);
  		mvc.perform(MockMvcRequestBuilders.fileUpload(ProcessFileController.PATH)
		                .file(courseFile)
		                .param(ProcessFileController.UPLOAD_COURSES, "")
		                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().is(200))
  				.andExpect(model().attribute(ProcessFileController.MESSAGE, CannotReadFileRuntimeException.WRONG_HEADER));
	}

	@Test
	public void uploadCoursesWithNoFileHeader() throws Exception {
	    File file = new ClassPathResource("samplefile/CoursesNoHeader.csv").getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile courseFile = new MockMultipartFile("courseFile", 
				"samplefile/CoursesNoHeader.csv", 
				"text/csv", 
				inputStream);
  		mvc.perform(MockMvcRequestBuilders.fileUpload(ProcessFileController.PATH)
		                .file(courseFile)
		                .param(ProcessFileController.UPLOAD_COURSES, "")
		                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().is(200))
  				.andExpect(model().attribute(ProcessFileController.MESSAGE, CannotReadFileRuntimeException.WRONG_HEADER));
	}

	@Test
	public void uploadStudents() throws Exception {
	    File file = new ClassPathResource("samplefile/StudentInOrderActive.csv").getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile studentFile = new MockMultipartFile("studentFile", 
				"samplefile/StudentInOrderActive.csv", 
				"text/csv", 
				inputStream);
		
		Student[] expectedError = new Student[]{
			new Student(100L, 11L, "User 100", StateCode.INACTIVE),	
			new Student(100L, 11L, "User 100", StateCode.ACTIVE),	
			new Student(100L, 12L, "User 100", StateCode.ACTIVE),	
			new Student(101L, 11L, "User 101", StateCode.ACTIVE),	
			new Student(101L, 12L, "User 101", StateCode.ACTIVE),	
		};
		
  		mvc.perform(MockMvcRequestBuilders.fileUpload(ProcessFileController.PATH)
		                .file(studentFile)
		                .param(ProcessFileController.UPLOAD_STUDENTS, "")
		                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().isOk())
  				.andExpect(model().attribute(ProcessFileController.MESSAGE, 
  						String.format(ProcessFileController.MSG_PROCESS_FILE, 5)))
  				.andExpect(model().attribute(ProcessFileController.ERROR_MESSAGE,
  						String.format(ProcessFileController.MSG_PROCESS_FILE_ERROR, expectedError.length)))
				.andExpect(model().attribute(ProcessFileController.ERROR_STUDENTS,
						IsIterableContainingInAnyOrder.<Student>containsInAnyOrder(expectedError)
                 ));
	}

	// The number of strings in the header is wrong
	@Test
	public void uploadStudentsWithInvalidFile() throws Exception {
	    File file = new ClassPathResource("samplefile/StudentsInvalid.csv").getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile studentFile = new MockMultipartFile("studentFile", 
				"samplefile/StudentsInvalid.csv", 
				"text/csv", 
				inputStream);
  		mvc.perform(MockMvcRequestBuilders.fileUpload(ProcessFileController.PATH)
                .file(studentFile)
                .param(ProcessFileController.UPLOAD_STUDENTS, "")
                .contentType(MediaType.MULTIPART_FORM_DATA))
		        .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().is(200))
  				.andExpect(model().attribute(ProcessFileController.MESSAGE, CannotReadFileRuntimeException.WRONG_HEADER));
	}

    //////////////////////////////////////////////
    // Test courses, no student
	@Test
	public void uploadCoursesShouldPersistWhenCourseInOrderActive() throws Exception {
		Object[] expected = new Object[]{new Object[]{"11", "110", "", ""},
										 new Object[]{"12", "120", "", ""}};
		
		verifyUpdateCourseResult("samplefile/CourseInOrderActive.csv", expected);
	}

	@Test
	public void uploadCoursesShouldPersistWhenCourseNotInOrderActive() throws Exception {
		Object[] expected = new Object[]{new Object[]{"31", "310", "", ""},
										 new Object[]{"32", "320", "", ""}};
		
		verifyUpdateCourseResult("samplefile/CourseNotInOrderActive.csv", expected);
	}

	@Test
	public void uploadCoursesShouldPersistWhenCourseInOrderInactive() throws Exception {
		Object[] expected = new Object[]{};
		
		verifyUpdateCourseResult("samplefile/CourseInOrderInactive.csv", expected);
	}

	@Test
	public void uploadCoursesShouldPersistWhenCourseNotInOrderInactive() throws Exception {
		Object[] expected = new Object[]{};
		
		verifyUpdateCourseResult("samplefile/CourseNotInOrderInactive.csv", expected);
	}

	private void verifyUpdateCourseResult(String fileName, Object[] expected) throws Exception {
		uploadCourseFile(fileName, ProcessFileController.PATH);
  		
		mvc.perform(post(ProcessFileController.PATH)
				.accept(MediaType.TEXT_HTML)
				.param(ProcessFileController.VIEW_RESULT, ""))
				.andExpect(status().is(200))
				.andExpect(model().attribute(ProcessFileController.QUERY_RESULT, hasSize(expected.length)))
				.andExpect(model().attribute(ProcessFileController.QUERY_RESULT,
						IsIterableContainingInAnyOrder.<Object>containsInAnyOrder(expected)
                     ));
	}
	
    //////////////////////////////////////////////
    // Test courses and student
	@Test
	public void uploadCoursesAndStudentsWhenCoursesActiveAndStudentActiveInOrder() throws Exception {
		Object[] expected = new Object[]{
				new Object[]{"11", "110", "100", "User 100"},
				new Object[]{"11", "110", "101", "User 101"},
				new Object[]{"12", "120", "100", "User 100"},
				new Object[]{"12", "120", "101", "User 101"}
		};

		verifyUpdateCourseAndStudentResult("samplefile/CourseInOrderActive.csv", "samplefile/StudentInOrderActive.csv", expected);
	}

	@Test
	public void uploadCoursesAndStudentsWhenCoursesActiveInOrderAndStudentActiveNotInOrder() throws Exception {
		Object[] expected = new Object[]{
				new Object[]{"11", "110", "300", "User 300"},
				new Object[]{"11", "110", "301", "User 301"},
				new Object[]{"12", "120", "300", "User 300"},
				new Object[]{"12", "120", "301", "User 301"}
		};

		verifyUpdateCourseAndStudentResult("samplefile/CourseInOrderActive.csv", "samplefile/StudentNotInOrderActive.csv", expected);
	}

	//TODO: what to do in this case? Future active?
	@Test
	public void uploadCoursesAndStudentsWhenCoursesInactiveAndStudentActive() throws Exception {
		Object[] expected = new Object[]{};

		verifyUpdateCourseAndStudentResult("samplefile/CourseInOrderInactive.csv", "samplefile/StudentInactiveAndCourseInactive.csv", expected);
	}

	@Test
	public void uploadCoursesAndStudentsWhenCoursesActiveAndStudentInactive() throws Exception {
		Object[] expected = new Object[]{
				new Object[]{"11", "110", "", ""},
				new Object[]{"12", "120", "", ""},
		};

		verifyUpdateCourseAndStudentResult("samplefile/CourseInOrderActive.csv", "samplefile/StudentInactiveAndCourseActive.csv", expected);
	}

	@Test
	public void uploadCoursesAndStudentsWhenCoursesNotExistsAndStudentActive() throws Exception {
		Object[] expected = new Object[]{
				new Object[]{"11", "110", "", ""},
				new Object[]{"12", "120", "", ""},
		};

		verifyUpdateCourseAndStudentResult("samplefile/CourseInOrderActive.csv", "samplefile/StudentInactiveAndCourseActive.csv", expected);
	}

	private void verifyUpdateCourseAndStudentResult(String courseFileName, String studentFileName, Object[] expected) throws Exception {
		uploadCourseFile(courseFileName, ProcessFileController.PATH);
		uploadStudentFile(studentFileName, ProcessFileController.PATH);
  		
		mvc.perform(post(ProcessFileController.PATH)
				.accept(MediaType.TEXT_HTML)
				.param(ProcessFileController.VIEW_RESULT, ""))
				.andExpect(status().isOk())
				.andExpect(model().attribute(ProcessFileController.QUERY_RESULT, hasSize(expected.length)))
				.andExpect(model().attribute(ProcessFileController.QUERY_RESULT,
						IsIterableContainingInAnyOrder.<Object>containsInAnyOrder(expected)
                     ))
				.andExpect(model().attribute(ProcessFileController.MESSAGE, 
						String.format(ProcessFileController.MSG_VIEW_RESULT, expected.length)));	
	}

	private void uploadCourseFile(String fileName, String requestURL) throws Exception {
	    File file = new ClassPathResource(fileName).getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile courseFile = new MockMultipartFile("courseFile", 
				fileName, 
				"text/csv", 
				inputStream);
  		mvc.perform(MockMvcRequestBuilders.fileUpload(requestURL)
		                .file(courseFile)
		                .param(ProcessFileController.UPLOAD_COURSES, "")
		                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().isOk())
				.andExpect(model().attribute(ProcessFileController.ERROR_COURSES, nullValue()));		
  	}
	
	private void uploadStudentFile(String fileName, String requestURL) throws Exception {
	    File file = new ClassPathResource(fileName).getFile();
		FileInputStream inputStream = new FileInputStream(file);
		MockMultipartFile studentFile = new MockMultipartFile("studentFile", 
				fileName, 
				"text/csv", 
				inputStream);
  		mvc.perform(MockMvcRequestBuilders.fileUpload(requestURL)
		                .file(studentFile)
		                .param(ProcessFileController.UPLOAD_STUDENTS, "")
		                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(view().name(ProcessFileController.HOME_PAGE))
				.andExpect(status().isOk())
		  		.andExpect(model().attribute(ProcessFileController.ERROR_STUDENTS, nullValue()));		
  	}
}