package kr.co.teacherforboss.domain.vo.smsVO;

import java.text.MessageFormat;
import java.util.Random;
import lombok.Getter;

@Getter
public class CodeSMS extends SMS {

    public static final long VALID_TIME = 60 * 3L; // seconds
    private String code;
    private String appHash;

    public CodeSMS(String appHash) {
        super("<#> [티쳐포보스] \n인증번호: {0} \n{1}");
        this.code = createCode();
        this.appHash = appHash;
    }

    @Override
    public String getText() {
        return MessageFormat.format(this.message, code, appHash);
    }

    private String createCode() {
        final int CODE_LENGTH = 5;
        String code = "";
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code += random.nextInt(10);
        }
        return code;
    }
}
