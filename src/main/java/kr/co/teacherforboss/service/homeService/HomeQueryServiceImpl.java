package kr.co.teacherforboss.service.homeService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.HomeHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.converter.MemberConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.TeacherInfo;
import kr.co.teacherforboss.domain.TeacherSelectInfo;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.repository.PostRepository;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.repository.TeacherInfoRepository;
import kr.co.teacherforboss.repository.TeacherSelectInfoRepository;
import kr.co.teacherforboss.web.dto.HomeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeQueryServiceImpl implements HomeQueryService {

    private final PostRepository postRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final TeacherInfoRepository teacherInfoRepository;
    private final TeacherSelectInfoRepository teacherSelectInfoRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "hot_posts")
    public HomeResponseDTO.GetHotPostsDTO getHotPosts() {
        return BoardConverter.toGetHotPostsDTO(postRepository.findHotPosts());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "hot_questions")
    public HomeResponseDTO.GetHotQuestionsDTO getHotQuestions() {
        return BoardConverter.toGetHotQuestionsDTO(questionRepository.findHotQuestions());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "hot_teachers")
    public HomeResponseDTO.GetHotTeachersDTO getHotTeachers() {
        List<TeacherSelectInfo> hotTeacherSelectInfos = teacherSelectInfoRepository.findHotTeachers();

        List<Long> hotTeacherIds = hotTeacherSelectInfos.stream()
                .map(teacherSelectInfo -> teacherSelectInfo.getMember().getId()).toList();
        List<Member> hotTeachers = memberRepository.findAllByIdInAndStatus(hotTeacherIds, Status.ACTIVE);
        List<TeacherInfo> hotTeacherInfos = teacherInfoRepository.findAllByMemberIdInAndStatus(hotTeacherIds, Status.ACTIVE);

        if (hotTeachers.size() != hotTeacherIds.size() || hotTeacherInfos.size() != hotTeacherIds.size()) {
            throw new HomeHandler(ErrorStatus.INVALID_HOT_TEACHER_DATA);
        }

        Map<Long, Member> hotTeacherMap = hotTeachers.stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));
        Map<Long, TeacherInfo> hotTeacherInfoMap = hotTeacherInfos.stream().collect(Collectors.toMap(
                teacherInfo -> teacherInfo.getMember().getId(), teacherInfo -> teacherInfo));

        return MemberConverter.toGetHotTeachersDTO(hotTeacherIds, hotTeacherMap, hotTeacherInfoMap);
    }
}

