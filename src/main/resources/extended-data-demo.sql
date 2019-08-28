/*
 Navicat Premium Data Transfer

 Source Server         : Mysql
 Source Server Type    : MySQL
 Source Server Version : 50527
 Source Host           : localhost:3306
 Source Schema         : extended-data-demo

 Target Server Type    : MySQL
 Target Server Version : 50527
 File Encoding         : 65001

 Date: 28/08/2019 14:32:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ext_product
-- ----------------------------
DROP TABLE IF EXISTS `ext_product`;
CREATE TABLE `ext_product`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `main_table_id` int(10) NULL DEFAULT NULL COMMENT '关联主表ID',
  `production_date` datetime NULL DEFAULT NULL COMMENT '生产日期',
  `producer` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生产公司',
  `production_address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生产地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_product_ext`(`main_table_id`) USING BTREE,
  CONSTRAINT `fk_product_ext` FOREIGN KEY (`main_table_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for ext_user
-- ----------------------------
DROP TABLE IF EXISTS `ext_user`;
CREATE TABLE `ext_user`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `main_table_id` int(10) NULL DEFAULT NULL COMMENT '关联主表ID',
  `address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `tel` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_user_ext`(`main_table_id`) USING BTREE,
  CONSTRAINT `fk_user_ext` FOREIGN KEY (`main_table_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of ext_user
-- ----------------------------
INSERT INTO `ext_user` VALUES (1, 1, '合肥市蜀山区', '13021341352', '123123@qq.com');
INSERT INTO `ext_user` VALUES (2, 2, '武汉市汉口区', '15585233142', 'lisi@163.com');
INSERT INTO `ext_user` VALUES (3, 3, '成都市双流区', '18359215721', 'liubei520@gmail.com');
INSERT INTO `ext_user` VALUES (4, 4, '南京市鼓楼区', '15582304831', 'sun@sina.com');

-- ----------------------------
-- Table structure for extended_configuration
-- ----------------------------
DROP TABLE IF EXISTS `extended_configuration`;
CREATE TABLE `extended_configuration`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `data_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据类型(业务描述，枚举类)',
  `table_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段所在表名',
  `filed_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `entity_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体名',
  `entity_filed_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体字段名',
  `entity_filed_name_zh` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体字段中文名',
  `relevant_main_table` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联主表名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of extended_configuration
-- ----------------------------
INSERT INTO `extended_configuration` VALUES (1, 'USER_INFO_SELECT_1', 'user', 'id', 'User', 'id', 'ID', 'user');
INSERT INTO `extended_configuration` VALUES (2, 'USER_INFO_SELECT_1', 'user', 'name', 'User', 'username', '用户名', 'user');
INSERT INTO `extended_configuration` VALUES (3, 'USER_INFO_SELECT_1', 'user', 'age', 'User', 'age', '年龄', 'user');
INSERT INTO `extended_configuration` VALUES (4, 'USER_INFO_SELECT_1', 'ext_user', 'tel', 'ExtUser', 'tel', '电话', 'user');
INSERT INTO `extended_configuration` VALUES (5, 'USER_INFO_SELECT_1', 'ext_user', 'email', 'ExtUser', 'email', '邮箱', 'user');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '产品名',
  `amount` int(10) NULL DEFAULT NULL COMMENT '数量',
  `price` int(10) NULL DEFAULT NULL COMMENT '价格',
  `type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `age` int(3) NULL DEFAULT NULL COMMENT '年龄',
  `male` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'zhangsan', '123456', 18, 'F');
INSERT INTO `user` VALUES (2, 'lisi', 'qwerty', 20, 'F');
INSERT INTO `user` VALUES (3, 'liubei', '232131', 56, 'F');
INSERT INTO `user` VALUES (4, 'sunshangxiang', 'gsdg12', 28, 'M');

SET FOREIGN_KEY_CHECKS = 1;
