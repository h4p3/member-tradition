package com.h4p3.context;

import java.util.List;

public record RequestContext(String userName, List<String> permissions) {
}
