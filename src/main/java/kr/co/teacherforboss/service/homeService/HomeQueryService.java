package kr.co.teacherforboss.service.homeService;

import kr.co.teacherforboss.web.dto.HomeResponseDTO;

public interface HomeQueryService {
    HomeResponseDTO.GetHotPostsDTO getHotPosts();
    HomeResponseDTO.GetHotQuestionsDTO getHotQuestions();
    HomeResponseDTO.GetHotTeachersDTO getHotTeachers();
}
