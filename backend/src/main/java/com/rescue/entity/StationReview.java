package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("station_review")
public class StationReview {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long stationId;
    private Long userId;
    private Long adoptionId;
    private Integer rating;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String userName;
}
