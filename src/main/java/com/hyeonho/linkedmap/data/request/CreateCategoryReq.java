package com.hyeonho.linkedmap.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryReq {
    private String email;
    private String categoryName;
}
