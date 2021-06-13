package net.kaaass.snlc.ast.attr;

import lombok.*;
import net.kaaass.snlc.ast.Kind;

@Getter
@Setter
@NoArgsConstructor
public class ArrayAttr extends BaseAttr{
    private Integer low;
    private Integer top;
    private Kind childType;
}
