package com.tony.log4m.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量删除DTO
 * 
 * @author Tony
 */
@Data
public class BatchDeleteDto {
    
    private List<Long> ids;
}