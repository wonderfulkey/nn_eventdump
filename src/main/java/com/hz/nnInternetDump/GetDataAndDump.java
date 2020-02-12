package com.hz.nnInternetDump;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hz.domain.HttpResult;
import com.hz.domain.XunchaDataInfo;
import com.hz.mapper.XunchaDataInfoMapper;
import com.hz.utils.AESUtil;
import com.hz.utils.APIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class GetDataAndDump {

    @Resource
    private XunchaDataInfoMapper xunchaDataInfoMapper;

    @Value("${http.url}")
    private String url;

    @Scheduled(fixedRate = 15000)
    public void getDataAndDump() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        APIService apiService = new APIService();
        try {
            System.out.println(df.format(System.currentTimeMillis()) + ": 发起请求");
          //  HttpResult httpResult = apiService.doGet(url);
            HttpResult httpResult = apiService.doPost(url,null);
            System.out.println(df.format(System.currentTimeMillis()) + ": 请求结束" + httpResult.getCode());
            if (null != httpResult && 200 == httpResult.getCode()) {
                String body = httpResult.getBody();
                JSONObject result = JSONObject.parseObject(body);
                if (200 == result.getInteger("code")) {
                    String data1 = AESUtil.decrypt(result.getString("data"));
                    JSONArray data = JSONArray.parseArray(data1);
                    if (null != data && data.size() > 0) {
                        System.out.println(df.format(System.currentTimeMillis()) + ": 已请求到数据");
                        //将JSON转成实体
                        List<XunchaDataInfo> datas = transfToEntity(data);
                        System.out.println(df.format(System.currentTimeMillis()) + ": 开始灌入数据");
                        dumpData(datas);
                        System.out.println(df.format(System.currentTimeMillis()) + ": 数据灌入成功,进行下一批次");
                    } else {
                        System.out.println(df.format(System.currentTimeMillis()) + ": 暂无数据");
                        return;
                    }
                } else {
                    System.out.println(df.format(System.currentTimeMillis()) + ": " + result.getString("data"));
                    return;
                }
            } else {
                System.out.println(df.format(System.currentTimeMillis()) + ": http请求异常");
                return;
            }

        } catch (Exception e) {
            System.out.println(df.format(System.currentTimeMillis()) + ": 程序异常");
            if (e.getMessage().contains("Connection refused")) {
                System.out.println(df.format(System.currentTimeMillis()) + ": 远程接口断开,无法请求");
                return;
            }
            e.printStackTrace();
            return;
        }

    }

    private List<XunchaDataInfo> transfToEntity(JSONArray data) {
        ArrayList<XunchaDataInfo> list = new ArrayList<XunchaDataInfo>();
        for (Object datum : data) {
            JSONObject jo = JSONObject.parseObject(String.valueOf(datum));
            XunchaDataInfo dataInfo = new XunchaDataInfo();
            dataInfo.setDataId(jo.getLongValue("data_id"));
            dataInfo.setViewNum(jo.getInteger("view_num"));
            dataInfo.setCmtNum(jo.getInteger("cmt_num"));
            dataInfo.setRttNum(jo.getInteger("rtt_num"));
            dataInfo.setEid(jo.getInteger("eid"));
            dataInfo.setInfoType(jo.getInteger("info_type"));
            dataInfo.setTitle(jo.getString("title"));
            dataInfo.setContent(jo.getString("content"));
            dataInfo.setUrl(jo.getString("url"));
            dataInfo.setSiteName(jo.getString("site_name"));
            dataInfo.setEmotionLevel(jo.getInteger("emotion_level"));
            dataInfo.setPubtime(jo.getString("pubtime"));
            dataInfo.setSummary(jo.getString("summary"));
            dataInfo.setKeywords(jo.getString("keywords"));
            dataInfo.setIsDup(jo.getInteger("is_dup"));
            dataInfo.setDupId(jo.getInteger("dup_id"));
            dataInfo.setAuthor(jo.getString("author"));
            dataInfo.setUrlMD5(jo.getString("urlMD5"));
            list.add(dataInfo);
        }
        return list;
    }

    @Transactional
    void dumpData(List<XunchaDataInfo> datas) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (XunchaDataInfo data : datas) {
            try {
                xunchaDataInfoMapper.addData(data);
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    //  System.out.println(df.format(System.currentTimeMillis()) + ": 数据重复,已去重");
                    continue;
                }
                e.printStackTrace();
                continue;
            }
        }
    }
}
