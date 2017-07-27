package com.jakduk.batch.model.embedded;

import com.jakduk.batch.common.JakdukConst;
import com.jakduk.batch.model.db.UserPicture;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by pyohwanjang on 2017. 2. 27..
 */

@NoArgsConstructor
@Getter
public class UserPictureInfo {

    private String id;
    private String sourceType;
    private String smallPictureUrl;
    private String largePictureUrl;

    public UserPictureInfo(UserPicture userPicture, String smallPictureUrl, String largePictureUrl) {
        this.id = userPicture.getId();
        this.sourceType = userPicture.getSourceType();
        this.smallPictureUrl = smallPictureUrl;
        this.largePictureUrl = largePictureUrl;
    }

}
