package com.chutneytesting.design.domain.environment;

import java.util.Collections;

public final class NoTarget {

    public static final Target NO_TARGET = Target.builder()
        .withId(Target.TargetId.of("", ""))
        .withUrl("")
        .withProperties(Collections.emptyMap())
        .build();

    private NoTarget() {}
}
