package com.example.demo.dtos.xxx;

import ir.mojir.spring_boot_commons.helpers.RegexHelper;
import jakarta.validation.constraints.Pattern;
import ir.mojir.spring_boot_commons.annotations.PersianNormalized;

public class CreateXxxReq {
    @PersianNormalized
    @Pattern(regexp = RegexHelper.persianFieldRegex, message = RegexHelper.persianFieldRegexMessageFa)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
