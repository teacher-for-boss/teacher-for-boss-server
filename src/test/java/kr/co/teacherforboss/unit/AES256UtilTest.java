package kr.co.teacherforboss.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import kr.co.teacherforboss.apiPayload.exception.GeneralException;
import kr.co.teacherforboss.util.AES256Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")  // 필요한 경우 경로 변경
public class AES256UtilTest {

    @Autowired
    private AES256Util aes256Util;

    @Test
    public void testEncryptDecrypt() {
        String originalText = "Hello, World!";  // Example text to encrypt and decrypt
        String encryptedText = null;
        String decryptedText = null;

        try {
            // Encrypt the original text
            encryptedText = aes256Util.encrypt(originalText);
            // Decrypt the encrypted text
            decryptedText = aes256Util.decrypt(encryptedText);
        } catch (GeneralException e) {
            e.printStackTrace();
        }

        // Assert that the decrypted text is the same as the original text
        assertEquals(originalText, decryptedText, "Decrypted text should match the original text");
    }
}
