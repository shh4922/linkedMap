package com.hyeonho.linkedmap.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateReq {
    String memberEmail;
    Long categoryId;
    String categoryName;
}
