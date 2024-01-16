package kr.co.teacherforboss.domain.vo.mailVO;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mail {
    protected String filename;
    protected String title;
    protected Map<String, String> values = new HashMap<>();

    protected Mail(String filename, String title) {
        this.filename = filename;
        this.title = title;
    }

    protected void setMailFields(String filename, String title) {
        this.filename = filename;
        this.title = title;
    }
}
