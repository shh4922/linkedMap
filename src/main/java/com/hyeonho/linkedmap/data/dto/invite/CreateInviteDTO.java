package com.hyeonho.linkedmap.data.dto.invite;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CreateInviteDTO {
    private String url;

    public CreateInviteDTO(String url) {
        this.url = url;
    }
}
