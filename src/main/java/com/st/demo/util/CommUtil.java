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

    /**
     * @param rlist
     * @param shortnum macd的短日期 12
     * @param langnum  macd的长日期 26
     * @param mid      macd的平均日，一般为9
     * @param tsell    交易日
     * @param tnum     从数据的倒数第n天开始统计
     */
    public static void method5(List<List<SinfoEntity>> rlist, int shortnum, int langnum,
                               int mid, int tsell, int tnum) {
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
            System.out.println("开始分析-" + i);
            List<SIndexEntity> indexList = new ArrayList<>();
            List<Map<String, Double>> macd = new ArrayList<>();
            List<Double> boll = new ArrayList<>();
            List<Double[]> zlmm = new ArrayList<>();
            List<Double> ema12 = new ArrayList<>();
            List<Double> ema50 = new ArrayList<>();
            List<Double> avgVatur = new ArrayList<>(); //n日交易额平均值
            List<Double> avgotur = new ArrayList<>(); //n日交易量平均值
            List<SinfoEntity> slist = rlist.get(i);

            int maxn = slist.size() - tnum;
            //最近27日的收盘价
            for (int j = maxn; j < slist.size(); j++) {
                List<Double> closeList = new ArrayList<>();
                for (int k = 0; k <= j; k++) {
                    closeList.add(slist.get(k).getSclose());
                }
                String so = "";
                so += slist.get(j).getScode() + "," + slist.get(j).getSname() + "," +
                        slist.get(j).getStime() + ",";
                macd.add(getMACD(closeList, shortnum, langnum, mid));
                zlmm.add(getZLMM(closeList));
                boll.add(getBoll(closeList, 21));
                avgVatur.add(getMA(closeList, 12));
                avgotur.add(getMA(closeList, 12));
                ema12.add(getEXPMA(closeList, 12));
                ema50.add(getEXPMA(closeList, 50));
                so += getNumFormat(macd.get(j - maxn).get("DIF"))
                        + "," + getNumFormat(macd.get(j - maxn).get("DEA"))
                        + "," + getNumFormat(macd.get(j - maxn).get("MACD"));
                so += "," + getNumFormat(zlmm.get(j - maxn)[0])
                        + "," + getNumFormat(zlmm.get(j - maxn)[1])
                        + "," + getNumFormat(zlmm.get(j - maxn)[2]);
                so += "," + getNumFormat(boll.get(j - maxn));
                so += "," + getNumFormat(ema12.get(j - maxn));
                so += "," + getNumFormat(ema50.get(j - maxn));
//                outText.add(so);
                //统计每个股票最近maxn天的指标信息
                SIndexEntity sindex = copySinfo(slist.get(j));
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
                sindex.setAvgotur(getNumDouble(avgotur.get(j - maxn)));
                indexList.add(sindex);
            }
            sindexList.add(indexList);
        }
        rlist = null;
        System.out.println("开始选股");
        //开始选股，一定时间内每天符合条件的都选出来
        /*
        List<List<SIndexEntity>> okStock = new ArrayList<>();
        for (int i = 4; i < tnum; i++) {
            List<SIndexEntity> dayList = new ArrayList<>(); //每天的满足条件的股票
            for (int j = 0; j < sindexList.size(); j++) {
                List<SIndexEntity> ilist = sindexList.get(j);
                if (ilist.get(i).getEma12() >= ilist.get(i).getEma50()) {
                    if (ilist.get(i - 2).getShigh() <= ilist.get(i).getBoll() * 1.05) {
                        if (ilist.get(i).getSopen() > ilist.get(i).getBoll() && ilist.get(i).getSclose() < ilist.get(i).getBoll()) {
                            ilist.get(i).setIsok(true);
                            ilist.get(i).setWeight(calWeight(ilist.subList(i - 4, i + 1)));
                            dayList.add(ilist.get(i));
                        }
                    }
                }
            }
            okStock.add(dayList);
        }
        //www.wewestar.com/thread-293597...https://www.baidu.com/link?url=MXOkrLoCWVuP2kzp4pQNF7mCqE36RPTtA27FD4xYjWHWBSfd5sTkZWGR4OxvtBNxO6nKSsYSHzBMoKSom429a_&wd=&eqid=f61c30f70000357d00000003602f6a39
        for (int i = 0; i < okStock.size(); i++) { //每天的符合条件的股票
            String os = "";
//            System.out.println("day "+i);
            List<SIndexEntity> dayList = okStock.get(i);
            for (int j = 0; j < dayList.size(); j++) {
                SIndexEntity dlast = dayList.get(j);
//                System.out.println("code "+dlast.getScode());
                os += dlast.getScode() + "," + dlast.getSname() + "," + dlast.getStime() + ",";
                os += dlast.getDif() + "," + dlast.getDea() + "," + dlast.getMacd() + ",";
                os += dlast.getMms() + "," + dlast.getMmm() + "," + dlast.getMml() + ",";
                os += dlast.getBoll() + "," + dlast.getEma12() + "," + dlast.getEma50();
                os += "," + dlast.getWeight();
                outText.add(os);
            }
        }*/
        //----------一定时间的选股方法结束-----------
        //模拟买卖,包括选股票
        simulateMM(tnum, sindexList, outText, tsell);
        //模拟买卖结束
        //开始选股，第一步只选最后一天符合要求
        /*
        List<List<SIndexEntity>> okStocks = new ArrayList<>();
        for (int i = 0; i < sindexList.size(); i++) { //每个股票
            List<SIndexEntity> ilist = sindexList.get(i);
            int tday = ilist.size() - 1;
            //如果12日ema<50日ema 去掉
            if (ilist.get(tday).getEma12() >= ilist.get(tday).getEma50()) {
                if (ilist.get(tday - 2).getShigh() <= ilist.get(tday).getBoll()*1.05) {
                    if (ilist.get(tday).getSopen() > ilist.get(tday).getBoll() && ilist.get(tday).getSclose() < ilist.get(tday).getBoll()) {
                        okStocks.add(ilist);
                    }
                }
            }
        }
        //www.wewestar.com/thread-293597...https://www.baidu.com/link?url=MXOkrLoCWVuP2kzp4pQNF7mCqE36RPTtA27FD4xYjWHWBSfd5sTkZWGR4OxvtBNxO6nKSsYSHzBMoKSom429a_&wd=&eqid=f61c30f70000357d00000003602f6a39
        for (int i = 0; i < okStocks.size(); i++) {
            String os = "";
            SIndexEntity dlast = okStocks.get(i).get(okStocks.get(i).size() - 1);
            System.out.println("code "+dlast.getScode());
            os += dlast.getScode() + "," + dlast.getSname() + "," + dlast.getStime() + ",";
            os += dlast.getDif() + "," + dlast.getDea() + "," + dlast.getMacd() + ",";
            os += dlast.getMms() + "," + dlast.getMmm() + "," + dlast.getMml() + ",";
            os += dlast.getBoll() + "," + dlast.getEma12() + "," + dlast.getEma50();
            //计算权重
            double weight = 0d;
            weight = calWeight(okStocks.get(i));
            os+=","+weight;
            outText.add(os);
        }
         */
        //----------最后一天的选股方法结束-----------
        System.out.println("开始输出");
        PrintWriter pw = new PrintWriter(fw);
