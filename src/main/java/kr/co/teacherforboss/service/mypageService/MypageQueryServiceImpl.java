package kr.co.teacherforboss.service.mypageService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.MemberHandler;
import kr.co.teacherforboss.converter.BoardConverter;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.Question;
import kr.co.teacherforboss.domain.enums.Role;
import kr.co.teacherforboss.repository.QuestionRepository;
import kr.co.teacherforboss.service.authService.AuthCommandService;
import kr.co.teacherforboss.web.dto.MypageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MypageQueryServiceImpl implements MypageQueryService {

    private final AuthCommandService authCommandService;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public MypageResponseDTO.GetAnsweredQuestionsDTO getAnsweredQuestions(Long lastQuestionId, int size) {
        Member member = authCommandService.getMember();
        PageRequest pageRequest = PageRequest.of(0, size);

        if (!member.getRole().equals(Role.TEACHER)) throw new MemberHandler(ErrorStatus.MEMBER_ROLE_INVALID);

        Slice<Question> questions = lastQuestionId == 0
                ? questionRepository.findAnsweredQuestionsSliceByMemberIdOrderByCreatedAtDesc(member.getId(), pageRequest)
                : questionRepository.findAnsweredQuestionsSliceByIdLessthanAndMemberIdOrderByCreatedAtDesc(member.getId(), lastQuestionId, pageRequest);
        return BoardConverter.toGetAnsweredQuestionsDTO(questions, member);
    }
}
