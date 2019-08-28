package com.ustcinfo.extended.repository;

import com.ustcinfo.extended.entity.ExtendedConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: liu.guangyao@ustcinfo.com
 * @Date: 2019/8/28 9:03
 */
public interface ExtendedConfigurationRepository extends JpaRepository<ExtendedConfiguration, Integer> {
}
