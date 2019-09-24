package com.acxiom.ams.compomemt;

import com.acxiom.ams.common.exception.AMSRMIException;
import com.acxiom.ams.common.utils.LogUtils;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by cldong on 12/6/2017.
 */
@Component
public class RetryRestTemplate {

    @Autowired
    RestTemplate restTemplate; //RestTemplate是Spring提供的用于访问Rest服务的客户端
                               //调用RestTemplate的默认构造函数，
                               //RestTemplate对象在底层通过使用java.net包下的实现创建HTTP 请求，
                               //可以通过使用ClientHttpRequestFactory指定不同的HTTP请求方式。
    public <T> T get(String servicePath, String path, Class<T> responseType, Object... urlVariables)
            throws AMSRMIException {
        return execute(HttpMethod.GET, servicePath, path, null, responseType, urlVariables);
    }

    public <T> ResponseEntity<T> get(String servicePath, String path,
                                     ParameterizedTypeReference<T> reference,
                                     Object... uriVariables)
            throws AMSRMIException {
        return exchangeGet(servicePath, path, reference, uriVariables);
    }

    public <T> T post(String servicePath, String path, Object request, Class<T> responseType,
                      Object... uriVariables)
            throws AMSRMIException {
        return execute(HttpMethod.POST, servicePath, path, request, responseType, uriVariables);
    }

    public void put(String servicePath, String path, Object request, Object... urlVariables)
            throws AMSRMIException {
        execute(HttpMethod.PUT, servicePath, path, request, null, urlVariables);
    }

    public void delete(String servicePath, String path, Object... urlVariables)
            throws AMSRMIException {
        execute(HttpMethod.DELETE, servicePath, path, null, null, urlVariables);
    }

    private <T> T execute(HttpMethod method, String servicePath, String path, Object request,
                          Class<T> responseType,
                          Object... uriVariables) throws AMSRMIException {

        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }
        try {
            return doExecute(method, servicePath, path, request, responseType, uriVariables);
        } catch (Exception e) {
            LogUtils.error(e);
            String error = ((HttpClientErrorException) e).getResponseBodyAsString();
            LogUtils.error(error);
            LogUtils.error("Http request failed, uri: " + parseHost(servicePath) + path + ", method: " + method);
            JSONObject jsonObject = JSONObject.parseObject(error);
            try {
                JSONObject errorObject = jsonObject.getJSONObject("error");
                throw new AMSRMIException(errorObject.getString("code"), errorObject.getString("message"), errorObject.get("data"));
            }catch (JSONException e1){
                throw new AMSRMIException(jsonObject.getString("status"), jsonObject.getString("message"));
            }
        }
    }

    private <T> T doExecute(HttpMethod method, String servicePath, String path, Object request,
                            Class<T> responseType, Object... uriVariables) {
        T result = null;
        switch (method) {
            case GET:
                result = restTemplate
                        .getForObject(parseHost(servicePath) + path, responseType, uriVariables);
                break;
            case POST:
                result = restTemplate
                        .postForEntity(parseHost(servicePath) + path, request, responseType,
                                uriVariables).getBody();
                break;
            case PUT:
                restTemplate.put(parseHost(servicePath) + path, request, uriVariables);
                break;
            case DELETE:
                restTemplate.delete(parseHost(servicePath) + path, uriVariables);
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("unsupported http method(method=%s)", method));
        }
        return result;
    }

    private <T> ResponseEntity<T> exchangeGet(String servicePath, String path,
                                              ParameterizedTypeReference<T> reference,
                                              Object... uriVariables) throws AMSRMIException {
        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }

        try {
           return restTemplate.exchange(parseHost(servicePath) + path, HttpMethod.GET, null, reference,
                                    uriVariables);
        } catch (Exception e) {
            LogUtils.error(e);
            LogUtils.error(
                    "Http request failed, uri: " + parseHost(servicePath) + path + "  , method: "
                            + HttpMethod.GET);
            throw new AMSRMIException();
        }
    }

    private String parseHost(String servicePath) {
        return servicePath + "/";
    }

    public <T> ResponseEntity<T> doGetWithHeader(String url, Map<String, String> headerMap,
                                                 ParameterizedTypeReference<T> reference) {

        HttpHeaders requestHeaders = new HttpHeaders();
        if (headerMap != null && !headerMap.isEmpty())
            for (String key : headerMap.keySet()) {
                requestHeaders.add(key, headerMap.get(key));
            }
        HttpEntity<T> requestEntity = new HttpEntity<T>(null, requestHeaders);
        ResponseEntity<T> result = restTemplate
                .exchange(url, HttpMethod.GET, requestEntity, reference);
        return result;
    }

    public <T> ResponseEntity<T> doPostWithHeader(String url, Map<String, String> headerMap, JSONObject body,
                                                  ParameterizedTypeReference<T> reference) {

        HttpHeaders requestHeaders = new HttpHeaders();
        if (headerMap != null && !headerMap.isEmpty())
            for (String key : headerMap.keySet()) {
                requestHeaders.add(key, headerMap.get(key));
            }
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, requestHeaders);
        ResponseEntity<T> result = restTemplate
                .exchange(url, HttpMethod.POST, requestEntity, reference);
        return result;
    }
}
