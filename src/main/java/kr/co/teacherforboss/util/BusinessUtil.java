package kr.co.teacherforboss.util;

import kr.co.teacherforboss.apiPayload.code.status.ErrorStatus;
import kr.co.teacherforboss.apiPayload.exception.handler.AuthHandler;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class BusinessUtil {

    private static final String STATUS_CODE_OK = "OK";
    private static final String VALID_CODE = "01";

    @Value("${business.api.url}")
    private String url;

    @Value("${business.api.key}")
    private String apiKey;

    public JSONObject getBusinessInfo(String businessNum, LocalDate openDate, String representative) {
        try {
            return requestBusinessAPI(businessNum, openDate, representative);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject requestBusinessAPI(String businessNum, LocalDate openDate, String representative) throws IOException, ParseException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject businessObject = new JSONObject();
        JSONArray businessArray = new JSONArray();
        JSONObject requestBody = new JSONObject();

        String parseBusinessNum = BusinessUtil.parseBusinessNum(businessNum);
        businessObject.put("b_no", parseBusinessNum);
        businessObject.put("start_dt", openDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        businessObject.put("p_nm", representative);

        businessArray.add(businessObject);

        requestBody.put("businesses", businessArray);

        RequestBody body = RequestBody.create(mediaType, requestBody.toString());
        Request request = new Request.Builder()
                .url(url + "?serviceKey=" + apiKey)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String responseBodyString = response.body().string();

        if (response.isSuccessful()) {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(responseBodyString);
        }
        return null;
    }

    public static String parseBusinessNum(String businessNum) {
        Pattern pattern = Pattern.compile("\\d{3}-\\d{2}-\\d{5}");
        Matcher matcher = pattern.matcher(businessNum);

        if (!matcher.matches()) {
            throw new AuthHandler(ErrorStatus.INVALID_BUSINESS_INFO);
        }

        return businessNum.replaceAll("-", "");
    }

    public boolean isStatusCodeOk(JSONObject response) {
        String statusCode = response.get("status_code").toString();
        return STATUS_CODE_OK.equals(statusCode);
    }

    public boolean isValidCode(JSONObject response) {
        String validCode = response.get("valid").toString();
        return VALID_CODE.equals(validCode);
    }
}
