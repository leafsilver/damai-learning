package com.damai.learning.program.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProgramCreateRequest {

    @NotBlank(message = "节目标题不能为空")
    @Size(max = 200, message = "节目标题不能超过200个字符")
    private String title;

    @Size(max = 100, message = "艺人名称不能超过100个字符")
    private String actor;

    @NotBlank(message = "演出地点不能为空")
    @Size(max = 100, message = "演出地点不能超过100个字符")
    private String place;

    @Size(max = 500, message = "节目图片地址不能超过500个字符")
    private String itemPicture;
}