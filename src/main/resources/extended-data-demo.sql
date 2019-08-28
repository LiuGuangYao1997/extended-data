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

 Date: 28/08/2019 22:13:34
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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of ext_product
-- ----------------------------
INSERT INTO `ext_product` VALUES (1, 1, '2019-08-28 14:37:15', '上海联杰网络器材公司', '上海市');
INSERT INTO `ext_product` VALUES (2, 2, '2019-08-28 14:37:57', '合肥市小森林绿化植物公司', '合肥市');
INSERT INTO `ext_product` VALUES (3, 3, '2019-08-28 14:38:35', '东莞市鼎浩办公器材有限公司', '东莞市');

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
-- Table structure for extended_config_detail
-- ----------------------------
DROP TABLE IF EXISTS `extended_config_detail`;
CREATE TABLE `extended_config_detail`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `entity_filed_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体属性名',
  `entity_filed_alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体属性别名(返回的map key)',
  `extended_main_id` int(10) NULL DEFAULT NULL COMMENT '配置主表ID，外键',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_config_detail_main`(`extended_main_id`) USING BTREE,
  CONSTRAINT `fk_config_detail_main` FOREIGN KEY (`extended_main_id`) REFERENCES `extended_config_main` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of extended_config_detail
-- ----------------------------
INSERT INTO `extended_config_detail` VALUES (1, 'username', 'username', 1);
INSERT INTO `extended_config_detail` VALUES (2, 'age', 'age', 1);
INSERT INTO `extended_config_detail` VALUES (3, 'address', 'address', 2);
INSERT INTO `extended_config_detail` VALUES (4, 'email', 'email', 2);

-- ----------------------------
-- Table structure for extended_config_main
-- ----------------------------
DROP TABLE IF EXISTS `extended_config_main`;
CREATE TABLE `extended_config_main`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `entity_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体名',
  `entity_alias` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体别名',
  `is_main_table` int(1) NULL DEFAULT NULL COMMENT '是否是主表：0为否，1为是',
  `primary_key_filed_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主键属性名',
  `foreign_key_filed_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当是扩展表时，与主表关联的外键的属性名',
  `business_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务代码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of extended_config_main
-- ----------------------------
INSERT INTO `extended_config_main` VALUES (1, 'User', 'user', 1, 'id', '', '1');
INSERT INTO `extended_config_main` VALUES (2, 'ExtUser', 'extUser', 0, '', 'mainTableId', '1');

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '网线', 5, 30, '电子产品');
INSERT INTO `product` VALUES (2, '袖珍椰子', 10, 50, '绿植');
INSERT INTO `product` VALUES (3, '办公椅', 20, 60, '办公用具');

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
