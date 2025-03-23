package com.hyeonho.linkedmap.data.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryJoin {
    private UUID inviteKey;
    private Long categoryId;
}
