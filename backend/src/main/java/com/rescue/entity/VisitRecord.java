package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("visit_record")
public class VisitRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adoptionId;
    private String content;
    private String status;
    private String image;
    private String visitor;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String animalName;
    @TableField(exist = false)
    private String adopterName;
}
