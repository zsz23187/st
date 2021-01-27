package com.st.demo.service;

import com.st.demo.entity.SinfoEntity;
import com.st.demo.entity.SlistEntity;
import com.st.demo.mapper.SinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SinfoService {
    @Autowired(required = false)
    private SinfoMapper sinfoMapper;
    public int saveData(List<SinfoEntity> slist){
        return sinfoMapper.saveData(slist);
    }

    public int deleteData(List<SinfoEntity> scode){
        return sinfoMapper.deleteData(scode);
    }

    public List<String> findByCode(String scode){
        return sinfoMapper.findByCode(scode);
    }

    public List<String> findAllByCode(String stime){
        return sinfoMapper.findAllByCode(stime);
    }

    public List<String> findDelCode(String stime){
        return sinfoMapper.findDelCode(stime);
    }

    public List<SinfoEntity> findDayAll(){
        return sinfoMapper.findDayAll();
    }

    public int saveDataDay(List<SinfoEntity> slist){
        return sinfoMapper.saveDataDay(slist);
    }

    public int deleteDay(){
        return sinfoMapper.deleteDay();
    }
}
