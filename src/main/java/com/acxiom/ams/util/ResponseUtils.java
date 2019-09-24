package com.acxiom.ams.util;

import com.acxiom.ams.model.vo.ResponseVO;

public class ResponseUtils {

    private ResponseUtils(){}

    public static ResponseVO success(Object object) {
        ResponseVO msg = new ResponseVO();
        msg.setCode("200");
        msg.setMessage("Success");
        msg.setData(object);
        return msg;
    }

    public static ResponseVO error(String code, String message) {
        ResponseVO msg = new ResponseVO();
        msg.setCode(code);
        msg.setMessage(message);
        return msg;
    }
}
