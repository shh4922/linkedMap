package com.hyeonho.linkedmap.data.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String username;


}
