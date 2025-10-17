package com.javaweb.service.impl.CourseService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.javaweb.entity.Course.CourseEnrollmentEntity;
import com.javaweb.entity.Course.CourseEntity;
import com.javaweb.entity.Course.CourseModuleEntity;
import com.javaweb.repository.ICourseEnrollmentRepository;
import com.javaweb.repository.ICourseRepository;
import com.javaweb.repository.impl.CourseRepositoryCustom.CourseRepositoryCustom;
import com.javaweb.service.ICourseService;

@Service
public class CourseServiceImpl implements ICourseService {

    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private ICourseEnrollmentRepository courseEnrollmentRepository;
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
	public List<CourseEntity> getAllCourse() {
		try {
			return courseRepository.getAllCourse();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public CourseEntity getCourseById(Long courseId) {
		try {
			return courseRepository.getCourseById(courseId);
		} catch (Exception e) {
			throw new RuntimeException(e + " Can not get course by id");
		}
	}

	@Override
	public double getCoursePrice(Long courseId) {
		CourseEntity course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("not found course"));
		return course.getPrice();
	}

	@Override
	public ResponseEntity<Object> getUserProgress(Long courseId, Long userId) {
		/*try {
			CourseEnrollmentEntity courseEnrollment = courseEnrollmentRepository.getUserProgress(courseId, userId);
			Map<String,Object> data = Map.of("overallProgress", courseEnrollment.getProgress(), "", "");
		} catch (Exception e) {
			e.fillInStackTrace();
			throw new RuntimeException(e + " error in user progress");
		}*/
		return null;
	}

}
