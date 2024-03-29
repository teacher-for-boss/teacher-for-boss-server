package kr.co.teacherforboss.apiPayload.code.status;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import kr.co.teacherforboss.apiPayload.code.BaseErrorCode;
import kr.co.teacherforboss.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _BAD_REQUEST(BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _DATA_NOT_FOUND(NOT_FOUND, "COMMON404", "해당 데이터를 찾을 수 없습니다."),
    _INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),


    // Soft Delete
    ALREADY_DELETED(BAD_REQUEST, "DELETE4001", "이미 삭제되었습니다."),


    // Mail
    MAIL_BAD_REQUEST(BAD_REQUEST, "MAIL4001", "이메일 형식이 유효하지 않습니다."),
    MAIL_SEND_FAIL(BAD_REQUEST, "MAIL4002","이메일을 전송할 수 없습니다."),
    NO_MAIL_CODE(BAD_REQUEST, "MAIL4003","해당 이메일에 유효한 인증정보가 없습니다."),
    TOO_MANY_MAIL_REQUEST(BAD_REQUEST, "MAIL4004","메일 인증 요청은 하루에 5회까지 가능합니다."),


    // Sms
    SMS_SEND_FAIL(INTERNAL_SERVER_ERROR, "SMS5001", "sms 전송에 실패했습니다."),


    // Member
    NICKNAME_NOT_EXIST(BAD_REQUEST, "MEMBER4001", "닉네임은 필수 입니다."),
    MEMBER_EMAIL_DUPLICATE(BAD_REQUEST, "MEMBER4002", "이미 존재하는 이메일입니다."),
    MEMBER_PHONE_DUPLICATE(BAD_REQUEST, "MEMBER4003", "이미 존재하는 휴대전화 번호입니다."),
    GENERAL_MEMBER_DUPLICATE(BAD_REQUEST, "MEMBER4004", "일반 회원가입을 통해 진행한 이메일 계정입니다."),
    SOCIAL_MEMBER_INFO_EMPTY(BAD_REQUEST, "MEMBER4005", "소셜 회원가입에 필요한 값이 없습니다."),

    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER4041", "사용자가 없습니다."),

    // Survey
    SURVEY_DUPLICATE(BAD_REQUEST, "SURVEY4001", "이미 사전정보가 존재합니다."),


    // Auth
    MAIL_NOT_CHECKED(BAD_REQUEST, "AUTH4001", "이메일을 인증 받지 않았습니다."),
    INVALID_CODE_MAIL(BAD_REQUEST, "AUTH4002", "이메일 인증번호가 틀렸습니다."),
    TIMEOUT_CODE_MAIL(BAD_REQUEST, "AUTH4003", "이메일 인증 유효시간이 만료되었습니다."),
    AUTH_EMAIL_DUPLICATED(BAD_REQUEST, "AUTH4004", "이미 가입된 이메일입니다."),
    INVALID_PASSWORD(BAD_REQUEST, "AUTH4005", "비밀번호 입력 조건을 확인해주세요."),
    PASSWORD_NOT_CORRECT(BAD_REQUEST, "AUTH4006", "비밀번호 입력값과 일치하지 않습니다."),
    AUTH_PHONE_DUPLICATED(BAD_REQUEST, "AUTH4007", "이미 가입된 전화번호입니다."),
    INVALID_CODE_PHONE(BAD_REQUEST, "AUTH4008", "휴대폰 인증번호가 틀렸습니다."),
    TIMEOUT_CODE_PHONE(BAD_REQUEST, "AUTH4009", "휴대폰 인증 유효시간이 만료되었습니다."),
    PHONE_NOT_CHECKED(BAD_REQUEST, "AUTH40010", "전화번호를 인증 받지 않았습니다."),
    INVALID_AGREEMENT_TERM(BAD_REQUEST, "AUTH40011", "모든 필수 이용 약관에 동의해야 합니다."),
    SOCIAL_TYPE_BAD_REQUEST(BAD_REQUEST, "AUTH40012", "기입한 소셜 로그인 유형과 일치하지 않습니다."),

    TOKEN_TIME_OUT(UNAUTHORIZED, "AUTH4011", "토큰이 만료되었습니다."),
    INVALID_JWT_TOKEN(UNAUTHORIZED, "AUTH4012", "토큰 유효성 검사 실패 또는 거부된 토큰입니다."),
    LOGIN_FAILED_PASSWORD_INCORRECT(UNAUTHORIZED,"AUTH4013", "비밀번호가 틀립니다."),

    ACCESS_DENIED(FORBIDDEN, "AUTH4031", "접근 권한이 없습니다."),

    JWT_REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "AUTH4041", "존재하지 않는 RefreshToken 입니다. 다시 로그인 해주세요."),


    // Exam
    MEMBER_EXAM_DUPLICATE(BAD_REQUEST, "EXAM4001", "이미 치룬 시험입니다."),
    INVALID_QUESTION_CHOICE(BAD_REQUEST, "EXAM4002", "문제에 맞지 않는 선지를 선택했습니다."),
    INVALID_EXAM_TAKE(BAD_REQUEST, "EXAM4003", "풀지 않은 문제가 있거나 문제가 너무 많습니다."),

    MEMBER_EXAM_NOT_FOUND(NOT_FOUND, "EXAM4041", "사용자가 치루지 않은 시험입니다."),
    EXAM_NOT_FOUND(NOT_FOUND, "EXAM4042", "시험 정보를 찾을 수 없습니다."),
    QUESTION_NOT_FOUND(NOT_FOUND, "EXAM4043", "입력한 ID 값에 해당되는 문제를 찾을 수 없습니다."),
    QUESTION_CHOICE_NOT_FOUND(NOT_FOUND, "EXAM4044", "입력한 ID 값에 해당되는 문제 선지를 찾을 수 없습니다."),
    EXAM_AVERAGE_NOT_FOUND(NOT_FOUND, "EXAM4045", "같은 업종 사장님 평균 점수를 내기 위한 데이터가 없습니다."),
    MEMBER_EXAM_HISTORY_NOT_FOUND(NOT_FOUND, "EXAM4046", "사용자가 시험을 치룬 내역이 없습니다."),
    EXAM_CATEGORY_NOT_FOUND(NOT_FOUND, "EXAM4047", "시험 카테고리가 존재하지 않습니다."),


    // For test
    TEMP_EXCEPTION(BAD_REQUEST, "TEMP4001", "이거는 테스트");


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