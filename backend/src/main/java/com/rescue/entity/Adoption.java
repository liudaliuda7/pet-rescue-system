package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("adoption")
public class Adoption {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long animalId;
    private Long userId;
    private String status;
    private String reason;
    private String contact;
    private String address;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String animalName;
    @TableField(exist = false)
    private String animalImage;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private Long stationId;
    @TableField(exist = false)
    private String stationName;
    @TableField(exist = false)
    private Integer visitCount;
    @TableField(exist = false)
    private String latestHealthTime;
    @TableField(exist = false)
    private String latestHealthContent;
}
