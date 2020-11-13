package com.alex.springcloud.domain;

import java.util.HashMap;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/13 14:32
 * @version:     1.0
 */
public class Response extends HashMap<String, Object> {

    private static final long serialVersionUID = -8713837118340960775L;

    public Response message(String message) {
        this.put("message", message);
        return this;
    }

    public Response data(Object data) {
        this.put("data", data);
        return this;
    }

    @Override
    public Response put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}