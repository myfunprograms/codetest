package codetest.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import codetest.domain.CannotReadFileRuntimeException;
import codetest.domain.Course;
import codetest.domain.DomainObject;
import codetest.domain.Student;
import codetest.service.PersistRecordService;
import codetest.service.ProcessRecordService;

@Controller
public class ProcessFileController {
	private final static Logger log = LoggerFactory.getLogger(ProcessFileController.class);

	protected final static String MESSAGE = "message";
	protected final static String ERROR_MESSAGE = "errorMessage";
	protected final static String ERROR_RECORDS = "errorRecords";
	protected final static String QUERY_RESULT = "queryResult";

	protected final static String UPLOAD_COURSES = "updateCourses";
	protected final static String UPLOAD_STUDENTS = "uploadStudents";
	protected final static String UPLOAD_COURSES_PATH = "/" + UPLOAD_COURSES;
	protected final static String UPLOAD_STUDENTS_PATH = "/" + UPLOAD_STUDENTS;
	
	protected final static String MSG_EMPTY_FILE = "Oops, File is empty!";
	protected final static String MSG_PROCESS_FILE_FAIlED = "Sorry, fail to upload this file.";
	protected final static String MSG_PROCESS_FILE = "Upload is successful. The number of the records processed is %d.";
	protected final static String MSG_PROCESS_FILE_ERROR = "The following %d records are not saved.";
	protected final static String MSG_VIEW_RESULT = "Returns %d records.";

	private final static String HOME_PAGE = "index";
	
	@Autowired
	private ProcessRecordService<Course> courseService;
	
	@Autowired
	private ProcessRecordService<Student> studentService;

	@Autowired
	private PersistRecordService persistService;
	
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String home() {
        return HOME_PAGE;
    }

    /** Method to handle uploading a course file */
    @RequestMapping(value=UPLOAD_COURSES_PATH, method=RequestMethod.POST, params = UPLOAD_COURSES)
    public String uploadCourses(@RequestParam("file") MultipartFile multipartFile, Model model) {
        Function<File, List<Course>> courseFileFunction = file -> {
            try {
                return courseService.processFile(file);
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        };

        Function<List<Course>, List<Course>> coursePersistFunction = courses -> {
            return persistService.createOrUpdateCourses(courses);
        };

        return uploadFile(multipartFile, 
 			   model, 
 			   courseFileFunction,
 			   coursePersistFunction);
    }

    /** Method to handle uploading a student file */
    @RequestMapping(value=UPLOAD_STUDENTS_PATH, method=RequestMethod.POST, params = UPLOAD_STUDENTS)
    public String uploadStudents(@RequestParam("file") MultipartFile multipartFile, Model model) {
        Function<File, List<Student>> studentFileFunction = file -> {
            try {
                return studentService.processFile(file);
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        };
        
        Function<List<Student>, List<Student>> studentPersistFunction = students -> {
                return persistService.createOrUpdateStudents(students);
        };

        return uploadFile(multipartFile, 
    			   model, 
    			   studentFileFunction,
    			   studentPersistFunction);
    }
   
    private <T extends DomainObject> String uploadFile(MultipartFile multipartFile, Model model, 
		Function<File, List<T>> fileFunc, Function<List<T>, List<T>> domainObjectFunc) {
        if (multipartFile.isEmpty()) {
            model.addAttribute(MESSAGE, MSG_EMPTY_FILE);
        } else {    
            try {
            	File file = convertToFile(multipartFile);
                List<T> domainObjects = fileFunc.apply(file);
                List<T> domainObjectsWithError = domainObjectFunc.apply(domainObjects);

                int totalNumber = domainObjects.size();
                int errorNumber = domainObjectsWithError.size();
                String message = String.format(MSG_PROCESS_FILE, totalNumber);
                model.addAttribute(MESSAGE,  message);

                if (!domainObjectsWithError.isEmpty()) {
                    String errorMessage = String.format(MSG_PROCESS_FILE_ERROR, errorNumber);
                	model.addAttribute(ERROR_MESSAGE, errorMessage);
                	model.addAttribute(ERROR_RECORDS, domainObjectsWithError);
                }
            } catch (CannotReadFileRuntimeException e) {
            	log.error(e.getMessage());
            	model.addAttribute(MESSAGE, e.getMessage());
            } catch (Exception e) {
            	log.error(e.getMessage());
            	model.addAttribute(MESSAGE, MSG_PROCESS_FILE_FAIlED);
            }
        } 

        return HOME_PAGE;
    }

    @RequestMapping(value = "/viewResults", method=RequestMethod.GET)
    public String viewResults(Model model) {
    	List<Object[]> allActiveCoursesAndStudents = persistService.findAllActiveCourses();
        model.addAttribute(MESSAGE, String.format(MSG_VIEW_RESULT, allActiveCoursesAndStudents.size()));
        model.addAttribute(QUERY_RESULT, allActiveCoursesAndStudents);
        return HOME_PAGE;
    }
    
    private File convertToFile(MultipartFile multipartFile) 
    		throws IllegalStateException, IOException{ 
        File file = File.createTempFile("tmp", ".csv");
        multipartFile.transferTo(file);
        return file;
    }
}
