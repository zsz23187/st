package com.st.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.demo.entity.SinfoEntity;
import com.st.demo.entity.SlistEntity;
import com.st.demo.service.RestTemplateToInterface;
import com.st.demo.service.SinfoService;
import com.st.demo.service.SlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.csvreader.CsvReader;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

/**
 * 定期获取股票列表，并进行对比更新
 */
@RestController
@RequestMapping(value = "/getStock")
public class GetSlistController {

    private Logger logger = LoggerFactory.getLogger(GetSlistController.class);
    @Resource
    private RestTemplateToInterface<String> templateToInterface;
    @Resource
    private SlistService slistService;
    @Resource
    private SinfoService sinfoService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    private SimpleDateFormat snum = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat symd = new SimpleDateFormat("yyyy-MM-dd");

    private String listUrl = "http://21.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112407750807719818911_1609121370291&pn=1&pz=9999&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:0+t:6,m:0+t:13,m:0+t:80,m:1+t:2,m:1+t:23&fields=f12,f14&_=1609121370292";

    private String stock1 = "http://quotes.money.163.com/service/chddata.html?";
    //    code=1600403&end=20180413
    private String stock2 = "&end=&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    @GetMapping(value = "/getList")
    public void getStock(HttpServletResponse response, HttpServletRequest request) throws Exception {
        String json = templateToInterface.doGetWith1(listUrl, String.class);
        ObjectMapper objm = new ObjectMapper();
        List<SlistEntity> slist = new ArrayList<>();
        Map mp = null;
        try {
            mp = objm.readValue(json.substring(json.indexOf("(") + 1, json.length() - 2), Map.class);
            List<Map> sdata = (List<Map>) ((Map) mp.get("data")).get("diff");
            //60是沪市 00是深市
            slist = sdata.parallelStream().filter(f ->
                    f.get("f12").toString().startsWith("60") ||
                            f.get("f12").toString().startsWith("00")).map(map -> {
                SlistEntity slistEntity = new SlistEntity();
                slistEntity.setScode(map.get("f12").toString());
                slistEntity.setSname(map.get("f14").toString());
                return slistEntity;
            }).collect(Collectors.toCollection(LinkedList::new));
            List<SlistEntity> old = slistService.findAll();
            //求差，old-slist 找出已经不存在的旧股票
            List<SlistEntity> finalSlist = slist;
            List<SlistEntity> delOld = old.stream().filter(new Predicate<SlistEntity>() {
                @Override
                public boolean test(SlistEntity student) {
                    for (SlistEntity boy : finalSlist) {
                        if (student.getScode().equals(boy.getScode())) {
                            return false;
                        }
                    }
                    return true;
                }
            }).collect(Collectors.toList());
            if (delOld.size() > 0)
                slistService.deleteData(delOld); //删掉不存在的旧的
            List<SlistEntity> saveNew = slist.stream().filter(new Predicate<SlistEntity>() {
                @Override
                public boolean test(SlistEntity student) {
                    for (SlistEntity boy : old) {
                        if (student.getScode().equals(boy.getScode())) {
                            return false;
                        }
                    }
                    return true;
                }
            }).collect(Collectors.toList());
            int st = 0;
            if (saveNew.size() > 0)
                st = slistService.saveData(saveNew);
            logger.info("保存股票信息个数：" + st + "  保存时间: " + sdf.format(new Date()));
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

//        templateToInterface.readCSVFileWithMap(stock1 + "code=0600403&end="+stock2);
//        System.out.println(slist);
    }

    public static String downloadFromUrl(String url, String dir, String scode) {

        try {
            URL httpurl = new URL(url);
            String fileName = scode + ".csv";
            System.out.println(fileName);
            File f = new File(dir + fileName);
            System.out.println(httpurl);
            FileUtils.copyURLToFile(httpurl, f);
        } catch (Exception e) {
            e.printStackTrace();
            return "Fault!";
        }
        return "Successful!";
    }

    /**
     * 获取每只股票的历史交易记录，保存到数据库
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @GetMapping(value = "/getData")
    public void getStockData(HttpServletResponse response, HttpServletRequest request) {
        List<SlistEntity> stockList = slistService.findAll();
        //查询历史数据，如果为空 则保存所有历史数据
        //如果历史数据有值，则开始时间为历史记录的最后一天+1
        String url = "";
        String start = "";
        int st = 0;
        String err = "";
        try {
//先查询每个股票是否有当天的信息，没有则插入新数据
            List<String> scodeList = sinfoService.findAllByCode(symd.format(new Date()));
            List<SlistEntity> delOld = stockList.stream().filter(new Predicate<SlistEntity>() {
                @Override
                public boolean test(SlistEntity student) {
                    for (String boy : scodeList) {
                        if (student.getScode().equals(boy)) {
                            return false;
                        }
                    }
                    return true;
                }
            }).collect(Collectors.toList());
            //linshi
            for (int i = delOld.size()-1; i >0; i--) {
                start = "";
                SlistEntity sle = delOld.get(i);
                err = sle.getScode() + "   " + sle.getSname();
                List<String> stime = sinfoService.findByCode(sle.getScode());
                if (stime.size() == 1){
                    if(snum.format(new Date()).equals((Integer.parseInt(getReplace(stime.get(0))))+""))
                        continue;
                    start = "&start=" + (Integer.parseInt(getReplace(stime.get(0))) + 1);
                }
                if (sle.getScode().startsWith("60"))
                    url = stock1 + "code=0" + sle.getScode() + start + stock2;
                else if (sle.getScode().startsWith("00"))
                    url = stock1 + "code=1" + sle.getScode() + start + stock2;
                System.out.println(start);
                List<SinfoEntity> sinfoList = templateToInterface.readCSVFileWithMap(url, sle.getSname());
                if (sinfoList.size() > 0){
                    int batchCount = 2000;
                    int batchLastIndex = batchCount;
                    List<List<SinfoEntity>> shareList = new ArrayList<>();
                    for (int index = 0; index < sinfoList.size(); ) {
                        if (batchLastIndex >= sinfoList.size()) {
                            batchLastIndex = sinfoList.size();
                            shareList.add(sinfoList.subList(index, batchLastIndex));
                            break;
                        } else {
                            shareList.add(sinfoList.subList(index, batchLastIndex));
                            // 设置下一批下标
                            index = batchLastIndex;
                            batchLastIndex = index + (batchCount - 1);
                        }
                    }
                        for (List<SinfoEntity> subList : shareList) {
                            //循环插入数据
                            st += sinfoService.saveData(subList); //保存数据
                        }
                }
                err = "";
                logger.info("保存股票--" + sle.getScode() + "--" + sle.getSname() +
                        "--数据：" + st + "  保存时间: " + sdf.format(new Date()));
                st = 0;
            }
        } catch (Exception e) {
            logger.error(err);
            e.printStackTrace();
        }
//        templateToInterface.readCSVFileWithMap(stock1 + "code=0600403&end="+stock2);
//        System.out.println(slist);
    }

    public String getReplace(String num) {
        num = num.replaceAll("[^\\d.]", "");
        if (num.length() == 6) //月和日都是1位
            num = num.substring(0, 4) + "0" + num.substring(4, 5) + "0" + num.substring(5);
        if (num.length() == 7) //月或日
            num = num.charAt(4) == 0 ? num.substring(0, 6) + "0" + num.substring(6) : num.substring(0, 4) + "0" + num.substring(4);
        return num;
    }
}
