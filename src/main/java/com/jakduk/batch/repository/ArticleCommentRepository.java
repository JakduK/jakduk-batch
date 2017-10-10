package com.jakduk.batch.repository;

import com.jakduk.batch.model.db.ArticleComment;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author <a href="mailto:phjang1983@daum.net">Jang,Pyohwan</a>
 * @company  : http://jakduk.com
 * @date     : 2014. 12. 3.
 * @desc     :
 */
public interface ArticleCommentRepository extends MongoRepository<ArticleComment, String>, ArticleCommentRepositoryCustom {
}