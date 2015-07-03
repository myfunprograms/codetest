CREATE TABLE course
(course_id BIGINT NOT NULL, 
 course_name VARCHAR(50) NOT NULL, 
 state VARCHAR(8) NOT NULL,
 PRIMARY KEY (course_id)
);
 
CREATE TABLE student
(user_id BIGINT NOT NULL,
 course_id BIGINT NOT NULL,
 user_name VARCHAR(50) NOT NULL, 
 state VARCHAR(8) NOT NULL,
 PRIMARY KEY (user_id, course_id),
 FOREIGN KEY (course_id) REFERENCES course(course_id)
);