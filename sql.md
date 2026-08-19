    -- ============================================================
-- 企业级 RBAC 权限系统 - 核心建表 SQL（MySQL 8.0+）
-- 版本: 1.0
-- 说明: 包含用户、角色、菜单(权限)、部门、岗位及关联关系
-- ============================================================

-- 1. 部门表（组织架构）
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
`dept_id`       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '部门ID',
`parent_id`     BIGINT UNSIGNED     DEFAULT 0               COMMENT '父部门ID（顶级为0）',
`ancestors`     VARCHAR(500)        DEFAULT ''              COMMENT '祖级列表（如 0,100,101）',
`dept_name`     VARCHAR(100)        NOT NULL                COMMENT '部门名称',
`order_num`     INT                 DEFAULT 0               COMMENT '显示排序',
`leader`        VARCHAR(50)         DEFAULT NULL            COMMENT '负责人',
`phone`         VARCHAR(11)         DEFAULT NULL            COMMENT '联系电话',
`email`         VARCHAR(50)         DEFAULT NULL            COMMENT '邮箱',
`status`        CHAR(1)             DEFAULT '0'             COMMENT '状态（0正常 1停用）',
`del_flag`      CHAR(1)             DEFAULT '0'             COMMENT '删除标志（0存在 1删除）',
`create_by`     VARCHAR(64)         DEFAULT ''              COMMENT '创建者',
`create_time`   DATETIME            DEFAULT NULL            COMMENT '创建时间',
`update_by`     VARCHAR(64)         DEFAULT ''              COMMENT '更新者',
`update_time`   DATETIME            DEFAULT NULL            COMMENT '更新时间',
PRIMARY KEY (`dept_id`),
KEY `idx_parent_id` (`parent_id`),
KEY `idx_status` (`status`),
KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';


-- 2. 岗位表
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
`post_id`       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
`post_code`     VARCHAR(64)         NOT NULL                COMMENT '岗位编码',
`post_name`     VARCHAR(50)         NOT NULL                COMMENT '岗位名称',
`order_num`     INT                 DEFAULT 0               COMMENT '显示排序',
`status`        CHAR(1)             DEFAULT '0'             COMMENT '状态（0正常 1停用）',
`create_by`     VARCHAR(64)         DEFAULT ''              COMMENT '创建者',
`create_time`   DATETIME            DEFAULT NULL            COMMENT '创建时间',
`update_by`     VARCHAR(64)         DEFAULT ''              COMMENT '更新者',
`update_time`   DATETIME            DEFAULT NULL            COMMENT '更新时间',
`remark`        VARCHAR(500)        DEFAULT NULL            COMMENT '备注',
PRIMARY KEY (`post_id`),
UNIQUE KEY `uk_post_code` (`post_code`),
KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';


-- 3. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
`user_id`       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '用户ID',
`dept_id`       BIGINT UNSIGNED     DEFAULT NULL            COMMENT '部门ID',
`user_name`     VARCHAR(64)         NOT NULL                COMMENT '登录账号',
`nick_name`     VARCHAR(50)         NOT NULL                COMMENT '用户昵称',
`user_type`     VARCHAR(2)          DEFAULT '00'            COMMENT '用户类型（00系统用户 01注册用户）',
`email`         VARCHAR(100)        DEFAULT ''              COMMENT '用户邮箱',
`phone`         VARCHAR(11)         DEFAULT ''              COMMENT '手机号码',
`sex`           CHAR(1)             DEFAULT '0'             COMMENT '性别（0男 1女 2未知）',
`avatar`        VARCHAR(200)        DEFAULT ''              COMMENT '头像地址',
`password`      VARCHAR(100)        DEFAULT ''              COMMENT '密码（BCrypt加密）',
`status`        CHAR(1)             DEFAULT '0'             COMMENT '状态（0正常 1停用）',
`del_flag`      CHAR(1)             DEFAULT '0'             COMMENT '删除标志（0存在 1删除）',
`login_ip`      VARCHAR(128)        DEFAULT ''              COMMENT '最后登录IP',
`login_date`    DATETIME            DEFAULT NULL            COMMENT '最后登录时间',
`create_by`     VARCHAR(64)         DEFAULT ''              COMMENT '创建者',
`create_time`   DATETIME            DEFAULT NULL            COMMENT '创建时间',
`update_by`     VARCHAR(64)         DEFAULT ''              COMMENT '更新者',
`update_time`   DATETIME            DEFAULT NULL            COMMENT '更新时间',
`remark`        VARCHAR(500)        DEFAULT NULL            COMMENT '备注',
PRIMARY KEY (`user_id`),
UNIQUE KEY `uk_user_name` (`user_name`),
KEY `idx_dept_id` (`dept_id`),
KEY `idx_status` (`status`),
KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


