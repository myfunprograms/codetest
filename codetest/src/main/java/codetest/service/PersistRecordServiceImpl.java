package codetest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codetest.dao.CourseDAO;
import codetest.dao.StudentDAO;
import codetest.domain.Course;
import codetest.domain.Student;

@Service
@Transactional
public class PersistRecordServiceImpl implements PersistRecordService {
	@Autowired
	private CourseDAO courseDAO;
	
	@Autowired
	private StudentDAO studentDAO;

	////////////////////////////////
	// CourseDAO
	public List<Course> createOrUpdateCourses(List<Course> courses) {
		return courseDAO.createOrUpdate(courses);
	}
	
	public List<Object[]> findAllActiveCourses() {
		return courseDAO.findAllActiveCourses();
	}

	////////////////////////////////
	// CourseDAO
	public List<Student> createOrUpdateStudents(List<Student> students) {
		return studentDAO.createOrUpdate(students);
	}
}
