package com.st.demo.util;

import com.st.demo.entity.SIndexEntity;
import com.st.demo.entity.SinfoEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.*;

public class CommUtil {
    public static String getReplace(String num) {
        num = num.replaceAll("[^\\d.]", "");
        if (num.length() == 6) //月和日都是1位
            num = num.substring(0, 4) + "0" + num.substring(4, 5) + "0" + num.substring(5);
        if (num.length() == 7) //月或日
            num = num.charAt(4) == 0 ? num.substring(0, 6) + "0" + num.substring(6) : num.substring(0, 4) + "0" + num.substring(4);
        return num;
    }

    /**
     * Calculate EMA,
     *
     * @param list :Price list to calculate，the first at head, the last at tail.
     * @return
     */
    public static final Double getEXPMA(final List<Double> list, final int number) {
        // 开始计算EMA值，
        Double k = 2d / (number + 1d);// 计算出序数
        Double ema = list.get(0);// 第一天ema等于当天收盘价
        for (int i = 1; i < list.size(); i++) {
            // 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
            ema = list.get(i) * k + ema * (1 - k);
        }
        return ema;
    }

    /**
     * Calculate MA,
     *
     * @param list :Price list to calculate，the first at head, the last at tail.
     * @return
     */
    public static final Double getMA(final List<Double> list, int num) {
        Double ema = 0d;//
//        for (int i = 0; i <= list.size() - num; i++) {
//            ema = 0d;
//            for (int j = i; j < num + i; j++) {
//                ema += list.get(j);
//            }
//            ema = ema / num;
//        }
        if (num > list.size()) {
            for (int i = 0; i < list.size(); i++) {
                ema += list.get(i);
            }
            if (list.size() < 1)
                return 0d;
            else
                return ema == 0d ? 0d : ema / list.size();
        }

        for (int i = list.size() - num; i < list.size(); i++) {
            ema += list.get(i);
        }
        ema = ema / num;
        return ema;
    }

    /**
     * Calculate SMA,
     *
     * @param list :Price list to calculate，the first at head, the last at tail.
     * @return
     */
    public static final Double getSMA(final List<Double> list, final int number) {
        // 开始计算EMA值，
        Double k = 1d / (number);// 计算出序数
        Double ema = list.get(0);// 第一天ema等于当天收盘价
        for (int i = 1; i < list.size(); i++) {
            // 第二天以后，当天收盘 收盘价乘以系数再加上昨天EMA乘以系数-1
            ema = list.get(i) * k + ema * (1 - k);
        }
        return ema;
    }

    /**
     * calculate MACD values
     *
     * @param list        :Price list to calculate，the first at head, the last at tail.
     * @param shortPeriod :the short period value.
     * @param longPeriod  :the long period value.
     * @param midPeriod   :the mid period value.
     * @return
     */
    public static final HashMap<String, Double> getMACD(final List<Double> list, final int shortPeriod, final int longPeriod, int midPeriod) {
        HashMap<String, Double> macdData = new HashMap<String, Double>();
        List<Double> diffList = new ArrayList<Double>();
        Double shortEMA = 0.0;
        Double longEMA = 0.0;
        Double dif = 0.0;
        Double dea = 0.0;

        for (int i = list.size() - 1; i >= 0; i--) {
            List<Double> sublist = list.subList(0, list.size() - i);
            shortEMA = getEXPMA(sublist, shortPeriod);
            longEMA = getEXPMA(sublist, longPeriod);
            dif = shortEMA - longEMA;
            diffList.add(dif);
        }
        dea = getEXPMA(diffList, midPeriod);
        macdData.put("DIF", dif);
        macdData.put("DEA", dea);
        macdData.put("MACD", (dif - dea) * 2);
        return macdData;
    }


