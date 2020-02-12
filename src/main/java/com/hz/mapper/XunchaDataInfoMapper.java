package com.hz.mapper;

import com.hz.domain.XunchaDataInfo;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.jdbc.SQL;

@Mapper
public interface XunchaDataInfoMapper {
    @InsertProvider(type = SqlProvider.class, method = "addData")
    @Options(useGeneratedKeys = true)
    void addData(XunchaDataInfo dataInfo);


    class SqlProvider {
        public String addData() {
            SQL sql = new SQL();
            sql.INSERT_INTO("data_info_xuncha");
            sql.VALUES("eid", "#{eid}");
            sql.VALUES("info_type", "#{infoType}");
            sql.VALUES("title", "#{title}");
            sql.VALUES("summary", "#{summary}");
            sql.VALUES("content", "#{content}");
            sql.VALUES("url", "#{url}");
            sql.VALUES("site", "#{siteName}");
            sql.VALUES("pubtime", "#{pubtime}");
            sql.VALUES("author", "#{author}");
           // sql.VALUES("data_id", "#{dataId}");
            sql.VALUES("reviewcount", "#{viewNum}"); //点击量
            sql.VALUES("replycount", "#{cmtNum}"); //回复
            sql.VALUES("rttcount", "#{rttNum}"); //分享转发
          //  sql.VALUES("up_count", "#{likeNum}");
            sql.VALUES("picture", "#{picture}");
            sql.VALUES("keywords", "#{keywords}");
          //  sql.VALUES("emotion_level", "#{emotionLevel}");
            //sql.VALUES("nation_category", "#{nationCategory}");
            sql.VALUES("is_dup", "#{isDup}");
            sql.VALUES("dup_id", "#{dupId}");
            sql.VALUES("url_md5", "#{urlMD5}");
            return sql.toString();
        }
    }
}
