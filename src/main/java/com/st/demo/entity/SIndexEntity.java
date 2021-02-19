package com.st.demo.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SIndexEntity {
    Integer id;
    Integer ttime; //交易日期的数字
    Integer tcode; //股票代码+数字 2+xxxxxx
    String stime; //交易日期
    String scode; //股票代码
    String sname; //股票名称
    Double sclose; //股票收盘价
    Double shigh; //股票最高价
    Double slow; //股票最低价
    Double sopen; //股票开盘价
    Double schg; //股票涨跌额
    Double spchg; //股票涨跌幅
    Double surnover; //股票换手率
    Double sotur; //股票成交量(股)
    Double svatur; //股票成交额(元)
    Double macd;
    Double dif;
    Double dea;
    Double ema12;
    Double ema50;
    Double boll;
    Double mms;
    Double mml;
    Double mmm;
    Double avgvatur; //平均股票成交额
    Double avgotur; //平均股票成交量
    Double weight;
}
