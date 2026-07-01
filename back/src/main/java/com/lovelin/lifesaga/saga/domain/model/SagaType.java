package com.lovelin.lifesaga.saga.domain.model;

/**
 * 副本类型的稳定领域标识；中文名称等展示文案由前端负责转换。
 */
public enum SagaType {
    /** 日常生活经历。 */
    LIFE,
    /** 旅行相关经历。 */
    TRAVEL,
    /** 学习与知识成长经历。 */
    STUDY,
    /** 工作与职业发展经历。 */
    WORK,
    /** 健身与健康管理经历。 */
    HEALTH,
    /** 亲密关系相关经历，兼容旧版 relationship 数据。 */
    RELATIONSHIP,
    /** 创作与作品产出经历。 */
    CREATIVE
}
