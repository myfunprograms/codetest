package codetest.service;

import java.util.List;

import codetest.domain.Course;
import codetest.domain.Student;

public interface PersistRecordService {

	List<Course> createOrUpdateCourses(List<Course> courses);

	List<Object[]> findAllActiveCourses();

	List<Student> createOrUpdateStudents(List<Student> students);

}
