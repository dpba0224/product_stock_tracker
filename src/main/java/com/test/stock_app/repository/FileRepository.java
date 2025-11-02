package com.test.stock_app.repository;

import com.test.stock_app.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,String> {

}
