package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.BoardFree;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardFreeRepository extends MongoRepository<BoardFree, String>, BoardFreeRepositoryCustom {
}
