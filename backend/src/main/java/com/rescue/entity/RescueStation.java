package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("rescue_station")
public class RescueStation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String contact;
    private String description;
    private String image;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;
}
