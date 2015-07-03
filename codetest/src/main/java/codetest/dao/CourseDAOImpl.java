package codetest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import codetest.domain.Course;
import codetest.domain.StateCode;

/**
 * This class handles all data operations for the course table.
 */
@Repository
public class CourseDAOImpl implements CourseDAO {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String QUERY = new StringBuffer()
			.append("SELECT c.course_id, c.course_name, s.user_id, s.user_name ")
			.append("FROM course c ")
			.append("  LEFT OUTER JOIN student s ON c.course_id = s.course_id ")
			.append("  AND s.state = '").append(StateCode.ACTIVE.getCode()).append("' ")
			.append("WHERE c.state = '").append(StateCode.ACTIVE.getCode()).append("' ")
			.toString();
	
	private String CREATE = new StringBuffer()
			.append("INSERT INTO course (course_id, course_name, state)")
			.append("VALUES (?, ?, ?)")
			.toString();
	
	private String UPDATE = new StringBuffer()
			.append("UPDATE course ")
			.append("SET course_name = ?, ")
			.append("    state = ? ")
			.append("WHERE course_id = ?")
			.toString();

	private String FIND_BY_ID = new StringBuffer()
			.append("SELECT course_id, course_name, state ")
			.append("FROM course ")
			.append("WHERE course_id = ? ")
			.toString();

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Course> createOrUpdate(List<Course> courses) {
		List<Course> error = new ArrayList<Course>();

		for (Course course : courses) {
			int updated = createOrUpdate(course);
			if (updated != 1) 
				error.add(course);
		}
		
		return error;
	}

	public int createOrUpdate(Course course) {
		try {
			return create(course);
		} catch (DataIntegrityViolationException e) {
			return update(course);
		}
	}
	
	public Course find(Long courseId) {
		log.debug("find {}", courseId);
		
		return jdbcTemplate
				.queryForObject(FIND_BY_ID, new Object[]{courseId}, new RowMapper<Course>(){
			        public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
			            return new Course(rs.getLong("course_id"), rs.getString("course_name"), StateCode.getByCode(rs.getString("state")));
			        }
			    });
	}
	
	private int create(Course course) {
		log.debug("create {}", course.getCourseId());
		
		return jdbcTemplate.update(CREATE, 
				new Object[]{course.getCourseId(), course.getCourseName(), course.getState().getCode()});
    }

	private int update(Course course) {
		log.debug("update {}", course.getCourseId());

		return jdbcTemplate.update(UPDATE, 
				new Object[]{course.getCourseName(), course.getState().getCode(), course.getCourseId()});
    }

	@Override
	public List<Object[]> findAllActiveCourses() {
		log.debug("findAllActiveCourses");

		return jdbcTemplate
				.query(QUERY, new RowMapper<Object[]>() {
			        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
			            Object[] result = new Object[]{
			            		rs.getLong("course_id"), rs.getString("course_name"), 
			            		rs.getLong("user_id"), rs.getString("user_name")};
			            return result;
			        }
			    });
	}
}
