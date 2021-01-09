package com.programmer.util.repository;

import com.programmer.util.domain.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author dengweiping
 * @date 2021/1/9 16:50
 */
@Repository
public interface TableRepository extends JpaRepository<TableEntity, String>, JpaSpecificationExecutor<TableEntity> {
}
