package com.rescue.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("animal_claim")
public class AnimalClaim {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long animalId;
    private Long userId;
    private String proofDesc;
    private String proofImages;
    private String status;
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
    private String userPhone;
}
