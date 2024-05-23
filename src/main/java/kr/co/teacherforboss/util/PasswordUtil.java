package kr.co.teacherforboss.util;

import kr.co.teacherforboss.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

    private final PasswordEncoder passwordEncoder;

    public List<String> generatePassword(String password) {
        List<String> passwordList = new ArrayList<>();
        String pwSalt = generateSalt();
        String pwHash = generatePwHash(password, pwSalt);
        passwordList.add(pwSalt);
        passwordList.add(pwHash);
        return passwordList;
    }

    // hash 생성
    private String generatePwHash(String pwRequest, String pwSalt){
        return passwordEncoder.encode(pwSalt + pwRequest);
    }

    // salt 생성
    private String generateSalt() {

        SecureRandom r = new SecureRandom();
        byte[] salt = new byte[20];

        r.nextBytes(salt);

        StringBuffer sb = new StringBuffer();
        for(byte b : salt) {
            sb.append(String.format("%02x", b));
        };

        return sb.toString();
    }

    public List<String> generateSocialMemberPassword(){
        List<String> passwordList = new ArrayList<>();
        String pwSalt = generateSalt();
        String pwHash = generatePwHash(getRandomPassword(20), pwSalt);
        passwordList.add(pwSalt);
        passwordList.add(pwHash);
        return passwordList;
    }

    private static final char[] rndAllCharacters = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '!', '@', '#', '$', '%', '^', '&', '_', '=', '+'
    };

    private String getRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        int rndAllCharactersLength = rndAllCharacters.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(rndAllCharacters[random.nextInt(rndAllCharactersLength)]);
        }

        return stringBuilder.toString();
    }
}
