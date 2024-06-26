CREATE TABLE account
(
    id            int AUTO_INCREMENT
        PRIMARY KEY,
    user_id       int                                      NOT NULL,
    account_name  varchar(255)                             NOT NULL COMMENT '账户名称',
    balance       decimal(10, 2) DEFAULT 0.00              NOT NULL COMMENT '账户余额',
    consume       decimal(10, 2) DEFAULT 0.00              NOT NULL COMMENT '消费总额',
    income        decimal(10, 2) DEFAULT 0.00              NOT NULL COMMENT '收入总额',
    consume_limit decimal(10, 2) DEFAULT 0.00              NOT NULL COMMENT '消费限制',
    sort          int            DEFAULT 99                NOT NULL COMMENT '排序',
    status        int            DEFAULT 1                 NOT NULL COMMENT '0: inactive, 1: active',
    is_default    tinyint(1)     DEFAULT 0                 NULL COMMENT '是否默认账户',
    deleted       tinyint(1)     DEFAULT 0                 NOT NULL,
    create_time   timestamp      DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time   timestamp      DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE bill
(
    id               int AUTO_INCREMENT
        PRIMARY KEY,
    user_id          int          DEFAULT 0                 NULL,
    account_id       int          DEFAULT 0                 NULL,
    title            varchar(128) DEFAULT ''                NULL,
    transaction_type varchar(32)                            NOT NULL COMMENT '交易类型 EXPENSE: 支出, INCOME: 收入',
    bill_date        datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    day              date AS (CAST(`bill_date` AS date)) STORED,
    month            char(7) AS (DATE_FORMAT(`bill_date`, _utf8mb4'%Y-%m')) STORED,
    amount           decimal(10, 2)                         NOT NULL COMMENT '金额',
    category_id      int          DEFAULT 0                 NULL COMMENT '分类',
    tag_id           int          DEFAULT 0                 NULL COMMENT '标签',
    remark           varchar(255) DEFAULT ''                NULL COMMENT '备注',
    deleted          tinyint(1)   DEFAULT 0                 NOT NULL,
    create_time      timestamp    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time      timestamp    DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE category
(
    id          int AUTO_INCREMENT
        PRIMARY KEY,
    user_id     int        DEFAULT 0                 NULL,
    name        varchar(255)                         NOT NULL,
    sort        int        DEFAULT 99                NOT NULL COMMENT '排序',
    deleted     tinyint(1) DEFAULT 0                 NOT NULL,
    create_time timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE rule
(
    id               int AUTO_INCREMENT
        PRIMARY KEY,
    name             varchar(255)                             NOT NULL COMMENT '规则名称',
    user_id          int            DEFAULT 0                 NULL,
    account_id       int            DEFAULT 0                 NULL,
    transaction_type varchar(32)                              NOT NULL COMMENT '交易类型 EXPENSE: 支出, INCOME: 收入',
    category_id      int            DEFAULT 0                 NULL COMMENT '分类',
    tag_id           int            DEFAULT 0                 NULL COMMENT '标签',
    amount           decimal(10, 2) DEFAULT 0.00              NOT NULL COMMENT '默认金额',
    keywords         varchar(255)   DEFAULT ''                NULL COMMENT '关键字',
    sort             int            DEFAULT 99                NOT NULL COMMENT '排序',
    create_time      timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    update_time      timestamp      DEFAULT CURRENT_TIMESTAMP NULL,
    deleted          tinyint(1)     DEFAULT 0                 NULL
);

CREATE TABLE t_user
(
    id                 int AUTO_INCREMENT
        PRIMARY KEY,
    username           varchar(255) DEFAULT ''                NULL,
    tg_user_id         int          DEFAULT 0                 NULL,
    password           varchar(255) DEFAULT ''                NULL,
    email              varchar(255) DEFAULT ''                NULL,
    default_account_id int          DEFAULT 0                 NULL COMMENT '默认账户',
    status             int          DEFAULT 1                 NOT NULL COMMENT '0: inactive, 1: active',
    deleted            tinyint(1)   DEFAULT 0                 NOT NULL,
    create_time        datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time        datetime     DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE tag
(
    id          int AUTO_INCREMENT
        PRIMARY KEY,
    user_id     int        DEFAULT 0                 NULL,
    name        varchar(255)                         NOT NULL,
    sort        int        DEFAULT 99                NOT NULL COMMENT '排序',
    deleted     tinyint(1) DEFAULT 0                 NOT NULL,
    create_time timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL
);

