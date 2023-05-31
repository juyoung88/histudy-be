package edu.handong.csee.histudy.service;

import edu.handong.csee.histudy.controller.form.ReportForm;
import edu.handong.csee.histudy.domain.Course;
import edu.handong.csee.histudy.domain.Report;
import edu.handong.csee.histudy.domain.Study;
import edu.handong.csee.histudy.domain.User;
import edu.handong.csee.histudy.dto.ReportDto;
import edu.handong.csee.histudy.repository.CourseRepository;
import edu.handong.csee.histudy.repository.ReportRepository;
import edu.handong.csee.histudy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public ReportDto.Response createReport(ReportForm form, String accessToken) {
        User user = userRepository.findUserByAccessToken(accessToken).orElseThrow();
        List<Optional<User>> participants = form.getParticipants().stream()
                .map(userRepository::findUserBySid)
                .toList();
        List<Optional<Course>> courses = form.getCourses().stream()
                .map(courseRepository::findById)
                .toList();
        Report report = form.toEntity(user.getTeam(), participants);
        Report saved = reportRepository.save(report);
        List<Study> studies = courses.stream()
                .filter(Optional::isPresent)
                .map(c -> new Study(saved,c.get()))
                .toList();
        saved.setStudies(studies);
        return new ReportDto.Response(saved);
    }
}