package kr.co.teacherforboss.service.authService;

import kr.co.teacherforboss.web.dto.AuthRequestDTO;

public interface AuthQueryService {
    boolean checkNickname(AuthRequestDTO.CheckNicknameDTO request);
}
