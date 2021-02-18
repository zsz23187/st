package com.st.demo.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StempEntity {
    Integer rc;
    Integer rt;
    long svr; //股票代码+数字 2+xxxxxx
    Integer lt;
    Integer full;
    SmapEntity data; //交易日期
}