    //生成文本文件
    public static void method1(List<List<SinfoEntity>> rlist) {
        FileWriter fw = null;
        try {
//如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File("D:\\stest\\dd.txt");
            if (f.exists())
                f.delete();
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println("代码,名称,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,幅度");
        for (int i = 0; i < rlist.size(); i++) {
            List<SinfoEntity> slist = rlist.get(i);
            pw.print(slist.get(0).getScode() + ",");
            pw.print(slist.get(0).getSname() + ",");
            for (int j = 0; j < slist.size(); j++) {
                pw.print(slist.get(j).getStime() + ",");
                pw.print(slist.get(j).getSopen() + ",");
                pw.print(slist.get(j).getSclose() - slist.get(j).getSopen() >= 0 ? 1 + "," : 0 + ",");
            }
            pw.print((slist.get(slist.size() - 1).getSopen() - slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen());
            pw.println();
        }
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //生成文本文件
    public static void method2(List<List<SinfoEntity>> rlist) {
        FileWriter fw = null;
        try {
//如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File("D:\\stest\\s" + rlist.get(0).get(rlist.get(0).size() - 1).getTtime() + ".txt");
            if (f.exists())
                f.delete();
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> outp = new ArrayList<>();
        PrintWriter pw = new PrintWriter(fw);
//        pw.println();
        outp.add("代码,名称,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,幅度,m1,m2,m3,m正负,前交易,交易量,平均量,量差,收入");
        for (int i = 0; i < rlist.size(); i++) {
            String ots = "";
            List<SinfoEntity> slist = rlist.get(i);
//            pw.print(slist.get(0).getScode() + ",");
//            pw.print(slist.get(0).getSname() + ",");
            ots += slist.get(0).getScode() + ",";
            ots += slist.get(0).getSname() + ",";
            for (int j = 3; j < slist.size(); j++) {
                ots += slist.get(j).getStime() + ",";
                ots += slist.get(j).getSopen() + ",";
                ots += slist.get(j).getSclose() - slist.get(j).getSopen() >= 0 ? 1 + "," : 0 + ",";
//                pw.print(slist.get(j).getStime() + ",");
//                pw.print(slist.get(j).getSopen() + ",");
//                pw.print(slist.get(j).getSclose() - slist.get(j).getSopen() >= 0 ? 1 + "," : 0 + ",");
            }
            ots += (slist.get(slist.size() - 1).getSopen() - slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen() + ",";
//            pw.print((slist.get(slist.size() - 1).getSopen() - slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen()+",");
            double avob = 0d;
            for (int j = 0; j < slist.size() - 2; j++) {
                avob += slist.get(j).getSotur();
            }

            avob = avob / 5;
            double[] md = new double[3];
            for (int j = 0; j < 3; j++) {
                SinfoEntity s1 = slist.get(j);
                SinfoEntity s2 = slist.get(j + 1);
                SinfoEntity s3 = slist.get(j + 2);
                double avg = (s1.getSopen() + s2.getSopen() + s3.getSopen()) / 3;
                double a1 = s1.getSopen() - avg > 0 ? Math.log(Math.abs(s1.getSopen() - avg) * 100) : -Math.log(Math.abs(s1.getSopen() - avg) * 100);
                double a2 = s2.getSopen() - avg > 0 ? Math.log(Math.abs(s2.getSopen() - avg) * 100) : -Math.log(Math.abs(s2.getSopen() - avg) * 100);
                double a3 = s3.getSopen() - avg > 0 ? Math.log(Math.abs(s3.getSopen() - avg) * 100) : -Math.log(Math.abs(s3.getSopen() - avg) * 100);
                double m = a1 + a2 + a3;
                md[j] = m;
                ots += m + ",";
//                pw.print(m+",");
//                pw.print(slist.get(j).getSopen() + ",");
//                pw.print(slist.get(j).getSclose() - slist.get(j).getSopen() >= 0 ? 1 + "," : 0 + ",");
            }

            if (md[2] > 0 && md[1] < 0)
//                pw.print(1+",");
                ots += 1 + ",";
            else
//                pw.print(0+",");
                ots += 0 + ",";
            ots += slist.get(slist.size() - 4).getSotur() + ","; //前一天交易量
            ots += slist.get(slist.size() - 3).getSotur() + ",";
            ots += avob + ",";
            ots += slist.get(slist.size() - 3).getSotur() > avob ? 1 : 0;
//            pw.print(slist.get(slist.size()-3).getSotur()+",");
//            pw.print(avob+",");
//            pw.print(slist.get(slist.size()-3).getSotur()>avob?1:0);
//            pw.println();
            ots += ",";
            ots += 10000 * (slist.get(slist.size() - 1).getSopen() -
                    slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen() + 10000 - 10000 / slist.get(slist.size() - 2).getSopen() * 0.04;
            if (slist.get(slist.size() - 3).getSotur() > avob && md[2] > 0 && md[1] < 0 && md[0] > 0)
                outp.add(ots);
        }
        for (String s : outp) {
            pw.println(s);
        }
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //生成文本文件
    public static void method3(List<List<SinfoEntity>> rlist) {
        FileWriter fw = null;
        try {
//如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File("D:\\stest\\s" + rlist.get(0).get(rlist.get(0).size() - 1).getTtime() + ".txt");
            if (f.exists())
                f.delete();
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> outp = new ArrayList<>();
        PrintWriter pw = new PrintWriter(fw);
//        pw.println();
        outp.add("代码,名称,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,幅度,m1,m2,m3,m正负,前交易,交易量,平均量,量差,收入,涨大于收,涨大于开,跌大于收,跌大于开");
        for (int i = 0; i < rlist.size(); i++) {
            String ots = "";
            List<SinfoEntity> slist = rlist.get(i);
            ots += slist.get(0).getScode() + ",";
            ots += slist.get(0).getSname() + ",";
            for (int j = 3; j < slist.size(); j++) {
                ots += slist.get(j).getStime() + ",";
                ots += slist.get(j).getSopen() + ",";
                ots += slist.get(j).getSclose() - slist.get(j).getSopen() >= 0 ? 1 + "," : 0 + ",";
            }
            ots += (slist.get(slist.size() - 1).getSopen() - slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen() + ",";
            double avob = 0d;
            double sopen = 0d, sclose = 0d;
            for (int j = 0; j < slist.size() - 2; j++) {
                avob += slist.get(j).getSotur();
                sopen += slist.get(j).getSopen();
                sclose += slist.get(j).getSclose();
            }
            sopen = sopen / 5;
            sclose = sclose / 5;
            avob = avob / 5;
            double[] md = new double[3];
            for (int j = 0; j < 3; j++) {
                SinfoEntity s1 = slist.get(j);
                SinfoEntity s2 = slist.get(j + 1);
                SinfoEntity s3 = slist.get(j + 2);
                double avg = (s1.getSopen() + s2.getSopen() + s3.getSopen()) / 3;
                double a3 = s3.getSopen() - avg > 0 ? Math.log(Math.abs(s3.getSopen() - avg) / s3.getSopen() * 100) : Math.log(0.01 + Math.abs(s3.getSopen() - avg) / s3.getSopen() * 100);
                md[j] = a3;
                ots += a3 + ",";
            }

            if (md[2] > 0 && md[1] < 0)
                ots += 1 + ",";
            else
                ots += 0 + ",";
            ots += slist.get(slist.size() - 4).getSotur() + ","; //前一天交易量
            ots += slist.get(slist.size() - 3).getSotur() + ",";
            ots += avob + ",";
            ots += slist.get(slist.size() - 3).getSotur() > avob ? 1 : 0;
            ots += ",";
            ots += 10000 * (slist.get(slist.size() - 1).getSopen() -
                    slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen() + 10000 - 10000 / slist.get(slist.size() - 2).getSopen() * 0.04;
            //当日涨且收盘大于收盘价的boll5日，开盘小于boll5日
            if (slist.get(slist.size() - 3).getSclose() > sclose && slist.get(slist.size() - 3).getSopen() < sclose)
                ots += ",1";
            else
                ots += ",0";
            //当日涨且收盘大于开盘价的boll5日，开盘小于boll5日
            if (slist.get(slist.size() - 3).getSclose() > sopen && slist.get(slist.size() - 3).getSopen() < sopen)
                ots += ",1";
            else
                ots += ",0";
            //当日跌且开盘大于收盘价的boll5日，收盘盘小于boll5日
            if (slist.get(slist.size() - 3).getSopen() > sclose && slist.get(slist.size() - 3).getSclose() < sclose)
                ots += ",1";
            else
                ots += ",0";
            //当日跌且开盘大于开盘价的boll5日，收盘小于boll5日
            if (slist.get(slist.size() - 3).getSopen() > sopen && slist.get(slist.size() - 3).getSclose() < sopen)
                ots += ",1";
            else
                ots += ",0";
            if (slist.get(slist.size() - 3).getSotur() > avob && md[2] > 0 && md[1] > 0 && md[0] > 0)
                outp.add(ots);
        }
        for (String s : outp) {
            pw.println(s);
        }
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //生成文本文件
    public static void method4(List<List<SinfoEntity>> rlist) {
        FileWriter fw = null;
        try {
//如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File("D:\\stest\\dd.txt");
            if (f.exists())
                f.delete();
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);
//        pw.println("代码,名称,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,幅度");
        for (int i = 0; i < rlist.size(); i++) {

            String sout = "{";
            List<SinfoEntity> slist = rlist.get(i);
            sout += "name:'" + slist.get(0).getSname() + i + "',";
            sout += "title:'" + slist.get(0).getScode() + "',";
            sout += "type:'line',stock:'总量',";
            sout += "data:[";
            double avob = 0d;
            for (int j = 0; j < slist.size() - 2; j++) {
                avob += slist.get(j).getSopen();
            }
            avob = avob / 5;

            for (int j = 0; j < slist.size(); j++) {
                sout += slist.get(j).getSopen() / avob - 1 + ",";
//                pw.print(slist.get(j).getStime() + ",");
//                pw.print(slist.get(j).getSopen() + ",");
//                pw.print(slist.get(j).getSclose() - slist.get(j).getSopen() >= 0 ? 1 + "," : 0 + ",");
            }
            sout = sout.substring(0, sout.length() - 1) + "]},";
//            pw.print((slist.get(slist.size() - 1).getSopen() - slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen());
//            pw.println();
            pw.println(sout);
        }
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //收盘后 当日收盘大于开盘，且收盘大于布林中线，开盘小于布林中线，2天前的最高价<布林中线*1.05
    //收盘后 当日收盘小于开盘，且开盘大于布林中线，收盘小于布林中线，2天前的最高价<布林中线*1.05
    //该股票expma呈现上升趋势比较好，最好expma12>expma50，下降趋势也可
    //zlmm公式里面的mms和mmm大于mml最好，起码也得有上升趋势
    //第二天开盘价<前日的expma12最好，小于expma50也可
    //直接开盘买


    //生成文本文件

    /**
     * @param rlist
     * @param shortnum macd的短日期 12
     * @param langnum  macd的长日期 26
     * @param mid      macd的平均日，一般为9
     * @param t        交易日
     * @param maxn     从数据的倒数第n天开始统计
     */
    public static void method5(List<List<SinfoEntity>> rlist, int shortnum, int langnum,
                               int mid, int t, int maxn) {
        FileWriter fw = null;
        try {
//如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File("D:\\stest\\macd.txt");
            if (f.exists())
                f.delete();
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> outText = new ArrayList<>();

        //每个股票的指标数据，作为选股条件
        List<List<SIndexEntity>> sindexList = new ArrayList<>();
//        pw.println("代码,名称,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,日期,open,涨跌,幅度");
        for (int i = 0; i < rlist.size(); i++) {
            List<SIndexEntity> indexList = new ArrayList<>();
            List<Map<String, Double>> macd = new ArrayList<>();
            List<Double> boll = new ArrayList<>();
            List<Double[]> zlmm = new ArrayList<>();
            List<Double> ema12 = new ArrayList<>();
            List<Double> ema50 = new ArrayList<>();
            List<Double> avgVatur = new ArrayList<>(); //n日交易额平均值
            List<SinfoEntity> slist = rlist.get(i);

            maxn = slist.size() - maxn;
            //最近27日的收盘价
            for (int j = maxn; j < slist.size(); j++) {
                List<Double> closeList = new ArrayList<>();
                for (int k = 0; k <= j; k++) {
                    closeList.add(slist.get(k).getSclose());
                }
//                String so = "";
//                so += slist.get(j).getScode() + "," + slist.get(j).getSname() + "," +
//                        slist.get(j).getStime() + ",";
                macd.add(getMACD(closeList, shortnum, langnum, mid));
                zlmm.add(getZLMM(closeList));
                boll.add(getBoll(closeList, 21));
                avgVatur.add(getMA(closeList, 12));
                ema12.add(getEXPMA(closeList, 12));
                ema50.add(getEXPMA(closeList, 50));
//                so += getNumFormat(macd.get(j - maxn).get("DIF"))
//                        + "," + getNumFormat(macd.get(j - maxn).get("DEA"))
//                        + "," + getNumFormat(macd.get(j - maxn).get("MACD"));
//                so += "," + getNumFormat(zlmm.get(j - maxn)[0])
//                        + "," + getNumFormat(zlmm.get(j - maxn)[1])
//                        + "," + getNumFormat(zlmm.get(j - maxn)[2]);
//                so += "," + getNumFormat(boll.get(j - maxn));
//                so += "," + getNumFormat(ema12.get(j - maxn));
//                so += "," + getNumFormat(ema50.get(j - maxn));
//                outText.add(so);
                //统计每个股票最近maxn天的指标信息
                SIndexEntity sindex = copySinfo(slist.get(j - maxn));
                sindex.setMacd(getNumDouble(macd.get(j - maxn).get("MACD")));
                sindex.setDif(getNumDouble(macd.get(j - maxn).get("DIF")));
                sindex.setDea(getNumDouble(macd.get(j - maxn).get("DEA")));
                sindex.setEma12(getNumDouble(ema12.get(j - maxn)));
                sindex.setEma50(getNumDouble(ema50.get(j - maxn)));
                sindex.setBoll(getNumDouble(boll.get(j - maxn)));
                sindex.setMms(getNumDouble(zlmm.get(j - maxn)[0]));
                sindex.setMmm(getNumDouble(zlmm.get(j - maxn)[1]));
                sindex.setMml(getNumDouble(zlmm.get(j - maxn)[2]));
                sindex.setAvgvatur(getNumDouble(avgVatur.get(j - maxn)));
                indexList.add(sindex);
            }
            sindexList.add(indexList);
        }
        //开始选股，第一步只选最后一天符合要求
        List<List<SIndexEntity>> okStock = new ArrayList<>();
        for (int i = 0; i < sindexList.size(); i++) { //每个股票
            List<SIndexEntity> ilist = sindexList.get(i);
            int tday = ilist.size() - 1;
            //如果12日ema<50日ema 去掉
            if (ilist.get(tday).getEma12() >= ilist.get(tday).getEma50()) {
                if (ilist.get(tday - 2).getShigh() <= ilist.get(tday).getBoll()) {
                    if (ilist.get(tday).getSopen() > ilist.get(tday).getBoll() && ilist.get(tday).getSclose() < ilist.get(tday).getBoll()) {
                        okStock.add(ilist);
                    }
                }
            }
        }
        for (int i = 0; i < okStock.size(); i++) {
            String os = "";
            SIndexEntity dlast = okStock.get(i).get(okStock.get(i).size()-1);
            SIndexEntity dsecond = okStock.get(i).get(okStock.get(i).size()-2);
            os+=dlast.getScode()+","+dlast.getSname()+","+dlast.getStime()+",";
            os+=dlast.getDif()+","+dlast.getDea()+","+dlast.getMacd()+",";
            os+=dlast.getMms()+","+dlast.getMmm()+","+dlast.getMml()+",";
            os+=dlast.getBoll()+","+dlast.getEma12()+","+dlast.getEma50();
            outText.add(os);
        }

        PrintWriter pw = new PrintWriter(fw);
        pw.println("代码,名称,日期,DIF,DEA,MACD,MMS,MMM,MML,BOLL,EMA12,EMA50");
        for (String s : outText) {
            pw.println(s);
        }
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //计算公式
    //LC :=REF(CLOSE,1);
    //RSI2:=SMA(MAX(CLOSE-LC,0),12,1)/SMA(ABS(CLOSE-LC),12,1)*100;
    //RSI3:=SMA(MAX(CLOSE-LC,0),18,1)/SMA(ABS(CLOSE-LC),18,1)*100;
    //MMS:MA(3*RSI2-2*SMA(MAX(CLOSE-LC,0),16,1)/SMA(ABS(CLOSE-LC),16,1)*100,3);
    //MMM:EMA(MMS,8);
    //MML:MA(3*RSI3-2*SMA(MAX(CLOSE-LC,0),12,1)/SMA(ABS(CLOSE-LC),12,1)*100,5);
    public static Double[] getZLMM(List<Double> slist) {
        Double[] zlmm = new Double[3];
        List<Double> rsi2 = new ArrayList<>();
        List<Double> rsi3 = new ArrayList<>();
        List<Double> mms = new ArrayList<>();

        for (int i = slist.size() - 1; i >= 0; i--) {
            List<Double> maxCloseList = new ArrayList<>();
            List<Double> absCloseList = new ArrayList<>();
            List<Double> sublist = slist.subList(0, slist.size() - i);
            for (int k = 0; k < sublist.size(); k++) {
                if (k == 0) { //数据不是从第一天开始就不能统计0
//                    maxCloseList.add(0d);
//                    absCloseList.add(0d);
                } else {
                    double lc = slist.get(k - 1);
                    double max = maxNum(slist.get(k) - lc, 0d);
                    double abs = Math.abs(slist.get(k) - lc);
                    maxCloseList.add(max);
                    absCloseList.add(abs);
                }
            }
            if (maxCloseList.size() > 0 && absCloseList.size() > 0) { //&& sublist.size()>1 数据从第一天开始的条件
                double si2 = getSMA(maxCloseList, 12) / getSMA(absCloseList, 12) * 100;
                double si3 = getSMA(maxCloseList, 18) / getSMA(absCloseList, 18) * 100;
                rsi2.add(3 * si2 - 2 * getSMA(maxCloseList, 16) / getSMA(absCloseList, 16) * 100);
                rsi3.add(3 * si3 - 2 * getSMA(maxCloseList, 12) / getSMA(absCloseList, 12) * 100);
                mms.add(getMA(rsi2, 3));
            }
        }
        zlmm[0] = getMA(rsi2, 3);
        if (mms.size() < 1)
            zlmm[1] = 0d;
        else
            zlmm[1] = getEXPMA(mms, 8);
        zlmm[2] = getMA(rsi3, 5);
//        for (int i = start; i < slist.size(); i++) {
//            List<Double> maxCloseList = new ArrayList<>();
//            List<Double> absCloseList = new ArrayList<>();
//            for (int k = 1; k <= start; k++) {
//                double lc = slist.get(k - 1).getSclose();
//                double max = maxNum(slist.get(k).getSclose() - lc, 0d);
//                double abs = Math.abs(slist.get(k).getSclose() - lc);
//                maxCloseList.add(max);
//                absCloseList.add(abs);
//            }
//            double si2 = getSMA(maxCloseList,12)/getSMA(absCloseList,12)*100;
//            double si3 = getSMA(maxCloseList,18)/getSMA(absCloseList,18)*100;
//            rsi2.add(3*si2-2*getSMA(maxCloseList,16)/getSMA(absCloseList,16)*100);
//            rsi3.add(3*si3-2*getSMA(maxCloseList,12)/getSMA(absCloseList,12)*100);
//            mms.add(getMA(rsi2,3));
//            zlmm[0] = getMA(rsi2,3);
//            zlmm[1] = getEXPMA(mms,8);
//            zlmm[2] = getMA(rsi3,5);
//        }
        return zlmm;
    }

    public static double maxNum(double a, double b) {
        double d = 0d;
        d = a > b ? a : b;
        return d;
    }

    /**
     * 柏林线，ma(close,n)
     *
     * @param slist
     * @param num
     * @return
     */
    public static double getBoll(List<Double> slist, int num) {
        double boll = 0d;
        boll = getMA(slist, num);
        return boll;
    }

    public static String getNumFormat(double d) {
        BigDecimal b = new BigDecimal(d);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.valueOf(f1);
    }

    public static Double getNumDouble(double d) {
        BigDecimal b = new BigDecimal(d);
        double f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    public static SIndexEntity copySinfo(SinfoEntity sinfo) {
        SIndexEntity re = new SIndexEntity();
        re.setId(sinfo.getId());
        re.setTcode(sinfo.getTcode());
        re.setTtime(sinfo.getTtime());
        re.setStime(sinfo.getStime());
        re.setScode(sinfo.getScode());
        re.setSname(sinfo.getSname());
        re.setSclose(sinfo.getSclose());
        re.setShigh(sinfo.getShigh());
        re.setSlow(sinfo.getSlow());
        re.setSopen(sinfo.getSopen());
        re.setSchg(sinfo.getSchg());
        re.setSpchg(sinfo.getSpchg());
        re.setSurnover(sinfo.getSurnover());
        re.setSotur(sinfo.getSotur());
        re.setSvatur(sinfo.getSvatur());
        return re;
    }
}
