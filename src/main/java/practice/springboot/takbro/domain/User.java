package practice.springboot.takbro.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public final class User {

    private final String alias;

    protected User() {
        alias = null;
    }

}
