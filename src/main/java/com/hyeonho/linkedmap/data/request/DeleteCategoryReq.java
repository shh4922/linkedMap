package com.hyeonho.linkedmap.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteCategoryReq {
    private Long categoryId;
    private String email;
}
