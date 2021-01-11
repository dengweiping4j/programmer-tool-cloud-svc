package com.programmer.util.repository;

import com.programmer.util.domain.DriverPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @author: DengWeiPing
 * @time: 2020/8/21 10:22
 */
@Repository
public interface DriverPathRepository extends JpaRepository<DriverPath, String>, JpaSpecificationExecutor<DriverPath> {


    DriverPath findByDriverAndType(String driverPath, String type);
}
