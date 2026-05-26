package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long animalId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String animalName;
    @TableField(exist = false)
    private String animalImage;
    @TableField(exist = false)
    private String animalStatus;
    @TableField(exist = false)
    private String typeName;
    @TableField(exist = false)
    private String stationName;
}
