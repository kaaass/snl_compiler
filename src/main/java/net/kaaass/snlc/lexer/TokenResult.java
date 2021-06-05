package net.kaaass.snlc.lexer;

import lombok.Data;

/**
 * Token 匹配结果
 * @author kaaass
 */
@Data
public class TokenResult<T> {

    private final TokenInfo<T> definition;

    private String token;
}