-- 4. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
`role_id`       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '角色ID',
`role_name`     VARCHAR(50)         NOT NULL                COMMENT '角色名称',
`role_key`      VARCHAR(100)        NOT NULL                COMMENT '角色权限字符串（如 admin、common）',
`role_sort`     INT                 NOT NULL                COMMENT '显示顺序',
`data_scope`    CHAR(1)             DEFAULT '1'             COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
`status`        CHAR(1)             DEFAULT '0'             COMMENT '状态（0正常 1停用）',
`del_flag`      CHAR(1)             DEFAULT '0'             COMMENT '删除标志（0存在 1删除）',
`create_by`     VARCHAR(64)         DEFAULT ''              COMMENT '创建者',
`create_time`   DATETIME            DEFAULT NULL            COMMENT '创建时间',
`update_by`     VARCHAR(64)         DEFAULT ''              COMMENT '更新者',
`update_time`   DATETIME            DEFAULT NULL            COMMENT '更新时间',
`remark`        VARCHAR(500)        DEFAULT NULL            COMMENT '备注',
PRIMARY KEY (`role_id`),
UNIQUE KEY `uk_role_key` (`role_key`),
KEY `idx_status` (`status`),
KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';


-- 5. 菜单/权限表（核心：控制前端路由 + 后端接口权限）
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
`menu_id`       BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
`menu_name`     VARCHAR(50)         NOT NULL                COMMENT '菜单名称',
`parent_id`     BIGINT UNSIGNED     DEFAULT 0               COMMENT '父菜单ID（顶级为0）',
`order_num`     INT                 DEFAULT 0               COMMENT '显示顺序',
`path`          VARCHAR(200)        DEFAULT ''              COMMENT '路由地址',
`component`     VARCHAR(255)        DEFAULT NULL            COMMENT '组件路径（前端Vue组件）',
`query`         VARCHAR(255)        DEFAULT NULL            COMMENT '路由参数（如 id=1&type=2）',
`is_frame`      INT                 DEFAULT 1               COMMENT '是否为外链（0是 1否）',
`is_cache`      INT                 DEFAULT 0               COMMENT '是否缓存（0缓存 1不缓存）',
`menu_type`     CHAR(1)             DEFAULT ''              COMMENT '菜单类型（M目录 C菜单 F按钮）',
`visible`       CHAR(1)             DEFAULT '0'             COMMENT '显示状态（0显示 1隐藏）',
`status`        CHAR(1)             DEFAULT '0'             COMMENT '菜单状态（0正常 1停用）',
`perms`         VARCHAR(100)        DEFAULT NULL            COMMENT '权限标识（如 system:user:list）',
`icon`          VARCHAR(100)        DEFAULT '#'             COMMENT '菜单图标',
`create_by`     VARCHAR(64)         DEFAULT ''              COMMENT '创建者',
`create_time`   DATETIME            DEFAULT NULL            COMMENT '创建时间',
`update_by`     VARCHAR(64)         DEFAULT ''              COMMENT '更新者',
`update_time`   DATETIME            DEFAULT NULL            COMMENT '更新时间',
`remark`        VARCHAR(500)        DEFAULT ''              COMMENT '备注',
PRIMARY KEY (`menu_id`),
KEY `idx_parent_id` (`parent_id`),
KEY `idx_menu_type` (`menu_type`),
KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';


-- 6. 用户-角色关联表（多对多）
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
`user_id`   BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
`role_id`   BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
PRIMARY KEY (`user_id`, `role_id`),
KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';


-- 7. 角色-菜单关联表（多对多）
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
`role_id`   BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
`menu_id`   BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
PRIMARY KEY (`role_id`, `menu_id`),
KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';


-- 8. 用户-岗位关联表（多对多）
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
`user_id`   BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
`post_id`   BIGINT UNSIGNED NOT NULL COMMENT '岗位ID',
PRIMARY KEY (`user_id`, `post_id`),
KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与岗位关联表';


-- 9. 角色-部门关联表（用于数据权限：角色可查看哪些部门的数据）
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
`role_id`   BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
`dept_id`   BIGINT UNSIGNED NOT NULL COMMENT '部门ID',
PRIMARY KEY (`role_id`, `dept_id`),
KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表（数据权限）';


