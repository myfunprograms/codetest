package codetest.domain;

import java.util.Arrays;
import java.util.List;

/** DTO of course table */
public class Course implements DomainObject {
    private Long courseId;
	private String courseName;
	private StateCode state;
	
	public final static List<String> METADATA = 
			Arrays.<String>asList(new String[]{"course_id", "course_name", "state"});

	public Course(Long courseId, String courseName, StateCode state) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.state = state;
	}

	public Course(String[] fields, Integer[] seqs) {
		this.courseId = Long.valueOf(fields[seqs[0]].trim());
		this.courseName = fields[seqs[1]].trim();
		this.state = StateCode.getByCode(fields[seqs[2]].trim());
	}
	
	public Long getCourseId() {
		return courseId;
	}

	public String getCourseName() {
		return courseName;
	}

	public StateCode getState() {
		return state;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((courseId == null) ? 0 : courseId.hashCode());
		result = prime * result + ((courseName == null) ? 0 : courseName.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Course [courseId=" + courseId + ", courseName=" + courseName + ", state=" + getState().getCode() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (courseId == null) {
			if (other.courseId != null)
				return false;
		} else if (!courseId.equals(other.courseId))
			return false;
		if (courseName == null) {
			if (other.courseName != null)
				return false;
		} else if (!courseName.equals(other.courseName))
			return false;
		if (state != other.state)
			return false;
		return true;
	}
}
