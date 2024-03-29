/*
 Navicat Premium Data Transfer

 Source Server         : Mysql
 Source Server Type    : MySQL
 Source Server Version : 50527
 Source Host           : localhost:3306
 Source Schema         : extended-data

 Target Server Type    : MySQL
 Target Server Version : 50527
 File Encoding         : 65001

 Date: 03/09/2019 09:50:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ext_product
-- ----------------------------
DROP TABLE IF EXISTS `ext_product`;
CREATE TABLE `ext_product`  (
  `main_table_id` int(10) NOT NULL COMMENT '关联主表ID',
  `production_date` datetime NULL DEFAULT NULL COMMENT '生产日期',
  `producer` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生产公司',
  `production_address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生产地址',
  PRIMARY KEY (`main_table_id`) USING BTREE,
  INDEX `fk_product_ext`(`main_table_id`) USING BTREE,
  CONSTRAINT `ext_product_ibfk_1` FOREIGN KEY (`main_table_id`) REFERENCES `product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for ext_user
-- ----------------------------
DROP TABLE IF EXISTS `ext_user`;
CREATE TABLE `ext_user`  (
  `main_table_id` int(10) NOT NULL COMMENT '关联主表ID',
  `address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `tel` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  PRIMARY KEY (`main_table_id`) USING BTREE,
  INDEX `fk_user_ext`(`main_table_id`) USING BTREE,
  CONSTRAINT `ext_user_ibfk_1` FOREIGN KEY (`main_table_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of ext_user
-- ----------------------------
INSERT INTO `ext_user` VALUES (6, '成都市双流区', '1234124213', 'lzm123@qq.com');
INSERT INTO `ext_user` VALUES (7, '江苏南京', '1851252321', 'sun13124@qq.com');
INSERT INTO `ext_user` VALUES (8, '山西太原', '1304235274', 'machao@163.com');
INSERT INTO `ext_user` VALUES (14, '安徽桐城市', '15523215853', 'wanghao@qq.com');
INSERT INTO `ext_user` VALUES (20, 'anhui huangshan', NULL, NULL);

-- ----------------------------
-- Table structure for extended_data_entity
-- ----------------------------
DROP TABLE IF EXISTS `extended_data_entity`;
CREATE TABLE `extended_data_entity`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `main_entity_package` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主实体类完全包名',
  `main_entity_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主实体名',
  `main_entity_alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主实体别名',
  `main_entity_primarykey` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主实体主键所在字段名',
  `ext_entity_package` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扩展实体类完全包名',
  `ext_entity_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扩展实体名',
  `ext_entity_alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扩展实体别名',
  `ext_entity_foreignkey` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '扩展实体外键所在字段名',
  `data_type_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据类型代码',
  `data_name_zh` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据中文名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of extended_data_entity
-- ----------------------------
INSERT INTO `extended_data_entity` VALUES (1, 'com.ustcinfo.extended.entity', 'User', 'user', 'id', 'com.ustcinfo.extended.entity', 'ExtUser', 'extUser', 'mainTableId', '1', '用户信息');

-- ----------------------------
-- Table structure for extended_data_filed
-- ----------------------------
DROP TABLE IF EXISTS `extended_data_filed`;
CREATE TABLE `extended_data_filed`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ext_entity_id` int(10) NOT NULL COMMENT '与entended_data_entity关联的外键',
  `filed_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性名',
  `filed_alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性别名',
  `is_main_entity_filed` int(1) NULL DEFAULT NULL COMMENT '是否是主表的字段：0为扩展表字段，1为主表字段',
  `filed_name_zh` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段中文名',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_extfiled_extentity`(`ext_entity_id`) USING BTREE,
  CONSTRAINT `fk_extfiled_extentity` FOREIGN KEY (`ext_entity_id`) REFERENCES `extended_data_entity` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of extended_data_filed
-- ----------------------------
INSERT INTO `extended_data_filed` VALUES (1, 1, 'username', 'name', 1, '用户名');
INSERT INTO `extended_data_filed` VALUES (2, 1, 'age', 'age', 1, '年龄');
INSERT INTO `extended_data_filed` VALUES (3, 1, 'email', 'email', 0, '邮箱');
INSERT INTO `extended_data_filed` VALUES (4, 1, 'male', 'male', 1, '性别');
INSERT INTO `extended_data_filed` VALUES (5, 1, 'address', 'address', 0, '地址');

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
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (6, 'liubei', '231242', 37, 'F');
INSERT INTO `user` VALUES (7, 'sunshangxiang', '521312', 30, 'M');
INSERT INTO `user` VALUES (8, 'machao', '421342', 34, 'F');
INSERT INTO `user` VALUES (14, 'wanghao', '123123', 18, 'F');
INSERT INTO `user` VALUES (20, 'hongpei', NULL, 25, NULL);

SET FOREIGN_KEY_CHECKS = 1;
