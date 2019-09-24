package com.acxiom.ams.api;

import com.acxiom.ams.compomemt.RetryRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by cldong on 12/5/2017.
 */
public abstract class API {
    @Autowired
    RetryRestTemplate retryRestTemplate;
}

