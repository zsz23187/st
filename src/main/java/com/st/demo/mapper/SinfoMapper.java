package com.st.demo.mapper;

import com.st.demo.entity.SinfoEntity;

import java.util.List;

/**
 * 股票数据
 */
public interface SinfoMapper {
    //增删改查
    int saveData(List<SinfoEntity> slist);

    int deleteData(String scode);

    List<String> findAllByCode(String stime);

    List<String> findByCode(String scode);
//
//    List<SinfoEntity> findByName(String sname);
//
//    List<SinfoEntity> findByTime(String scode, String starty, String endy);


}
