package com.st.demo.service;

import com.st.demo.entity.SinfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Service
public class RestTemplateToInterface<T> {

    @Autowired(required = false)
    private RestTemplate restTemplate;

    /**
     * 以get方式请求第三方http接口 getForEntity
     * @param url
     * @return
     */
    public T doGetWith1(String url, Class<T> clt){
        ResponseEntity<T> responseEntity = restTemplate.getForEntity(url,clt);
        T user = (T) responseEntity.getBody();
        return user;
    }

    /**
     * 以get方式请求第三方http接口 getForObject
     * 返回值返回的是响应体，省去了我们再去getBody()
     * @param url
     * @return
     */
    public Object doGetWith2(String url){
        Object user  = restTemplate.getForObject(url, Object.class);
        return user;
    }

    /**
     * 以get方式请求第三方http接口 getForObject
     * 返回值返回的是响应体，省去了我们再去getBody()
     * @param url
     * @return
     */
    public HttpURLConnection doGetWith3(String url) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        // Accept 表示客户端支持什么格式的响应体
        httpHeaders.set(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        // Accept-Encoding 头，表示客户端接收gzip格式的压缩
//        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate");

        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), byte[].class);

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(5 * 1000);
                 conn.setDoOutput(true);
                 //get方式提交
                 conn.setRequestMethod("GET");
                 //凭借请求头文件
                 conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                 conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                 conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8");
                 conn.setRequestProperty("Connection", "keep-alive");
//                 conn.setRequestProperty("Cookie", "vjuids=c0b1df0b4.1607cbfc24e.0.4117b8e2e9a7d; _ntes_nuid=6043eb752fe64f531095359e3c269144; _ngd_tid=ocr30F19QRs9TGLtUFxJA5ZmNbNWmu9L; mail_psc_fingerprint=ed3554798217fa7da7d87acf8502a3af; nts_mail_user=zsz23187001@163.com:-1:1; _antanalysis_s_id=1579578567740; _ntes_nnid=6043eb752fe64f531095359e3c269144,1589782092302; usertrack=ezq0J17sgVwCP3/bAwTVAg==; UM_distinctid=17409d81b2f30c-0b66dc80f2fd0a-3323766-1fa400-17409d81b30731; NTES_CMT_USER_INFO=105495135%7Czsz23187%40163.com%7Chttp%3A%2F%2Fcms-bucket.nosdn.127.net%2F2018%2F08%2F13%2F078ea9f65d954410b62a52ac773875a1.jpeg%7Cfalse%7CenN6MjMxODdAMTYzLmNvbQ%3D%3D; s_n_f_l_n3=d61db0d3fd2a27d61607060437145; _ntes_stock_recent_=0600403%7C0688308%7C0600962%7C1003022%7C1000638%7C1300911; _ntes_stock_recent_=0600403%7C0688308%7C0600962%7C1003022%7C1000638%7C1300911; _ntes_stock_recent_=0600403%7C0688308%7C0600962%7C1003022%7C1000638%7C1300911; vjlast=1513921430.1609043860.11; ne_analysis_trace_id=1609840161081; vinfo_n_f_l_n3=d61db0d3fd2a27d6.1.1.1579578567665.1607048109710.1609840219416");
                 conn.setRequestProperty("Host", "quotes.money.163.com");
                 conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36");
        conn.connect();
                 return conn;
    }
    /**
     * 以post方式请求第三方http接口 postForEntity
     * @param url
     * @return
     */
    public String doPostWith1(String url){

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
        String body = responseEntity.getBody();
        return body;
    }

    /**
     * 以post方式请求第三方http接口 postForEntity
     * @param url
     * @return
     */
    public String doPostWith2(String url){
        String body = restTemplate.postForObject(url, null, String.class);
        return body;
    }

    /**
     * exchange
     * @return
     */
    public String doExchange(String url, Integer age, String name) throws JSONException {
        //header参数
        HttpHeaders headers = new HttpHeaders();
        String token = "asdfaf2322";
        headers.add("authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        //放入body中的json参数
        JSONObject obj = new JSONObject();
        obj.put("age", age);
        obj.put("name", name);

        //组装
        HttpEntity<JSONObject> request = new HttpEntity<>(obj, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        String body = responseEntity.getBody();
        return body;
    }

    public List<SinfoEntity> readCSVFileWithMap(String filePath, String name) throws Exception {
        List<SinfoEntity> slist = new ArrayList<>();
        URL httpurl = new URL(filePath);
        HttpURLConnection conn = (HttpURLConnection) httpurl.openConnection();
        conn.connect();
        BufferedReader reader2 = new BufferedReader(
                new InputStreamReader(httpurl.openStream(), "gbk"));
        String s;
        while ((s = reader2.readLine()) != null) {
            String[] ss = s.split(",");
            //不是第一行 开始处理数据
            if(!ss[0].equals("日期")){
                SinfoEntity se = new SinfoEntity();
                se.setStime(ss[0]); //日期
                se.setScode(StringUtils.trimAllWhitespace(ss[1]).substring(1)); //代码
                se.setSname(name); //名称StringUtils.trimAllWhitespace(ss[2])
                se.setSclose(Double.parseDouble(ss[3])); //收盘价
                se.setShigh(Double.parseDouble(ss[4])); //当日最高价
                se.setSlow(Double.parseDouble(ss[5])); //当日最低价
                se.setSopen(Double.parseDouble(ss[6])); //开盘价
                se.setSlclose(Double.parseDouble(ss[7])); //前日收盘价
                se.setSchg(Double.parseDouble(ss[8].toLowerCase().equals("none")? "0":ss[8])); //涨跌额
                se.setSpchg(Double.parseDouble(ss[9].toLowerCase().equals("none")? "0":ss[9])); //涨跌幅
                se.setSurnover(Double.parseDouble(ss[10].toLowerCase().equals("none")? "0":ss[10])); //换手率
                se.setSotur(Double.parseDouble(ss[11].toLowerCase().equals("none")? "0":ss[11])); //成交量
                se.setSvatur(Double.parseDouble(ss[12].toLowerCase().equals("none")? "0":ss[12])); //成交额
                se.setStcap(Double.parseDouble(ss[13].toLowerCase().equals("none")? "0":ss[13])); //总市值
                se.setSmcap(Double.parseDouble(ss[14].toLowerCase().equals("none")? "0":ss[14])); //流通市值
                slist.add(se);
            }
        }
        reader2.close();
        conn.disconnect();
        return slist;
    }

}

