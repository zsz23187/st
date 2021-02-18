package com.st.demo.util;

import com.st.demo.entity.SinfoEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            File f = new File("D:\\stest\\s"+rlist.get(0).get(rlist.get(0).size()-1).getTtime()+".txt");
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
            ots+=",";
            ots+=10000*(slist.get(slist.size() - 1).getSopen() -
                    slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen()+10000-10000/slist.get(slist.size() - 2).getSopen()*0.04;
            if (slist.get(slist.size() - 3).getSotur() > avob && md[2] > 0 && md[1] < 0 && md[0]>0)
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
            File f = new File("D:\\stest\\s"+rlist.get(0).get(rlist.get(0).size()-1).getTtime()+".txt");
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
            double sopen = 0d,sclose = 0d;
            for (int j = 0; j < slist.size() - 2; j++) {
                avob += slist.get(j).getSotur();
                sopen += slist.get(j).getSopen();
                sclose += slist.get(j).getSclose();
            }
            sopen = sopen/5;
            sclose = sclose/5;
            avob = avob / 5;
            double[] md = new double[3];
            for (int j = 0; j < 3; j++) {
                SinfoEntity s1 = slist.get(j);
                SinfoEntity s2 = slist.get(j + 1);
                SinfoEntity s3 = slist.get(j + 2);
                double avg = (s1.getSopen() + s2.getSopen() + s3.getSopen()) / 3;
                double a3 = s3.getSopen() - avg > 0 ? Math.log(Math.abs(s3.getSopen() - avg)/ s3.getSopen() * 100) : Math.log(0.01+Math.abs(s3.getSopen() - avg)/s3.getSopen() * 100);
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
            ots+=",";
            ots+=10000*(slist.get(slist.size() - 1).getSopen() -
                    slist.get(slist.size() - 2).getSopen()) / slist.get(slist.size() - 2).getSopen()+10000-10000/slist.get(slist.size() - 2).getSopen()*0.04;
            //当日涨且收盘大于收盘价的boll5日，开盘小于boll5日
            if(slist.get(slist.size() - 3).getSclose()>sclose && slist.get(slist.size() - 3).getSopen()<sclose)
                ots +=",1";
            else
                ots += ",0";
            //当日涨且收盘大于开盘价的boll5日，开盘小于boll5日
            if(slist.get(slist.size() - 3).getSclose()>sopen && slist.get(slist.size() - 3).getSopen()<sopen)
                ots +=",1";
            else
                ots += ",0";
            //当日跌且开盘大于收盘价的boll5日，收盘盘小于boll5日
            if(slist.get(slist.size() - 3).getSopen()>sclose && slist.get(slist.size() - 3).getSclose()<sclose)
                ots +=",1";
            else
                ots += ",0";
            //当日跌且开盘大于开盘价的boll5日，收盘小于boll5日
            if(slist.get(slist.size() - 3).getSopen()>sopen && slist.get(slist.size() - 3).getSclose()<sopen)
                ots +=",1";
            else
                ots += ",0";
            if (slist.get(slist.size() - 3).getSotur() > avob && md[2] > 0 && md[1] > 0 && md[0]>0)
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
            sout+="name:'" + slist.get(0).getSname()+i+"',";
            sout+="title:'"+slist.get(0).getScode() + "',";
            sout+="type:'line',stock:'总量',";
            sout+="data:[";
            double avob = 0d;
            for (int j = 0; j < slist.size() - 2; j++) {
                avob += slist.get(j).getSopen();
            }
            avob = avob / 5;

            for (int j = 0; j < slist.size(); j++) {
                sout+=slist.get(j).getSopen()/avob-1+",";
//                pw.print(slist.get(j).getStime() + ",");
//                pw.print(slist.get(j).getSopen() + ",");
//                pw.print(slist.get(j).getSclose() - slist.get(j).getSopen() >= 0 ? 1 + "," : 0 + ",");
            }
            sout=sout.substring(0,sout.length()-1) + "]},";
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


    public static double[] getBoll(List<SinfoEntity> slist){
        double[] boll = new double[2]; //boll开盘和收盘
        double sopen = 0d;
        double sclose = 0d;
        for (int i = 0; i < slist.size(); i++) {
            sopen += slist.get(i).getSopen();
            sclose += slist.get(i).getSclose();
        }
        boll[0] = sopen/slist.size();
        boll[1] = sclose/ slist.size();
        return boll;
    }
}
