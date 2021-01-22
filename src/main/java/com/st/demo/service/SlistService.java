package com.st.demo.service;

import com.st.demo.entity.SlistEntity;
import com.st.demo.mapper.SlistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class SlistService {

    @Autowired(required = false)
    private SlistMapper slistMapper;

    public int saveData(List<SlistEntity> slist){
        return slistMapper.saveData(slist);
    }

    public int deleteData(List<SlistEntity> slist){
        return slistMapper.deleteData(slist);
    }

    public int deleteDataAll(){
        return slistMapper.deleteDataAll();
    }

    public List<SlistEntity> findAll(){
        return slistMapper.findAll();
    }
}
