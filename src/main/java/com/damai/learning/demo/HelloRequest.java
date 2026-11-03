package com.damai.learning.demo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HelloRequest {

    @NotBlank(message = "name不能为空")
    @Size(max = 20, message = "name长度不能超过20个字符")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
