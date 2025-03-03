package com.hyeonho.linkedmap.data.dto;
import com.hyeonho.linkedmap.enumlist.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoDTO {
    private String email;
    private String username;
    private Role role;
}
