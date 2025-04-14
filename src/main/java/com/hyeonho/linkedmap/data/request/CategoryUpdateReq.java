package com.hyeonho.linkedmap.data.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateReq {
    Long categoryId;
    String categoryName;
}
