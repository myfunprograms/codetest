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

import codetest.domain.StateCode;
import codetest.domain.Student;

@Repository
public class StudentDAOImpl implements StudentDAO {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String FIND_BY_ID = new StringBuffer()
			.append("SELECT user_id, course_id, user_name, state ")
			.append("FROM student ")
			.append("WHERE user_id = ? AND course_id = ? ")
			.toString();
		
	private String CREATE = new StringBuffer()
			.append("INSERT INTO student (user_id, course_id, user_name, state) ")
			.append("VALUES (?, ?, ?, ?) ")
			.toString();

	private String UPDATE = new StringBuffer()
			.append("UPDATE student ")
			.append("SET user_name = ?, ")
			.append("    state = ? ")
			.append("WHERE user_id = ? AND course_id = ?")
			.toString();

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Student> createOrUpdate(List<Student> students) {
		List<Student> error = new ArrayList<Student>();
		
		for (Student student : students) {
			int updated = createOrUpdate(student);
			if (updated != 1) {
				error.add(student);
			}
		}
		
		return error;
	}
	
	@Override
	public Student find(Long studentId, Long courseId) {
		log.debug("find {}", studentId);
		
		return jdbcTemplate
				.queryForObject(FIND_BY_ID, new Object[]{studentId, courseId}, new RowMapper<Student>(){
			        public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
			            return new Student(rs.getLong("user_id"), rs.getLong("course_id"), rs.getString("user_name"), StateCode.getByCode(rs.getString("state")));
			        }
			    });
	}

	public int createOrUpdate(Student student) {
		try {
			return create(student);
		} catch (DataIntegrityViolationException e) {
			return update(student);
		}
	}
	
	protected int create(Student student) {
		log.debug("create student id={} course id={}", student.getUserId(), student.getCourseId());
		
		return jdbcTemplate.update(CREATE, 
				new Object[]{student.getUserId(), student.getCourseId(), student.getUserName(), student.getStateCode()});
    }

	protected int update(Student student) {
		log.debug("update student id={} course id={}", student.getUserId(), student.getCourseId());

		return jdbcTemplate.update(UPDATE, 
				new Object[]{student.getUserName(), student.getStateCode(), student.getUserId(), student.getCourseId()});
    }
}
