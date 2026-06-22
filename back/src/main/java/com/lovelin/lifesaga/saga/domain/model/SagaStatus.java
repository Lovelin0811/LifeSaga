package com.lovelin.lifesaga.saga.domain.model;

/**
 * 副本状态的稳定领域标识；中文名称等展示文案由前端负责转换。
 */
public enum SagaStatus {
    /** 副本仍在持续记录中。 */
    ACTIVE,
    /** 副本当前已完成，添加新节点后可重新进入进行中状态。 */
    COMPLETED
}
