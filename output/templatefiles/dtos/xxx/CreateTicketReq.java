package ir.mojir.simple_ticketing_system.dtos.ticket;

import ir.mojir.spring_boot_commons.helpers.RegexHelper;
import jakarta.validation.constraints.Pattern;

public class CreateTicketReq {
    @Pattern(regexp = RegexHelper.persianFieldRegex, message = "pattern was not match")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
