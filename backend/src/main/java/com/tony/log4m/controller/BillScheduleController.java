package com.tony.log4m.controller;

import com.tony.log4m.exception.Log4mException;
import com.tony.log4m.models.dto.SaveBillScheduleDto;
import com.tony.log4m.models.entity.BillSchedule;
import com.tony.log4m.models.entity.Category;
import com.tony.log4m.models.vo.ResultVO;
import com.tony.log4m.service.BillScheduleService;
import com.tony.log4m.service.CategoryService;
import com.tony.log4m.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/**
 * 定时记账管理Controller
 *
 * @author Tony
 * @since 10/29/2026
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillScheduleController {

    private final BillScheduleService billScheduleService;
    private final CategoryService categoryService;
    private final UserService userService;

    /**
     * 查询定时记账列表
     */
    @GetMapping
    public ResultVO<List<BillSchedule>> list() {
        return ResultVO.success(billScheduleService.lambdaQuery()
                .orderByAsc(BillSchedule::getExecTime)
                .list());
    }

    /**
     * 创建定时记账（chatId 取自已有 Telegram 用户，到点推送账单通知）
     */
    @PostMapping
    public ResultVO<BillSchedule> create(@Valid @RequestBody SaveBillScheduleDto dto) {
        Long chatId = userService.getPrimaryChatId();
        if (chatId == null) {
            throw new Log4mException("未找到 Telegram 用户，请先在机器人中发送任意消息");
        }
        Category category = categoryService.getById(dto.getCategoryId());
        BillSchedule schedule = BillSchedule.builder()
                .chatId(chatId)
                .note(dto.getNote())
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .amount(dto.getAmount())
                .execTime(LocalTime.parse(dto.getExecTime()))
                .weekDays(dto.getWeekDays())
                // fastjson2 FieldBased 反序列化不执行字段初始值，缺省时需显式默认为启用
                .enabled(dto.getEnabled() == null || dto.getEnabled())
                .build();
        schedule.insert();
        return ResultVO.success(schedule);
    }

    /**
     * 更新定时记账
     */
    @PutMapping("/{id}")
    public ResultVO<Boolean> update(@PathVariable Long id, @Valid @RequestBody SaveBillScheduleDto dto) {
        BillSchedule schedule = billScheduleService.getById(id);
        if (schedule == null) {
            return ResultVO.error("定时记账不存在");
        }
        Category category = categoryService.getById(dto.getCategoryId());
        schedule.setNote(dto.getNote())
                .setCategoryId(category.getCategoryId())
                .setCategoryName(category.getCategoryName())
                .setAmount(dto.getAmount())
                .setExecTime(LocalTime.parse(dto.getExecTime()))
                .setWeekDays(dto.getWeekDays())
                .setEnabled(dto.getEnabled());
        return ResultVO.success(schedule.updateById());
    }

    /**
     * 删除定时记账
     */
    @DeleteMapping("/{id}")
    public ResultVO<Boolean> delete(@PathVariable Long id) {
        return ResultVO.success(billScheduleService.removeById(id));
    }
}
