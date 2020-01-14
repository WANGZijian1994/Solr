package com.zijian.service;

import com.zijian.utils.ResponseCode;

import java.util.Map;

public interface SearchService {
    ResponseCode search(Map<String,Object> searchMap);
}
