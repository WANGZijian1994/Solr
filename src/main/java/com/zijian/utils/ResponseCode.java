package com.zijian.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ResponseCode {
    private Object data;

    private Long total;

    private String msg;

    public ResponseCode(String msg){
        this.msg = msg;
    }

    public ResponseCode(Object data, Long total){
        this.data = data;
        this.total = total;
    }
}
