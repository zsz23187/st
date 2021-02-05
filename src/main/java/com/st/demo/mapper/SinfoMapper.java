package com.st.demo.mapper;

import com.st.demo.entity.SinfoEntity;
import com.st.demo.entity.SlistEntity;

import java.util.List;

/**
 * 股票数据
 */
public interface SinfoMapper {
    //增删改查
    int saveData(List<SinfoEntity> slist);

    int deleteData(List<SinfoEntity> scode);

    List<String> findAllByCode(String stime);

    List<String> findByCode(String scode);
//
//    List<SinfoEntity> findByName(String sname);
//
//    List<SinfoEntity> findByTime(String scode, String starty, String endy);

    List<String> findDelCode(String stime);

    //当日的数据
    List<SinfoEntity> findDayAll();
    //当日的数据
    int saveDataDay(List<SinfoEntity> slist);

    int deleteDay();

    int saveStime(List<SinfoEntity> slist);

    int deleteStime();

    List<SinfoEntity> findStime();

    List<String> findAllByTime(String time);
}
