package kr.co.teacherforboss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import kr.co.teacherforboss.converter.StringConverter;
import java.util.List;
import kr.co.teacherforboss.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @NotNull
    @Column(length = 30)
    private String title;

    @NotNull
    @Column(length = 1000)
    private String content;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer likeCount;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer viewCount;

    @NotNull
    @Column
    @ColumnDefault("0")
    private Integer bookmarkCount;

    @Column(length = 36)
    private String imageUuid;

    @Column
    @Convert(converter = StringConverter.class)
    private List<String> imageIndex;

    @OneToMany(mappedBy = "post")
    @SQLRestriction(value = "status = 'ACTIVE'")
    private List<PostHashtag> hashtagList;

    @OneToMany(mappedBy = "post")
    private List<Comment> commentList;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void addHashtag(PostHashtag postHashtag) {
        this.hashtagList.add(postHashtag);
    }
}
