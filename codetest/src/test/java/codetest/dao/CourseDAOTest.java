package codetest.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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
public class CourseDAOTest {
	
	@Autowired
	private CourseDAO courseDAO;
	
	@Autowired
	private StudentDAO studentDAO;

	private Course course = new Course(1L, "101", StateCode.ACTIVE);
	private Course updatedCourse = new Course(course.getCourseId(), "102", StateCode.INACTIVE);
	private Course newCourse = new Course(2L, "102", StateCode.ACTIVE);

	@Test
	public void createCourseShouldPassWhenNewCourse() {
		int persisted = courseDAO.createOrUpdate(course);
		assertEquals(1, persisted);
		
		Course savedCourse = courseDAO.find(course.getCourseId());
		assertEquals(course, savedCourse);
	}
	
	@Test
	public void updateCourseShouldPassWhenExists() {
		courseDAO.createOrUpdate(course);

		int persisted = courseDAO.createOrUpdate(updatedCourse);
		assertEquals(1, persisted);

		Course savedCourse = courseDAO.find(course.getCourseId());
		assertEquals(updatedCourse, savedCourse);
	}
	
	@Test
	public void createOrUpdateShouldPass() {
		List<Course> courses = Arrays.<Course>asList(new Course[]{
			course,
			updatedCourse,
			newCourse,
		});
		courseDAO.createOrUpdate(courses);

		List<Course> persisted = courseDAO.createOrUpdate(courses);
		assertEquals(0, persisted.size());
		
		Course savedUpdatedCourse = courseDAO.find(course.getCourseId());
		assertEquals(updatedCourse, savedUpdatedCourse);
		
		Course savedNewCourse = courseDAO.find(newCourse.getCourseId());
		assertEquals(newCourse, savedNewCourse);
	}

	@Test
	public void findAllActiveCoursesShouldReturnAllActiveCoursesAndNoStudent() {
		List<Course> courses = Arrays.<Course>asList(new Course[]{
			course,
		});
		courseDAO.createOrUpdate(courses);
		
		List<Object[]> persisted = courseDAO.findAllActiveCourses();
		assertEquals(1, persisted.size());
		
		assertEquals(course.getCourseId(), persisted.get(0)[0]);
	}
	
	@Test
	public void findAllActiveCoursesShouldReturnAllActiveCoursesAndHasStudent() {
		List<Course> courses = Arrays.<Course>asList(new Course[]{
			course,
		});
		courseDAO.createOrUpdate(courses);
		
		Student student1 = new Student(10L, course.getCourseId(), "user1", StateCode.ACTIVE);
		Student student2 = new Student(11L, course.getCourseId(), "user2", StateCode.ACTIVE);
		Student student3 = new Student(12L, course.getCourseId(), "user3", StateCode.INACTIVE);

		List<Student> students = Arrays.<Student>asList(new Student[]{
				student1, student2, student3
			});
			
		studentDAO.createOrUpdate(students);

		List<Object[]> persisted = courseDAO.findAllActiveCourses();
		assertEquals(2, persisted.size());
		
		assertEquals(course.getCourseId(), persisted.get(0)[0]);
		assertEquals(course.getCourseId(), persisted.get(1)[0]);
		assertEquals(student1.getUserId(), persisted.get(0)[2]);
		assertEquals(student2.getUserId(), persisted.get(1)[2]);
	}
	
	@Test
	public void findAllActiveCoursesShouldReturnAllActiveCoursesAndNoActivecourse() {
		List<Course> courses = Arrays.<Course>asList(new Course[]{
			updatedCourse,
		});
		courseDAO.createOrUpdate(courses);
		
		Student student1 = new Student(10L, updatedCourse.getCourseId(), "user1", StateCode.ACTIVE);
		Student student2 = new Student(11L, updatedCourse.getCourseId(), "user2", StateCode.ACTIVE);

		List<Student> students = Arrays.<Student>asList(new Student[]{
				student1, student2
		});
			
		studentDAO.createOrUpdate(students);

		List<Object[]> persisted = courseDAO.findAllActiveCourses();
		assertEquals(0, persisted.size());
	}

}
