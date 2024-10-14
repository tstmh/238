package com.stee.pasystem.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stee.pasystem.enums.ResultCodeEnum;
import com.stee.pasystem.exceptions.ApiProcessException;
import com.stee.pasystem.vos.HeartbeatRespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HeartbeatService {

    private final ObjectMapper objectMapper;

    @Value("${stee.based-api-link}")
    private String basedApiLink;
    private final String API_URI = "/pasystem/api/heartbeat/";
    public HeartbeatRespVO process() throws ApiProcessException {
        OkHttpClient client = new OkHttpClient();

        LocalDateTime now = LocalDateTime.now();

        String pattern = "yyyyMMddHHmmss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        String formattedDateTime = now.format(formatter);

        String url = basedApiLink + API_URI + formattedDateTime;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            // Check if the request was successful (HTTP status code 200)
            if (response.isSuccessful()) {
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        String responseString = responseBody.string();
                        log.info("Heartbeat response Body: {}", responseString);
                        HeartbeatRespVO heartbeatRespVO = objectMapper.readValue(responseString, HeartbeatRespVO.class);
                        Integer resultCode = heartbeatRespVO.getResultCode();
                        if (resultCode == null || !resultCode.equals(ResultCodeEnum.SUCCESS.getCode())) {
                            throw new ApiProcessException("The server responses an error, error code: " + resultCode);
                        }
                        return heartbeatRespVO;
                    } else {
                        throw new ApiProcessException("The response body is null");
                    }
                }
            } else {
                throw new ApiProcessException("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            log.error("Error occurs on HeartbeatService.process: ", e);
            throw new ApiProcessException(e.getMessage());
        }
    }
}
