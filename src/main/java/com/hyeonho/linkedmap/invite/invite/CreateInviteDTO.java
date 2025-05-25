package com.hyeonho.linkedmap.invite.invite;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CreateInviteDTO {
    private String url;

    public CreateInviteDTO(String url) {
        this.url = url;
    }
}
