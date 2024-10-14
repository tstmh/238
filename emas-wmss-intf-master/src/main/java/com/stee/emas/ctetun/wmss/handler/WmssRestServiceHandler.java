package com.stee.emas.ctetun.wmss.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stee.emas.common.constants.Constants;

@Service
public class WmssRestServiceHandler {

	private static final Logger logger = LoggerFactory.getLogger(WmssRestServiceHandler.class);

	@Value("${connectTimeout}")
	private String connectTimeout;
	@Value("${socketTimeout}")
	private String socketTimeout;
	
	@Autowired
	WmssDataProcess wmssDataProcess;

	public String callRestService(String restUrl) {
		logger.info("Calling callService .....");
		String respStr = new String();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		URIBuilder uriBuilder = null;
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		//String format = simpleDateFormat.format(new Date());
		RequestConfig config = RequestConfig.custom().setConnectTimeout(Integer.parseInt(connectTimeout)).setSocketTimeout(Integer.parseInt(socketTimeout)).build();
		try {
			uriBuilder = new URIBuilder(restUrl);
			//uriBuilder.setParameter("lastget", format);
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpGet.setConfig(config);
			logger.info("Executing Request....." + httpGet.getRequestLine());
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			logger.info("--------------------");
			logger.info("response ....." + response);
			logger.info("statusLine ....." + response.getStatusLine());
			logger.info("statusCode ....." + response.getStatusLine().getStatusCode());

			if (response.getStatusLine().getStatusCode() == 200) {
				respStr = EntityUtils.toString(response.getEntity());
				if (entity != null) {
					logger.info("Response content:: " + entity);
					logger.info("Response content Length :: " + entity.getContentLength());
				}
				EntityUtils.consumeQuietly(entity);
			}
			wmssDataProcess.handleRestServiceStatus(Constants.EQUIP_STATUS_NORMAL, Constants.ALARM_CLEARED);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			wmssDataProcess.handleRestServiceStatus(Constants.EQUIP_STATUS_NG, Constants.ALARM_RAISED);
			logger.error("Connection timed out: connecting to Rest Service ....." + restUrl);
		} finally {
			try {
				httpClient.close();
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return respStr;
	}
}