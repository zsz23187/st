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

    public List<String> findTimeByCode(String scode){
        return sinfoMapper.findTimeByCode(scode);
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

    public int saveStime(List<SinfoEntity> slist){
        return sinfoMapper.saveStime(slist);
    }

    public int deleteStime(){
        return sinfoMapper.deleteStime();
    }

    public List<SinfoEntity> findStime(){
        return sinfoMapper.findStime();
    }

    public List<String> findAllByTime(String time){
        return sinfoMapper.findAllByTime(time);
    }

    /**
     * 根据股票代码和日期查询数据，日期从大到小
     * @param scode
     * @param beg
     * @param end
     * @return
     */
    public List<SinfoEntity> findByCodeTimeDesc(String scode, Integer beg, Integer end){
        return sinfoMapper.findByCodeTimeDesc(scode, beg, end);
    }

    /**
     * 根据股票代码和日期查询数据，日期从小到大
     * @param scode
     * @param beg
     * @param end
     * @return
     */
    public List<SinfoEntity> findByCodeTime(String scode, Integer beg, Integer end){
        return sinfoMapper.findByCodeTime(scode, beg, end);
    }

    /**
     * 根据日期查询数据，日期从小到大
     * @param beg
     * @param end
     * @return
     */
    public List<SinfoEntity> findByTime(Integer beg, Integer end){
        return sinfoMapper.findByTime(beg, end);
    }
}
