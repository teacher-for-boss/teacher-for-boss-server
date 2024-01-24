package kr.co.teacherforboss.domain.vo.smsVO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SMS {

    protected String message;

    public String getText() {
        return message;
    }
}
