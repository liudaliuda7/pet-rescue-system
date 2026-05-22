package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("help_record")
public class HelpRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long requestId;
    private Long stationId;
    private String content;
    private String handler;
    private String result;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String requestTitle;
    @TableField(exist = false)
    private String stationName;
}
