package com.h4p3.entity;

import java.util.List;

public record MemberCacheEntity(String userName, String description, long tokenCreateTime, List<String> permission) {
}
