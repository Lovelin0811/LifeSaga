package com.lovelin.lifesaga.saga.domain.model;

/**
 * 副本稀有度的稳定领域标识；中文名称等展示文案由前端负责转换。
 */
public enum SagaRarity {
    /** 普通，节点数量为 0 至 2。 */
    COMMON,
    /** 优秀，节点数量为 3 至 5。 */
    UNCOMMON,
    /** 稀有，节点数量为 6 至 10。 */
    RARE,
    /** 史诗，节点数量为 11 至 20。 */
    EPIC,
    /** 传说，节点数量为 21 至 29。 */
    LEGENDARY,
    /** 神话，节点数量不少于 30。 */
    MYTHIC;

    public static SagaRarity fromNodeCount(int nodeCount) {
        if (nodeCount < 0) {
            throw new IllegalArgumentException("节点数量不能为负数");
        }
        if (nodeCount >= 30) {
            return MYTHIC;
        }
        if (nodeCount >= 21) {
            return LEGENDARY;
        }
        if (nodeCount >= 11) {
            return EPIC;
        }
        if (nodeCount >= 6) {
            return RARE;
        }
        if (nodeCount >= 3) {
            return UNCOMMON;
        }
        return COMMON;
    }
}
