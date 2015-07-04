package codetest.domain;

import java.util.Arrays;
import java.util.List;

/** DTO of student table */
public class Student implements DomainObject {
	private Long userId;
	private Long courseId;
	private String userName;
	private StateCode state;

	public final static List<String> METADATA = 
			Arrays.asList(new String[]{"user_id", "course_id", "user_name", "state"});

	public Student(Long userId, Long courseId, String userName, StateCode state) {
		this.userId = userId;
		this.courseId = courseId;
		this.userName = userName;
		this.state = state;
	}

	public Student(String[] fields, Integer[] seqs) {
		this.userId = Long.valueOf(fields[seqs[0]].trim());
		this.courseId = Long.valueOf(fields[seqs[1]].trim());
		this.userName = fields[seqs[2]].trim();
		this.state = StateCode.getByCode(fields[seqs[3]].trim());
	}
	
	public Long getUserId() {
		return userId;
	}

	public Long getCourseId() {
		return courseId;
	}

	public String getUserName() {
		return userName;
	}

	public StateCode getState() {
		return state;
	}
	
	public String getStateCode() {
		return getState().getCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((courseId == null) ? 0 : courseId.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (courseId == null) {
			if (other.courseId != null)
				return false;
		} else if (!courseId.equals(other.courseId))
			return false;
		if (state != other.state)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Student [userId=" + userId + ", courseId=" + courseId + ", userName=" + userName + ", state=" + getStateCode()
				+ "]";
	}
}
