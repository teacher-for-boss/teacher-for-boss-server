package kr.co.teacherforboss.domain.vo.mailVO;

import java.util.Random;

public class CodeMail extends Mail {
    public static final long VALID_TIME = 1000 * 60 * 3L;

    public CodeMail() {
        super("code", "[티쳐 포 보스] 인증 번호");
        super.values.put("code", createCode());
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
