package com.tony.log4m.external.tutu;


import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.read.listener.ReadListener;
import com.alibaba.fastjson2.JSON;
import com.tony.log4m.enums.TransactionType;
import com.tony.log4m.pojo.entity.*;
import com.tony.log4m.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tony
 * @since 4/6/2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TutuService {

    private final CategoryService categoryService;
    private final TagService tagService;
    private final LedgerService ledgerService;
    private final UserService userService;
    private final BillService billService;


    private final List<TutuData> dataList = new ArrayList<>();
    private final Map<String, List<String>> categoryMap = new HashMap<>();

    private User user;


    public void read(String fileUrl, Long tgUserId) throws IOException {
        user = userService.getByTgUserId(tgUserId);

        URL url = URI.create(fileUrl).toURL();
        try (InputStream inputStream = url.openConnection().getInputStream()) {
            // 使用 FastExcel 的配置指定编码为 UTF-8
            FastExcel.read(inputStream, TutuData.class, new TutuDataListener())
                    .charset(StandardCharsets.UTF_8)
                    .sheet()
                    .doRead();
        }
    }

    public class TutuDataListener implements ReadListener<TutuData> {
        @Override
        public void invoke(TutuData data, AnalysisContext context) {
            log.info("解析到一条数据:{}", JSON.toJSONString(data));

            String category = data.getCategory();
            String subCategory = data.getSubCategory();
            if (StrUtil.isNotBlank(subCategory)) {
                List<String> subCategoryList = categoryMap.getOrDefault(category, new ArrayList<>());
                if (!subCategoryList.contains(subCategory)) {
                    subCategoryList.add(subCategory);
                }
                categoryMap.put(category, subCategoryList);
            }
            dataList.add(data);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("所有数据解析完成！");

            Long userId = user.getId();

            // 保存分类
            List<String> categoryNames = dataList.stream()
                    .map(TutuData::getCategory)
                    .distinct()
                    .toList();

            List<Category> categoryList = categoryNames.stream()
                    .map(name -> new Category().setName(name).setUserId(userId))
                    .toList();

            categoryService.saveBatch(categoryList);

            // 保存子分类
            List<Category> subCategoryList = new ArrayList<>();
            categoryMap.forEach((k, v) -> {
                log.info("{} -> {}", k, v);
                categoryList.stream().filter(c -> c.getName().equals(k)).findFirst().ifPresent(category -> {
                    v.forEach(subCategory -> {
                        subCategoryList.add(new Category().setName(subCategory).setParentId(category.getId()).setUserId(userId));
                    });
                });
            });

            categoryService.saveBatch(subCategoryList);

            // 保存标签
            List<Tag> tagList = new ArrayList<>();
            dataList.stream()
                    .map(TutuData::getTags)
                    .filter(StrUtil::isNotBlank)
                    .map(tags -> StrUtil.split(tags, ","))
                    .flatMap(List::stream)
                    .distinct()
                    .forEach(tagName -> {
                        tagList.add(new Tag().setName(tagName).setUserId(userId));
                    });

            tagService.saveBatch(tagList);

            // 保存账本
            List<Ledger> ledgerList = new ArrayList<>();
            dataList.stream()
                    .map(TutuData::getLedger)
                    .filter(StrUtil::isNotBlank)
                    .distinct()
                    .forEach(ledgerName -> {
                        ledgerList.add(new Ledger().setName(ledgerName).setUserId(userId));
                    });

            ledgerService.saveBatch(ledgerList);

            // 保存账单
            List<Bill> billList = new ArrayList<>();
            dataList.forEach(data -> {
                // 账单日期
                String date = data.getDate().substring(0, 8);
                LocalDateTime billDate = LocalDateTimeUtil.parse(date, "yyyyMMdd");

                // 账单类型
                TransactionType transactionType = data.getType().equals("收入") ? TransactionType.INCOME : TransactionType.EXPENSE;

                Bill bill = Bill.builder()
                        .userId(userId)
                        .title(data.getNote())
                        .note(data.getNote())
                        .tagName(data.getTags())
                        .billDate(billDate)
                        .billDay(billDate.toLocalDate())
                        .billMonth(LocalDateTimeUtil.format(billDate, "yyyyMM"))
                        .transactionType(transactionType)
                        .amount(data.getAmount())
                        .build();

                // 账单分类
                String category = data.getCategory();
                categoryList.stream().filter(c -> c.getName().equals(category))
                        .findFirst().ifPresent(c ->
                                bill.setCategoryId(c.getId()).setCategoryName(c.getName())
                        );

                String subCategory = data.getSubCategory();
                if (StrUtil.isNotBlank(subCategory)) {
                    subCategoryList.stream().filter(c -> c.getName().equals(subCategory))
                            .findFirst().ifPresent(c ->
                                    bill.setSubCategoryId(c.getId()).setSubCategoryName(c.getName())
                            );
                }

                // 账单标签
                String tags = data.getTags();
                if (StrUtil.isNotBlank(tags)) {
                    String[] tagNames = tags.split(",");
                    List<Long> tagIds = new ArrayList<>();
                    for (String tagName : tagNames) {
                        tagList.stream().filter(t -> t.getName().equals(tagName))
                                .findFirst().ifPresent(t ->
                                        tagIds.add(t.getId())
                                );
                    }
                    bill.setTagId(tagIds.get(0));
                }

                // 账本
                String ledger = data.getLedger();
                if (StrUtil.isNotBlank(ledger)) {
                    ledgerList.stream().filter(l -> l.getName().equals(ledger))
                            .findFirst().ifPresent(l ->
                                    bill.setLedgerId(l.getId()).setLedgerName(l.getName())
                            );
                }

                billList.add(bill);

            });

            billService.saveBatch(billList);

        }
    }


}
