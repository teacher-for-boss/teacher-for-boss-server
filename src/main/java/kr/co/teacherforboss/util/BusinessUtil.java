package kr.co.teacherforboss.util;

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
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject businessObject = new JSONObject();
        businessObject.put("b_no", businessNum);
        businessObject.put("start_dt", openDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        businessObject.put("p_nm", representative);

        JSONArray businessArray = new JSONArray();
        businessArray.add(businessObject);

        JSONObject requestBody = new JSONObject();
        requestBody.put("businesses", businessArray);

        RequestBody body = RequestBody.create(mediaType, requestBody.toString());

        Request request = new Request.Builder()
                .url(url + "?serviceKey=" + apiKey)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseBodyString = response.body().string();
            if (response.isSuccessful()) {
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(responseBodyString);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
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