-- ============================================================
-- 初始化数据（最基础的管理员角色 + 菜单框架）
-- ============================================================

-- 初始化部门
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `status`, `del_flag`, `create_time`) VALUES
(1, 0, '0', '总公司', 0, '0', '0', NOW()),
(2, 1, '0,1', '研发部门', 1, '0', '0', NOW()),
(3, 1, '0,1', '市场部门', 2, '0', '0', NOW()),
(4, 1, '0,1', '测试部门', 3, '0', '0', NOW()),
(5, 1, '0,1', '财务部门', 4, '0', '0', NOW());

-- 初始化岗位
INSERT INTO `sys_post` (`post_id`, `post_code`, `post_name`, `order_num`, `status`, `create_time`) VALUES
(1, 'ceo', '董事长', 1, '0', NOW()),
(2, 'se', '高级工程师', 2, '0', NOW()),
(3, 'sse', '架构师', 3, '0', NOW()),
(4, 'pm', '产品经理', 4, '0', NOW());

-- 初始化用户（密码：admin123，BCrypt加密后示例值）
-- 注意：生产环境请使用真实BCrypt加密值替换
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phone`, `sex`, `avatar`, `password`, `status`, `del_flag`, `create_time`) VALUES
(1, 2, 'admin', '系统管理员', '00', 'admin@example.com', '13800138000', '0', '', '$2a$10$7JB720yubVS6vJ5xK3L/4OQZ3W5qGJmV2p1Lk8nM9oP0QrStUvWx', '0', '0', NOW()),
(2, 3, 'zhangsan', '张三', '00', 'zhangsan@example.com', '13800138001', '0', '', '$2a$10$7JB720yubVS6vJ5xK3L/4OQZ3W5qGJmV2p1Lk8nM9oP0QrStUvWx', '0', '0', NOW());

-- 初始化角色
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `del_flag`, `create_time`) VALUES
(1, '超级管理员', 'admin', 1, '1', '0', '0', NOW()),
(2, '普通角色', 'common', 2, '2', '0', '0', NOW());

-- 初始化菜单（简化版，仅展示框架）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_time`) VALUES
(1, '系统管理', 0, 1, 'system', NULL, 'M', '0', '0', NULL, 'el-icon-setting', NOW()),
(2, '用户管理', 1, 1, 'user', 'system/user/index', 'C', '0', '0', 'system:user:list', 'el-icon-user', NOW()),
(3, '角色管理', 1, 2, 'role', 'system/role/index', 'C', '0', '0', 'system:role:list', 'el-icon-s-custom', NOW()),
(4, '菜单管理', 1, 3, 'menu', 'system/menu/index', 'C', '0', '0', 'system:menu:list', 'el-icon-menu', NOW()),
(5, '部门管理', 1, 4, 'dept', 'system/dept/index', 'C', '0', '0', 'system:dept:list', 'el-icon-office-building', NOW()),
-- 按钮权限（用户管理下的增删改查按钮）
(100, '用户查询', 2, 1, '', NULL, 'F', '0', '0', 'system:user:query', '#', NOW()),
(101, '用户新增', 2, 2, '', NULL, 'F', '0', '0', 'system:user:add', '#', NOW()),
(102, '用户修改', 2, 3, '', NULL, 'F', '0', '0', 'system:user:edit', '#', NOW()),
(103, '用户删除', 2, 4, '', NULL, 'F', '0', '0', 'system:user:remove', '#', NOW()),
(104, '用户导出', 2, 5, '', NULL, 'F', '0', '0', 'system:user:export', '#', NOW()),
-- 按钮权限（角色管理下的按钮）
(200, '角色查询', 3, 1, '', NULL, 'F', '0', '0', 'system:role:query', '#', NOW()),
(201, '角色新增', 3, 2, '', NULL, 'F', '0', '0', 'system:role:add', '#', NOW()),
(202, '角色修改', 3, 3, '', NULL, 'F', '0', '0', 'system:role:edit', '#', NOW()),
(203, '角色删除', 3, 4, '', NULL, 'F', '0', '0', 'system:role:remove', '#', NOW()),
(204, '角色导出', 3, 5, '', NULL, 'F', '0', '0', 'system:role:export', '#', NOW());

-- 关联关系
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1), (2, 2);

-- 超级管理员拥有所有菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, menu_id FROM sys_menu;

-- 普通角色只拥有查询权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),
(2, 100), (2, 200);