//        pw.println("代码,名称,日期,DIF,DEA,MACD,MMS,MMM,MML,BOLL,EMA12,EMA50,权重");
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

    //计算一个股票当日的权重
    public static Double calWeight(List<SIndexEntity> slist) {
        SIndexEntity dlast = slist.get(slist.size() - 1);
        SIndexEntity dsecond = slist.get(slist.size() - 2);
        SIndexEntity dfive = slist.get(0);
        double weight = 0d;
        //最重要的还是看大趋势，2天的小趋势不可靠
        //第一种方案，只去掉下行
        double macd1 = dlast.getMacd();
        double macd2 = dsecond.getMacd();
        if (macd1 < 0 && macd2 > 0)
            weight -= 1d;
        double ms1 = dlast.getMms() - dsecond.getMms();
        double ms2 = dsecond.getMms() - dfive.getMms();
        if (ms1 < 0 && ms2 < 0 && dlast.getMms() < dlast.getMml())
            weight -= 1d;
        double ms_mm1 = dlast.getMms() - dlast.getMmm();
        double ms_mm2 = dsecond.getMms() - dsecond.getMmm();
        if (ms_mm1 < ms_mm2)
            weight -= 1d;
        double emac1 = dlast.getEma12() - dlast.getEma50();
        double emac5 = dfive.getEma12() - dfive.getEma50();
        if (emac1 < emac5)
            weight -= 1d;
        /*
        double emac1 = dlast.getEma12() - dlast.getEma50();
        double emac2 = dsecond.getEma12() - dsecond.getEma50();
        //5天的ema趋势
        double emac5 = dfive.getEma12() - dfive.getEma50();
        if (emac1 > emac2 && emac2 > emac5)
            weight += 0.3;
        else if (emac1 > emac2 && emac2 <= emac5)
            weight += 0.15;
        else if (emac1 > emac5)
            weight += 0.1;
        else if (emac5 > emac2 && emac2 > emac1)
            weight -= 0.2;
        else if (emac2 > emac1)
            weight -= 0.05;
//            zlmm曲线走向
        double ms1 = dlast.getMms() - dsecond.getMms();
        double ms2 = dsecond.getMms() - dfive.getMms();
        if (ms1 > 0 && ms2 > 0) {
            if (ms1 > ms2)
                weight += 0.2;
            else
                weight += 0.1;
        } else if (ms1 > 0 && ms2 < 0) {
            weight += 0.1;
        } else if (ms1 < 0 && ms2 < 0)
            weight -= 0.1;
        double mm1 = dlast.getMmm() - dsecond.getMmm();
        double mm2 = dsecond.getMmm() - dfive.getMmm();
        if (mm1 > 0 && mm2 > 0) {
            if (mm1 > mm2)
                weight += 0.15;
            else
                weight += 0.05;
        } else if (mm1 > 0 && mm2 < 0) {
            weight += 0.05;
        } else if (mm1 < 0 && mm2 < 0)
            weight -= 0.05;
//        double ml1 = dlast.getMml()-dsecond.getMml();
//        double ml2 = dsecond.getMml()-dfive.getMml();
        //mms-mml
        double ms_ml1 = dlast.getMms() - dlast.getMml();
        double ms_ml2 = dsecond.getMms() - dsecond.getMml();
        double ms_ml5 = dfive.getMms() - dfive.getMml();
        if (ms_ml1 > 0 && ms_ml2 > 0 && ms_ml5 > 0) {
            if (ms_ml1 >= ms_ml2 && ms_ml2 >= ms_ml5) {
                weight += 0.4;
            } else if (ms_ml1 < ms_ml2 && ms_ml2 < ms_ml5) {
                weight += 0.05;
            } else if (ms_ml1 < ms_ml2)
                weight += 0.05;
            else
                weight += 0.2;
        } else if (ms_ml1 > 0 && ms_ml2 < 0 && ms_ml5 > 0) {
            if (ms_ml1 > ms_ml5)
                weight += 0.2;
            else if (ms_ml1 < ms_ml5)
                weight += 0.05;
        } else if (ms_ml1 > 0 && ms_ml2 < 0 && ms_ml5 < 0)
            weight += 0.15;
        else if (ms_ml1 > ms_ml2)
            weight += 0.1;
        else
            weight -= 0.1;
        //mmm-mml
        double mm_ml1 = dlast.getMmm() - dlast.getMml();
        double mm_ml2 = dsecond.getMmm() - dsecond.getMml();
        double mm_ml5 = dfive.getMmm() - dfive.getMml();
        if (mm_ml1 > 0 && mm_ml2 > 0 && mm_ml5 > 0) {
            if (mm_ml1 >= mm_ml2 && mm_ml2 >= mm_ml5) {
                weight += 0.4;
            } else if (mm_ml1 < mm_ml2 && mm_ml2 < mm_ml5) {
                weight += 0.05;
            } else if (mm_ml1 < mm_ml2)
                weight += 0.05;
            else
                weight += 0.2;
        } else if (mm_ml1 > 0 && mm_ml2 < 0 && mm_ml5 > 0) {
            if (mm_ml1 > mm_ml5)
                weight += 0.2;
            else if (mm_ml1 < mm_ml5)
                weight += 0.05;
        } else if (mm_ml1 > 0 && mm_ml2 < 0 && mm_ml5 < 0)
            weight += 0.15;
        else
            weight -= 0.1;
        //mms-mmm 
        double ms_mm1 = dlast.getMms() - dlast.getMmm();
        double ms_mm2 = dsecond.getMms() - dsecond.getMmm();
        double ms_mm5 = dfive.getMms() - dfive.getMmm();
        if (ms_mm1 > 0 && ms_mm2 > 0 && ms_mm5 > 0) {
            if (ms_mm1 >= ms_mm2 && ms_mm2 >= ms_mm5) {
                weight += 0.4;
            } else if (ms_mm1 < ms_mm2 && ms_mm2 < ms_mm5) {
                weight += 0.05;
            } else if (ms_mm1 < ms_mm2)
                weight += 0.05;
            else
                weight += 0.2;
        } else if (ms_mm1 > 0 && ms_mm2 < 0 && ms_mm5 > 0) {
            if (ms_mm1 > ms_mm5)
                weight += 0.2;
            else if (ms_mm1 < ms_mm5)
                weight += 0.05;
        } else if (ms_mm1 > 0 && ms_mm2 < 0 && ms_mm5 < 0)
            weight += 0.15;
        else
            weight -= 0.1;
        //当日的mms mmm mml的差
        if (ms_ml1 > 0 && ms_mm1 > 0 && mm_ml1 > 0)
            weight += 0.3;
        else if (ms_mm1 > 0)
            weight += 0.1;
        else if (ms_ml1 < 0 && mm_ml1 < 0 && ms_mm1 < 0)
            weight -= 0.2;
        else if (ms_ml1 < 0)
            weight -= 0.05;
        //macd
        double dif1 = dlast.getDif();
        double dea1 = dlast.getDea();
        double macd1 = dlast.getMacd();
        double dif2 = dsecond.getDif();
        double dea2 = dsecond.getDea();
        double macd2 = dsecond.getMacd();
        if (macd1 > 0 && macd1 >= macd2) {
            if (macd2 > 0)
                weight += 0.3;
            else
                weight += 0.15;
        } else if (macd2 > macd1 && macd1 >= 0)
            weight -= 0.05;
        else if (macd2 >= 0 && macd1 < 0)
            weight -= 2d;
        else if (macd2 > macd1 && macd2 < 0)
            weight -= 0.1;
        else if (macd1 < 0 && macd1 >= macd2)
            weight += 0.1;

        if (dif1 > 0 && dif2 > 0 && dea1 > 0 && dea2 > 0)
            weight += 0.1;
        else if (dif1 > 0 && dea1 > 0)
            weight += 0.05;
        else if (dif1 < 0 && dif2 < 0 && dea1 < 0 && dea2 < 0)
            weight -= 0.1;
*/
        return getNumDouble(weight);
    }

    //计算主力买卖指标公式
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
                double ti2 = getSMA(maxCloseList, 16) / getSMA(absCloseList, 16) * 100;
                double ti3 = getSMA(maxCloseList, 12) / getSMA(absCloseList, 12) * 100;
                si2 = Double.isNaN(si2) ? 0d : Double.isInfinite(si2) ? 0d : si2;
                si3 = Double.isNaN(si3) ? 0d : Double.isInfinite(si3) ? 0d : si3;
                ti2 = Double.isNaN(ti2) ? 0d : Double.isInfinite(ti2) ? 0d : ti2;
                ti3 = Double.isNaN(ti3) ? 0d : Double.isInfinite(ti3) ? 0d : ti3;
                rsi2.add(3 * si2 - 2 * ti2);
                rsi3.add(3 * si3 - 2 * ti3);
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

    public static void simulateMM(int tnum, List<List<SIndexEntity>> sindexList, List<String> tradeInfo, int tsell) {
        //计算所有股票在每天是否符合条件，符合的isok设为true，并设置权重
//        List<String> tradeInfo = new ArrayList<>();// 每一笔交易的记录
        for (int j = 0; j < sindexList.size(); j++) {
            List<SIndexEntity> ilist = sindexList.get(j);
            for (int i = 4; i < tnum; i++) { //按天循环每个股票，从第5天开始
                if (ilist.get(i).getEma12() >= ilist.get(i).getEma50()) {
                    if (ilist.get(i - 2).getShigh() <= ilist.get(i).getBoll() * 1.05) {
                        if (ilist.get(i).getSopen() > ilist.get(i).getBoll() && ilist.get(i).getSclose() < ilist.get(i).getBoll()) {
                            ilist.get(i).setIsok(true);
                            ilist.get(i).setWeight(calWeight(ilist.subList(i - 4, i + 1)));
                        }
                    }
                }
            }
        }
        // //代码,名称,交易日期,资金,权重,状态,开盘价,买入价,手数,总价
        tradeInfo.add("代码,名称,交易日期,资金,权重,开盘价,状态,买卖价,手数,总价");
        //开始进行买卖，每天进行循环
        int status1 = 1; //1=当天买入，0=当天卖出,开始之前为当天买入
        double money1 = 100000d;
        int tsell1 = 0;
        int status2 = 1;
        double money2 = 100000d;
        int tsell2 = 0;
        for (int i = 4; i < tnum; i++) { //时间到最后一天-交易时间长度
            int scount = 0; //权重大于0的股票
            int smcount = 0; //权重小于0但大于-0.6的股票
            //计算每天满足条件的股票数量
            for (int j = 0; j < sindexList.size(); j++) {
                List<SIndexEntity> ilist = sindexList.get(j);
                if (ilist.get(i).getIsok()) {
                    if (ilist.get(i).getWeight() >= 0d)
                        scount++;
                    else if (ilist.get(i).getWeight() > -1d)
                        smcount++;
                }
            }

            if (status1 == 1) { //资金1当日可以买
                if (scount > 0 && i + tsell + 1 < tnum) {
                    money1 = dayBuy(money1, tradeInfo, sindexList, i, scount, 0d, 0);
                    status1 = 0;
                    tsell1 = i + tsell + 1; //买的是第二天的股票，所以买出日=i+1+tsell
                } else if (smcount > 0 && i + tsell + 1 < tnum) {
                    money1 = dayBuy(money1, tradeInfo, sindexList, i, smcount, -0.9d, 0);
                    status1 = 0;
                    tsell1 = i + tsell + 1;
                }

            }
            if (status1 == 0 && i == tsell1) { //资金1当天可以卖
                money1 = daySell(money1, tradeInfo, sindexList, i, 0, tsell);
                status1 = 1; //转为买入状态
            }
            if (status2 == 1 && tsell1 != i + tsell + 1) { //资金2当日可以买，且资金1没有买
                if (scount > 0 && i + tsell + 1 < tnum) {
                    money2 = dayBuy(money2, tradeInfo, sindexList, i, scount, 0d, 1);
                    status2 = 0;
                    tsell2 = i + tsell + 1;
                } else if (smcount > 0 && i + tsell + 1 < tnum) {
                    money2 = dayBuy(money2, tradeInfo, sindexList, i, smcount, -0.9d, 1);
                    status2 = 0;
                    tsell2 = i + tsell + 1;
                }

            }
            if (status2 == 0 && i == tsell2) {
                money2 = daySell(money2, tradeInfo, sindexList, i, 1, tsell);
                status2 = 1;
            }

        }

        //模拟结束，计算收入并输出
        tradeInfo.add("资金1," + money1 + ",盈利," + getNumDouble(money1 - 100000d) + ",百分比," + getNumDouble((money1 - 100000d) / 100000d));
        tradeInfo.add("资金2," + money2 + ",盈利," + getNumDouble(money2 - 100000d) + ",百分比," + getNumDouble((money2 - 100000d) / 100000d));
    }

    /**
     * @param money1     总金额
     * @param tradeInfo  交易记录
     * @param sindexList 股票信息
     * @param i          天数下标
     * @param scount     要买的股票数量
     * @param weight     股票的权重
     * @return
     */
    public static Double dayBuy(double money1, List<String> tradeInfo, List<List<SIndexEntity>> sindexList,
                                int i, int scount, double weight, int f) {
        double dout1 = 0d;
        double dmoney1 = money1 / scount;
        for (int j = 0; j < sindexList.size(); j++) {
            List<SIndexEntity> ilist = sindexList.get(j);
            if (ilist.get(i).getIsok() && ilist.get(i).getWeight() >= weight) {
                //每100股才能达成交易,买入价格高2分
                double in = 0.01 + ilist.get(i + 1).getSopen();
                int lot = new Double(dmoney1 / (in * 100)).intValue();
                ilist.get(i + 1).setLot(lot);
                dout1 += lot * in * 100d; //每天的总支出
                SIndexEntity sie = ilist.get(i + 1);
                //代码,名称,交易日期,资金,权重,状态,开盘价,买入价,手数,总价
                tradeInfo.add(sie.getScode() + "," + sie.getSname() + "," + sie.getStime() + "," +
                        (f == 0 ? "资金1," : "资金2,") + ilist.get(i).getWeight() + "," +
                        sie.getSopen() + ",买入," + in + "," + lot + "," + getNumDouble(in * lot * 100d));
            }
        }
        money1 = money1 - dout1;
        return money1;
    }

    public static Double daySell(double money1, List<String> tradeInfo,
                                 List<List<SIndexEntity>> sindexList, int i, int f, int tsell) {
        double din = 0d;
        for (int j = 0; j < sindexList.size(); j++) {
            List<SIndexEntity> ilist = sindexList.get(j);
            if (ilist.get(i - tsell).getLot() > 0) { //这个股票有买入，则卖出
                //如果第二天的开盘价小于买入的价格，等到收盘卖出，卖出价格低2分
                double out = 0d;
                if (ilist.get(i).getSopen() < ilist.get(i - tsell).getSopen())
                    out = ilist.get(i).getSclose() - 0.01;
                else
                    out = ilist.get(i).getSopen() - 0.01;
                din += ilist.get(i - tsell).getLot() * out * 100d;
                SIndexEntity sie = ilist.get(i);
                //代码,名称,交易日期,资金,权重,开盘价,状态卖出价,手数,总价
                tradeInfo.add(sie.getScode() + "," + sie.getSname() + "," + sie.getStime() + "," + (f == 0 ? "资金1" : "资金2") + ",-," +
                        sie.getSopen() + ",卖出," + +out + "," + ilist.get(i - tsell).getLot() + "," +
                        getNumDouble(out * ilist.get(i - tsell).getLot() * 100d));
                ilist.get(i - tsell).setLot(0);
            }
        }
        money1 = money1 + din;
        return money1;
    }
}
