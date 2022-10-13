package com.tony.log4m;

import cn.hutool.json.JSONUtil;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.Task;
import com.meilisearch.sdk.model.SearchResult;
import com.tony.log4m.pojo.entity.Category;
import com.tony.log4m.pojo.entity.Record;
import com.tony.log4m.service.impl.CategoryServiceImpl;
import com.tony.log4m.service.impl.RecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author TonyLeung
 * @date 2022/10/13
 */
//@SpringBootTest
public class MeiliTest {

    private final static Client client = new Client(new Config("http://127.0.0.1:7700", ""));


    @Autowired
    private RecordServiceImpl recordService;
    @Autowired
    private CategoryServiceImpl categoryService;


    public static void main(String[] args) throws Exception {
        client.deleteIndex("records");
        Path fileName = Path.of("/tony/docs/records.json");
        String moviesJson = Files.readString(fileName);
        Index index = client.index("records");
        Task task = index.addDocuments(moviesJson);
        System.out.println("task: " + JSONUtil.toJsonStr(task));
    }


    @Test
    void search() throws Exception {
        Client client = new Client(new Config("http://127.0.0.1:7700", ""));
        Index index = client.index("movies");
        String documents = index.getDocuments();
        System.out.println("documents: " + documents);
        SearchResult result = index.search("iron");
        System.out.println("search: " + JSONUtil.toJsonStr(result));
    }

    @Test
    void addCategory() throws Exception {
        List<Category> list = categoryService.list();
        String recordJsonStr = JSONUtil.toJsonStr(list);
        System.out.println("recordJsonStr: " + JSONUtil.toJsonStr(recordJsonStr));
        Index index = client.index("category");
        Task task = index.addDocuments(recordJsonStr, "id");
        System.out.println("task: " + JSONUtil.toJsonStr(task));

        String documents = index.getDocuments();
        System.out.println("documents: " + JSONUtil.toJsonStr(documents));
    }

    @Test
    void addRecords() throws Exception {
        List<Record> list = recordService.list();
        String recordJsonStr = JSONUtil.toJsonStr(list);
        System.out.println("recordJsonStr: " + JSONUtil.toJsonStr(recordJsonStr));
        Index index = client.index("records");
        Task task = index.addDocuments(recordJsonStr, "id");
        System.out.println("task: " + JSONUtil.toJsonStr(task));

        String documents = index.getDocuments();
        System.out.println("documents: " + JSONUtil.toJsonStr(documents));
    }
}
