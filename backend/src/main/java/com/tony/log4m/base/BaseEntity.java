package com.tony.log4m.base;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * @author TonyLeung
 * @since 2022/9/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BaseEntity<T> extends Model<BaseEntity<T>> {
    @Serial
    private static final long serialVersionUID = 1L;

    private LocalDateTime crTime;

    private LocalDateTime mdTime;

    @TableField(select = false)
    private Boolean deleted;
}
