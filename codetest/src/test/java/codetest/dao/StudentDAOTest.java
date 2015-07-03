package codetest.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import codetest.Application;
import codetest.domain.Course;
import codetest.domain.StateCode;
import codetest.domain.Student;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
public class StudentDAOTest {
	@Autowired
	private StudentDAO studentDAO;

	@Autowired
	private CourseDAO courseDAO;

	private Course course1 = new Course(1L, "101", StateCode.ACTIVE);
	private Course course2 = new Course(2L, "102", StateCode.ACTIVE);
	private Student student = new Student(10L, course1.getCourseId(), "user1", StateCode.ACTIVE);
	private Student studentToUpdate = new Student(student.getUserId(), student.getCourseId(), "user11", StateCode.INACTIVE);
	private Student studentNewCourse = new Student(student.getUserId(), course2.getCourseId(), "user11", StateCode.INACTIVE);
	private Student studentNoCourse = new Student(student.getUserId(), 3L, "user2", StateCode.ACTIVE);

	@Before
	public void setUp() {
		courseDAO.createOrUpdate(course1);
		courseDAO.createOrUpdate(course2);
	}
	
	@Test
	public void createOrUpdateShouldCreateWhenStudentNotExistsAndCourseExists() {
		int persisted = studentDAO.createOrUpdate(student);
		assertEquals(1, persisted);

		Student savedStudent = getSavedStudent(student);
		assertEquals(student, savedStudent);
	}
	
	@Test
	public void createOrUpdateShouldCreateWhenStudentExistsAndHasNotTakeCourse() {
		studentDAO.createOrUpdate(student);

		int persisted = studentDAO.createOrUpdate(studentNewCourse);
		assertEquals(1, persisted);

		Student savedStudent = getSavedStudent(student);
		assertEquals(student, savedStudent);
		
		Student savedStudentNewCourse = getSavedStudent(studentNewCourse);
		assertEquals(studentNewCourse, savedStudentNewCourse);
	}

	@Test
	public void createOrUpdateStudentShouldUpdateWhenStudentAndCourseExists() {
		studentDAO.createOrUpdate(student);
		studentDAO.createOrUpdate(studentToUpdate);
		
		Student savedStudent = getSavedStudent(student);
		assertEquals(studentToUpdate, savedStudent);
	}

	@Test
	public void createOrUpdateStudentShouldPass() {
		List<Student> students = Arrays.<Student>asList(new Student[]{
			student,
			studentToUpdate,
			studentNewCourse,
		});
		
		List<Student> persisted = studentDAO.createOrUpdate(students);
		assertEquals(0, persisted.size());
		
		Student savedStudent = getSavedStudent(student);
		assertEquals(studentToUpdate, savedStudent);
		
		Student savedNewStudent = getSavedStudent(studentNewCourse);
		assertEquals(studentNewCourse, savedNewStudent);
	}

	@Test
	public void createStudentShouldReturn0WhenCourseDoesNotExists() {
		int count = studentDAO.createOrUpdate(studentNoCourse);
		assertEquals(0, count);
	}

	@Test
	public void createOrUpdateStudentShouldReturnListWithError() {
		List<Student> students = Arrays.<Student>asList(new Student[]{
			student,
			studentToUpdate,
			studentNewCourse,
			studentNoCourse,
		});
		
		List<Student> persisted = studentDAO.createOrUpdate(students);
		assertEquals(1, persisted.size());
		
		Student savedStudent = getSavedStudent(student);
		assertEquals(studentToUpdate, savedStudent);
		
		Student savedNewStudent = getSavedStudent(studentNewCourse);
		assertEquals(studentNewCourse, savedNewStudent);
		
		assertEquals(studentNoCourse, persisted.get(0));
	}

	private Student getSavedStudent(Student studentToFind) {
		return studentDAO.find(studentToFind.getUserId(), studentToFind.getCourseId());
	}
}
