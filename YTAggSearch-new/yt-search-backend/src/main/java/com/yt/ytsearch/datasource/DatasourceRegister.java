package com.yt.ytsearch.datasource;

import com.yt.ytsearch.model.enums.SearchTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DatasourceRegister {

    @Resource
    private PictureDatasource pictureDatasource;


    @Resource
    private PostDatasource postDatasource;

    @Resource
    private UserDatasource userDatasource;

    private Map<String, Datasource> typeDataSourceMap;

    @PostConstruct
    public void doInit() {
        typeDataSourceMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDatasource);
            put(SearchTypeEnum.USER.getValue(), userDatasource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDatasource);
        }};
    }

    public Datasource getDatasourceByType(String type) {
        if (typeDataSourceMap == null) {
            return null;
        }
        return typeDataSourceMap.get(type);
    }

}
