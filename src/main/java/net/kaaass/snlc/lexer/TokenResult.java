package net.kaaass.snlc.lexer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Token 匹配结果
 * @author kaaass
 */
@Getter
@RequiredArgsConstructor
public class TokenResult<T> {

    private final TokenInfo<T> definition;

    private String token;
}
