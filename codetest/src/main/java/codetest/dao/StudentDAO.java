package codetest.dao;

import java.util.List;

import codetest.domain.Student;

public interface StudentDAO {
	
	/** Find an existing student by user_id and course Id. */
	Student find(Long studentId, Long courseId);

	/** Insert a new student if not exists a student with the same user_id and course_id.
	 *  Update an existing student if exists a student with the same user_id and course_id. */
	int createOrUpdate(Student student);

	/** Insert or update a list of students */
	List<Student> createOrUpdate(List<Student> students);
}
