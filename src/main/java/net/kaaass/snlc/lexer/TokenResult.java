package net.kaaass.snlc.lexer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Token 匹配结果
 * @author kaaass
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TokenResult<T> {

    private final TokenInfo<T> definition;

    private String token;
}
