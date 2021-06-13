package net.kaaass.snlc.ast;

/**
 * 标志节点类型
 * @author Kevin Axel
 */
public enum Kind {
    // 声明类型
    ArrayK, CharK, IntegerK, RecordK, IdK,

    // 语句类型
    IfK {
        @Override
        public String toString() {
            return "If";
        }
    }, WhileK {
        @Override
        public String toString() {
            return "While";
        }
    }, AssignK {
        @Override
        public String toString() {
            return "Assign";
        }
    }, ReadK {
        @Override
        public String toString() {
            return "Read";
        }
    }, WriteK {
        @Override
        public String toString() {
            return "Write";
        }
    }, CallK {
        @Override
        public String toString() {
            return "Call";
        }
    }, ReturnK {
        @Override
        public String toString() {
            return "Return";
        }
    },

    // 表达式类型
    OpK {
        @Override
        public String toString() {
            return "Op";
        }
    }, ConstK {
        @Override
        public String toString() {
            return "Const";
        }
    }, IdEK {
        @Override
        public String toString() {
            return "IdE";
        }
    }
}
