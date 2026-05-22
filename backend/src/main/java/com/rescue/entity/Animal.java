package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("animal")
public class Animal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long typeId;
    private String gender;
    private String age;
    private String color;
    private String description;
    private String image;
    private String status;
    private Long stationId;
    private String healthStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String typeName;
    @TableField(exist = false)
    private String stationName;
}
