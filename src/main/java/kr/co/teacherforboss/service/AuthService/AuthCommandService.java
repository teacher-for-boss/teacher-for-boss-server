package kr.co.teacherforboss.service.AuthService;

import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;

public interface AuthCommandService {
    public Member joinMember(AuthRequestDTO.JoinDto request);
}