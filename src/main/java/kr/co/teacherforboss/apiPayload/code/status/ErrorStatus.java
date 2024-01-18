package kr.co.teacherforboss.apiPayload.code.status;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // Soft Delete
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "DELETE400", "이미 삭제되었습니다."),

    // Mail
    MAIL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "MAIL4001", "이메일 형식이 유효하지 않습니다."),
    MAIL_SEND_FAIL(HttpStatus.BAD_REQUEST, "MAIL4002","이메일을 전송할 수 없습니다."),
    NO_MAIL_CODE(HttpStatus.BAD_REQUEST, "MAIL4003","해당 이메일에 유효한 인증정보가 없습니다."),
    TOO_MANY_MAIL_REQUEST(HttpStatus.BAD_REQUEST, "MAIL4004","메일 인증 요청은 하루에 5회까지 가능합니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    MEMBER_DUPLICATE(HttpStatus.BAD_REQUEST, "MEMBER4003", "이미 존재하는 이메일입니다."),

    // Auth
    MAIL_NOT_CHECKED(HttpStatus.NOT_FOUND, "AUTH4001", "이메일을 인증 받지 않았습니다."),
    PASSWORD_NOT_CORRECT(HttpStatus.BAD_REQUEST, "AUTH4002", "비밀번호 입력값과 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH4003", "비밀번호 입력 조건을 확인해주세요."),
    PHONE_NOT_CHECKED(HttpStatus.NOT_FOUND, "AUTH4004", "전화번호를 인증 받지 않았습니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}