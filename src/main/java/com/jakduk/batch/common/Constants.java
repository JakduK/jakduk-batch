package com.jakduk.batch.common;

public class Constants {

    public final static Integer ARTICLE_SHORT_CONTENT_LENGTH = 100;

    public final static Integer ES_BULK_LIMIT = 1000;
    public final static Integer ES_AWAIT_CLOSE_TIMEOUT_MINUTES = 2;
    public final static String ES_TYPE_ARTICLE = "article";
    public final static String ES_TYPE_COMMENT = "comment";
    public final static String ES_TYPE_GALLERY = "gallery";
    public final static String ES_TYPE_SEARCH_WORD = "search_word";

    /**
     * mongoDB collection 이름
     */
    public final static String COLLECTION_ARTICLE = "article";
    public final static String COLLECTION_ARTICLE_COMMENT = "articleComment";
    public final static String COLLECTION_GALLERY = "gallery";

    /**
     * 사진 상태값.
     */
    public enum GALLERY_STATUS_TYPE {
        TEMP,
        ENABLE,
    }

    /**
     * 댓글 히스토리 상태
     */
    public enum BOARD_FREE_COMMENT_HISTORY_TYPE {
        CREATE,
        EDIT
    }

    // 배치 타입
    public enum BATCH_TYPE {
        CHANGE_BOARD_CONTENT_IMAGE_URL_01,
        APPEND_GALLERY_FILE_EXT_01,
        BOARD_FREE_ADD_SHORT_CONTENT_01,			// 본문 미리보기 용으로, HTML이 제거된 100자 정도의 본문 요약 필드가 필요하다
        BOARD_FREE_ADD_LAST_UPDATED_01,				// BoardFree에 lastUpdated 필드를 추가한다.
        BOARD_FREE_ADD_LINKED_GALLERY_01,			// BoardFree에 linkedGallery 필드를 추가한다.
        GALLERY_CHANGE_POSTS_TO_LINKED_ITEMS_01,	// Gallery의 posts를 linkedItems으로 바꾼다.
        GALLERY_ADD_HASH_FIELD_01,					// Gallery에 hash 필드 추가.
        GALLERY_CHECK_NAME_01,						// Gallery 의 name이 fileName과 동일하면 ""로 엎어친다.
        BOARD_FREE_COMMENT_ADD_HISTORY_01,			// BoardFreeComment에 history 필드를 추가한다.
        ARTICLE_COMMENT_EDIT_BOARD_01               // ArticeComment의 article.board와 실제 article의 board를 일치시킴.
    }

    /**
     * 사진을 등록한 출처
     */
    public enum GALLERY_FROM_TYPE {
        ARTICLE,
        ARTICLE_COMMENT
    }

}
