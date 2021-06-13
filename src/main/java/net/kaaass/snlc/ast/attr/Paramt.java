package net.kaaass.snlc.ast.attr;

public enum Paramt {
    ValParamType {
        @Override
        public String toString() {
            return "value";
        }
    },
    VarParamType {
        @Override
        public String toString() {
            return "var";
        }
    }
}
