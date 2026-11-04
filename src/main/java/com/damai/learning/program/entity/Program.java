package com.damai.learning.program.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("d_program")
public class Program {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String actor;

    private String place;

    private String itemPicture;

    private Integer programStatus;

    private LocalDateTime createTime;

    private LocalDateTime editTime;

    private Integer status;
}