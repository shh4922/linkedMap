package com.hyeonho.linkedmap.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteMarkerReq {
    private String email;
    private Long markerId;
}
