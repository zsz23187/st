package com.st.demo.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SinfoEntity {
    Integer id;
    String stime; //交易日期
    String scode; //股票代码
    String sname; //股票名称
    Double sclose; //股票收盘价
    Double shigh; //股票最高价
    Double slow; //股票最低价
    Double sopen; //股票开盘价
    Double slclose; //股票前日收盘
    Double schg; //股票涨跌额
    Double spchg; //股票涨跌幅
    Double surnover; //股票换手率
    Double sotur; //股票成交量(股)
    Double svatur; //股票成交额(元)
    Double stcap; //股票总市值(元)
    Double smcap; //股票流通市值（元
}
