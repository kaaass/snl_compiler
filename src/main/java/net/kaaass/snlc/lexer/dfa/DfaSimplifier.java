package net.kaaass.snlc.lexer.dfa;

import lombok.Data;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 分割法化简 DFA
 *
 * @author kaaass
 */
public class DfaSimplifier {

    /**
     * 执行简化操作
     */
    public static DfaGraph run(DfaGraph input) {
        // 计算转移矩阵
        var serializer = DfaSerializer.on(input, DfaSerializer.TRANS);
        var transMat = serializer.getTransMat();
        // 初始化分组
        var initGroup = calcInitGroup(input);
        // 等价类划分
        var groups = groupMatrix(transMat, initGroup);
        // 构造新图
        var newGraph = new DfaGraph();
        // 创建状态
        DfaState startState = null;
        for (var group : groups) {
            var state = new DfaState();
            // 计算匹配的 token
            state.setMatchedTokens(
                    group.stream()
                            .map(oid -> input.getStates().get(oid).getMatchedTokens())
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream)
                            .sorted()
                            .distinct()
                            .collect(Collectors.toList()));
            // 添加图
            newGraph.addState(state);
            // 开始状态识别
            if (group.contains(input.getStartState().getId())) {
                startState = state;
            }
        }
        newGraph.setStartState(startState);
        // 创建边
        var states = newGraph.getStates();
        // 边缓存
        var edgeCache = new HashMap<Integer, Set<Character>>();
        // 旧 ID 转换为新 ID
        int[] groupMap = new int[input.getStates().size() + 1];
        buildGroupMap(groupMap, groups);
        for (int gid = 0; gid < groups.size(); gid++) {
            var group = groups.get(gid);
            var from = states.get(gid);
            // 遍历所有边，统计转移边去重
            edgeCache.clear();
            group.stream()
                    .map(input.getStates()::get)
                    .flatMap(st -> st.getNextEdges().stream())
                    .forEach(edge -> {
                        var toId = groupMap[edge.getNextState().getId() + 1];
                        var edgeSet =
                                edgeCache.computeIfAbsent(toId, id -> new HashSet<>());
                        // 添加边 cache
                        edgeSet.add(edge.getMatchChar());
                    });
            // 创建边
            edgeCache.forEach((toId, characters) -> {
                var to = states.get(toId);
                characters.forEach(chr -> DfaEdge.edge(chr).link(from, to));
            });
        }
        return newGraph;
    }

    /**
     * 根据状态匹配 token 的不同分组
     */
    public static List<Set<Integer>> calcInitGroup(DfaGraph input) {
        // 剩余状态集（无 token 指定）
        var remain = new TreeSet<Integer>();
        IntStream.range(0, input.getStates().size()).forEach(remain::add);
        // 根据不同的匹配 token 分组，作为初值划分
        var groupResult = input.getStates().stream()
                .map(st -> {
                    var tokens = st.getMatchedTokens();
                    if (tokens == null) {
                        return null;
                    }
                    return new Pair(tokens.stream().mapToInt(value -> value).toArray(), st.getId());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity()));
        // 对不同集合
        var initGroup = groupResult.values().stream()
                .map(pairs -> pairs.stream()
                        .map(p -> {
                            // 删除剩余状态
                            remain.remove(p.getRight());
                            return p.getRight();
                        }).collect(Collectors.toSet()))
                .collect(Collectors.toList());
        initGroup.add(remain);
        return initGroup;
    }

    /**
     * 状态转移矩阵分组
     *
     * @param mat       状态转移矩阵
     * @param initGroup 标志的状态
     * @return 划分得到的状态等价类
     */
    public static List<Set<Integer>> groupMatrix(int[][] mat, List<Set<Integer>> initGroup) {
        var groups = new ArrayList<>(initGroup);
        var newGroups = new ArrayList<Set<Integer>>();
        int[] groupMap = new int[mat.length + 1];
        // 初始化分组映射
        buildGroupMap(groupMap, groups);
        // 划分
        while (true) {
            newGroups.clear();
            // 尝试划分
            for (var group : groups) {
                var result = splitMatrix(mat, groupMap, group);
                newGroups.addAll(result);
            }
            // 比较结果
            if (groups.equals(newGroups)) {
                break;
            } else {
                // 复制新组内容
                groups.clear();
                groups.addAll(newGroups);
                // 重新建立组映射
                buildGroupMap(groupMap, groups);
            }
        }
        return groups;
    }

    /**
     * 在下标集合内分割
     */
    public static List<Set<Integer>> splitMatrix(int[][] mat, int[] group, Set<Integer> indexes) {
        var groupMap = indexes.stream()
                .map(idx -> new Pair(mat[idx], idx))
                .map(states -> states.modifyLeft(
                        Arrays.stream(states.left)
                                .map(idx -> group[idx + 1])
                                .toArray()))
                .collect(Collectors.groupingBy(Function.identity()));
        return groupMap.values().stream()
                .map(list ->
                        list.stream()
                                .map(Pair::getRight)
                                .collect(Collectors.toSet()))
                .collect(Collectors.toList());
    }

    @Data
    public static class Pair {
        public final int[] left;
        public final int right;

        public Pair(int[] left, int right) {
            this.left = left;
            this.right = right;
        }

        public Pair modifyLeft(int[] newLeft) {
            return new Pair(newLeft, this.right);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return Arrays.equals(left, ((Pair) o).left);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(left);
        }
    }

    /**
     * 构建组映射，下标 + 1
     *
     * @param groupMap 已经完成分配的数据，长度 = n + 1
     * @param groups   分组
     */
    public static void buildGroupMap(int[] groupMap, List<Set<Integer>> groups) {
        groupMap[0] = -1;
        for (int gid = 0; gid < groups.size(); gid++) {
            int finalGid = gid;
            groups.get(gid).forEach(sid -> groupMap[sid + 1] = finalGid);
        }
    }
}
