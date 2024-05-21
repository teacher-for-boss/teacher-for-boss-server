package kr.co.teacherforboss.util;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class BusinessUtil {

    private static final String STATUS_CODE_OK = "OK";
    private static final String VALID_CODE = "01";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final Pattern BUSINESS_NUMBER_PATTERN = Pattern.compile("\\d{3}-\\d{2}-\\d{5}");

    @Value("${business.api.url}")
    private String apiUrl;

    @Value("${business.api.key}")
    private String apiKey;

    public boolean requestBusinessAPI(String businessNumber, LocalDate openDate, String representative) {
        String parsedBusinessNumber = parseBusinessNumber(businessNumber);
        String formattedDate = openDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));

        Map<String, Object> requestBody = createRequestBody(parsedBusinessNumber, formattedDate, representative);
        String responseBody = sendPostRequest(apiUrl, apiKey, requestBody);

        String valid = parseResponseField(responseBody, "valid");
        String statusCode = parseResponseField(responseBody, "status_code");

        return STATUS_CODE_OK.equals(statusCode) && VALID_CODE.equals(valid);
    }

    private Map<String, Object> createRequestBody(String businessNumber, String startDate, String representative) {
        Map<String, String> business = new HashMap<>();
        business.put("b_no", businessNumber);
        business.put("start_dt", startDate);
        business.put("p_nm", representative);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("businesses", Collections.singletonList(business));

        return requestBody;
    }

    private String sendPostRequest(String url, String apiKey, Map<String, Object> requestBody) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        return WebClient.builder()
                .uriBuilderFactory(factory)
                .build()
                .post()
                .uri(url + "?serviceKey=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public static String parseBusinessNumber(String businessNumber) {
        Matcher matcher = BUSINESS_NUMBER_PATTERN.matcher(businessNumber);
        if (!matcher.matches()) {
            throw new AuthHandler(ErrorStatus.INVALID_BUSINESS_INFO);
        }
        return businessNumber.replaceAll("-", "");
    }

    private String parseResponseField(String responseBody, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(responseBody);
        return matcher.find() ? matcher.group(1) : null;
    }
}