package com.mojagap.mojanode.repository.aws;

import com.mojagap.mojanode.model.aws.S3Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface S3DocumentRepository extends JpaRepository<S3Document, Integer> {
}
