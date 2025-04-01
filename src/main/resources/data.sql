-- 用户表 t_user
INSERT INTO `user` (id, username, tg_user_id, password, email, default_account_id, status)
VALUES (1, 'tony', 123456789, 'hashed_password', 'tony@example.com', 1, 1),
       (2, 'alice', 987654321, 'password123', 'alice@example.com', 2, 1);

-- 账户表 account
INSERT INTO account (id, user_id, account_name, balance, consume, income, consume_limit, is_default)
VALUES (1, 1, '工资账户', 50000.00, 1200.00, 20000.00, 5000.00, 1),
       (2, 2, '信用卡', -2000.00, 3000.00, 0.00, 10000.00, 1),
       (3, 1, '储蓄账户', 150000.00, 0.00, 150000.00, 0.00, 0);

-- 分类表 category
INSERT INTO category (id, user_id, name, sort)
VALUES (1, 1, '餐饮', 1),
       (2, 1, '交通', 2),
       (3, 2, '购物', 1),
       (4, 1, '娱乐', 3);

-- 标签表 tag
INSERT INTO tag (id, user_id, name, sort)
VALUES (1, 1, '外卖', 1),
       (2, 1, '加油', 2),
       (3, 2, '网购', 1),
       (4, 1, '电影', 3);

-- 账单表 bill
INSERT INTO bill (id, user_id, account_id, title, transaction_type, bill_date, bill_day, bill_month, amount,
                  category_id, tag_id)
VALUES (1, 1, 1, '美团外卖', 'EXPENSE', '2025-04-01 12:00:00', '2025-04-01', '2025-04', 58.00, 1, 1),
       (2, 1, 1, '地铁月票', 'EXPENSE', '2025-04-02 08:00:00', '2025-04-01', '2025-04', 200.00, 2, 2),
       (3, 2, 2, '京东购物', 'EXPENSE', '2025-04-03 15:30:00', '2025-04-03', '2025-04', 399.00, 3, 3),
       (4, 1, 3, '工资到账', 'INCOME', '2025-04-01 18:00:00', '2025-04-01', '2025-04', 20000.00, NULL, NULL);

-- 规则表 rule
INSERT INTO rule (id, name, user_id, account_id, transaction_type, category_id, keywords, amount)
VALUES (1, '餐饮预算', 1, 1, 'EXPENSE', 1, '外卖|餐厅', 2000.00),
       (2, '交通限制', 1, NULL, 'EXPENSE', 2, NULL, 500.00),
       (3, '网购提醒', 2, 2, 'EXPENSE', 3, '京东|天猫', 0.00);
