package codetest.dao;

import java.util.List;

import codetest.domain.Course;

public interface CourseDAO {
	/** Insert a new course if not exists a course with the same course_id.
	 *  Update an existing course if exists a course with the same course_id. */
	int createOrUpdate(Course course);

	/** Insert or update a list of courses. */
	List<Course> createOrUpdate(List<Course> course);
	
	/** Find an existing course by course_id. */
	Course find(Long courseId);
	
	/** Find all active courses with student records. */
	List<Object[]> findAllActiveCourses();
}
