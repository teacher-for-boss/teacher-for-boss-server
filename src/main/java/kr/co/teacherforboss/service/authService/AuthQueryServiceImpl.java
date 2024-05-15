package kr.co.teacherforboss.service.authService;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import kr.co.teacherforboss.config.jwt.PrincipalDetails;
import kr.co.teacherforboss.domain.Member;
import kr.co.teacherforboss.domain.enums.Status;
import kr.co.teacherforboss.repository.MemberRepository;
import kr.co.teacherforboss.web.dto.AuthRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService, UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new AuthHandler(ErrorStatus.MEMBER_NOT_FOUND));

        log.info("user Email [ID : {}]", member.getEmail());
        return new PrincipalDetails(member);
    }

    @Override
    public boolean checkNickname(AuthRequestDTO.CheckNicknameDTO request) {
        return !memberRepository.existsByNicknameAndStatus(request.getNickname(), Status.ACTIVE);
    }
}
