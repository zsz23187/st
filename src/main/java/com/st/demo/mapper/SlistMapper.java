package com.st.demo.mapper;

import com.st.demo.entity.SinfoEntity;
import com.st.demo.entity.SlistEntity;

import java.util.List;

/**
 * 股票列表
 */
public interface SlistMapper {
    //增删改查
    int saveData(List<SlistEntity> slist);

    int deleteData(List<SlistEntity> slist);

    int deleteDataAll();

    List<SlistEntity> findAll();
//
//    List<SlistEntity> findByCode(String scode);
//
//    List<SlistEntity> findByName(String sname);
}
