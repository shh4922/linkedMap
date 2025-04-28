package com.hyeonho.linkedmap.data.dto.member;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberUpdateDto {

    private String email;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}
