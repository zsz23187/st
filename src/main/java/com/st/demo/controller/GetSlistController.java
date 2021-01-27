package com.st.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.demo.entity.SinfoEntity;
import com.st.demo.entity.SlistEntity;
import com.st.demo.service.RestTemplateToInterface;
import com.st.demo.service.SinfoService;
import com.st.demo.service.SlistService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 定期获取股票列表，并进行对比更新
 */
@RestController
@RequestMapping(value = "/getStock")
public class GetSlistController {

    private Logger logger = LoggerFactory.getLogger(StockController.class);
    @Resource
    private RestTemplateToInterface<String> templateToInterface;
    @Resource
    private SlistService slistService;
    @Resource
    private SinfoService sinfoService;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    private static SimpleDateFormat snum = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat symd = new SimpleDateFormat("yyyy-MM-dd");
    //股票列表请求连接
    private String listUrl = "http://21.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112407750807719818911_1609121370291&pn=1&pz=9999&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:0+t:6,m:0+t:13,m:0+t:80,m:1+t:2,m:1+t:23&fields=f12,f14,f26&_=1609121370292";
    //股票历史数据请求链接
    private String stock1 = "http://quotes.money.163.com/service/chddata.html?";
    //    code=1600403&end=20180413
    private String stock2 = "&end=&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";

    private String stockD = "http://api.money.126.net/data/feed/"; //+code

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
                    (f.get("f12").toString().startsWith("60") ||
                            f.get("f12").toString().startsWith("00"))
                            && !"-".equals(f.get("f26").toString())
                            && Integer.parseInt(f.get("f26").toString()) < Integer.parseInt(getFday(snum))).map(map -> {
                SlistEntity slistEntity = new SlistEntity();
                slistEntity.setScode(map.get("f12").toString());
                slistEntity.setSname(map.get("f14").toString());
                return slistEntity;
            }).collect(Collectors.toCollection(LinkedList::new));
            //目前存在的退市股票
            List<SlistEntity> stui = slistService.findDelAll();
            //从列表中减去退市的股票
            slist = slist.stream().filter(new Predicate<SlistEntity>() {
                @Override
                public boolean test(SlistEntity student) {
                    for (SlistEntity boy : stui) {
                        if (student.getScode().equals(boy.getScode())) {
                            return false;
                        }
                    }
                    return true;
                }
            }).collect(Collectors.toList());

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
            if (delOld.size() > 0) {
                int d1 = slistService.deleteData(delOld); //删掉不存在的旧的
                List<SinfoEntity> ss = new ArrayList<>();
                delOld.parallelStream().forEach(f -> {
                    SinfoEntity p = new SinfoEntity();
                    p.setScode(f.getScode());
                    ss.add(p);
                });
                int d2 = sinfoService.deleteData(ss);
                logger.info("删除股票的个数：" + d1 + "  删除时间：" + sdf.format(new Date()));
                logger.info("删除股票的数据量：" + d2 + "  删除时间：" + sdf.format(new Date()));
            }
            //新的-旧的，保存新的股票
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

    /**
     * 获取当日开盘数据
     *
     * @param response
     * @param request
     * @throws Exception
     */
    @GetMapping(value = "/getStockD")
    public void getStockD(HttpServletResponse response, HttpServletRequest request) throws Exception {
        ObjectMapper objm = new ObjectMapper();
        List<SlistEntity> slistAll = slistService.findAll();
        List<SinfoEntity> sday = new ArrayList<>();
        String url = stockD;
        int st = 0;
        sinfoService.deleteDay();
        for (int i = 0; i < slistAll.size(); i++) {
            SlistEntity se = slistAll.get(i);
            if (se.getScode().startsWith("60"))
                url = "0" + se.getScode();
            else if (se.getScode().startsWith("00"))
                url = "1" + se.getScode();
            String json = templateToInterface.doGetWith1(stockD+url, String.class);
            Map mp = null;
            mp = objm.readValue(json.substring(json.indexOf("(") + 1, json.length() - 2), Map.class);
            if (mp.size() > 0) {
                SinfoEntity sie = new SinfoEntity();
                sie.setScode(se.getScode());
                sie.setSname(se.getSname());
                sie.setSopen(((Map)mp.get(url)).get("open").toString().toLowerCase().equals("null") ?
                        0d : Double.parseDouble(((Map)mp.get(url)).get("open").toString()));
                sday.add(sie);
            }
        }
        if (sday.size() > 0) {
            st = sinfoService.saveDataDay(sday);
        }

        logger.info("保存当日开盘信息个数：" + st + "  保存时间: " + sdf.format(new Date()));
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
            for (int i = delOld.size() - 1; i > 0; i--) {
                start = "";
                SlistEntity sle = delOld.get(i);
                err = sle.getScode() + "   " + sle.getSname();
                //获取最后一次股票更新时间
                List<String> stime = sinfoService.findByCode(sle.getScode());
                if (stime.size() == 1) { //如果最后一天等于当天，跳过这个股票，否则获取日期+1
                    if (snum.format(new Date()).equals((Integer.parseInt(getReplace(stime.get(0)))) + ""))
                        continue;
                    start = "&start=" + (Integer.parseInt(getReplace(stime.get(0))) + 1);
                }
                if (sle.getScode().startsWith("60"))
                    url = stock1 + "code=0" + sle.getScode() + start + stock2;
                else if (sle.getScode().startsWith("00"))
                    url = stock1 + "code=1" + sle.getScode() + start + stock2;
                System.out.println(start);
                List<SinfoEntity> sinfoList = templateToInterface.readCSVFileWithMap(url, sle.getSname());
                if (sinfoList.size() > 0) { //分段保存，每2000条保存一次
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
            logger.info("保存信息结束" + sdf.format(new Date()));
            System.out.println("over");
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

    public String getFday(SimpleDateFormat symd) {
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String firstDay = symd.format(cal_1.getTime());
        return firstDay;
    }

    //查询已经退市的股票并入库
    @GetMapping(value = "/getDel")
    public void getDelStock() {
        String firstDay = getFday(symd);

        List<String> ds = sinfoService.findDelCode(firstDay);
        //数据库存在的
        List<SlistEntity> st = slistService.findDelAll();

        //ds-st 得到数据库里面不存在的退市股票
        List<SlistEntity> delNew = new ArrayList<>();
        ds.stream().forEach(m -> {
            boolean flag = true;
            for (SlistEntity boy : st) {
                if (m.equals(boy.getScode())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                SlistEntity se = new SlistEntity();
                se.setScode(m);
                delNew.add(se);
            }
        });
        //保存新的退市股票
        int sp = slistService.saveData(delNew);
        logger.info("新增退市股票：" + sp + "  时间" + sdf.format(new Date()));
    }

    public static void main(String[] args) {
        StockController gs = new StockController();
        Calendar cal_1 = Calendar.getInstance();//获取当前日期
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String firstDay = symd.format(cal_1.getTime());
        System.out.println(firstDay);
    }

    @GetMapping(value = "/getTest")
    public void getStockT(HttpServletResponse response, HttpServletRequest request) {
        List<SlistEntity> stockList = new ArrayList<>();
        SlistEntity sl = new SlistEntity();
        sl.setScode("605368");
        stockList.add(sl);
        List<SinfoEntity> ss = new ArrayList<>();
        stockList.parallelStream().forEach(f -> {
            SinfoEntity p = new SinfoEntity();
            p.setScode(f.getScode());
            ss.add(p);
        });
        int p = sinfoService.deleteData(ss);
        System.out.println("xxx");
    }
}