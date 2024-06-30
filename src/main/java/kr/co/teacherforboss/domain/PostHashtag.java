package kr.co.teacherforboss.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import kr.co.teacherforboss.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostHashtag extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtagId")
    private Hashtag hashtag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostHashtag that = (PostHashtag) o;
        return Objects.equals(hashtag.getName(), that.hashtag.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashtag);
    }

    public static PostHashtagBuilder builder() {
        return new CustomPostHashtagBuilder();
    }

    private static class CustomPostHashtagBuilder extends PostHashtagBuilder {
        @Override
        public PostHashtag build() {
            PostHashtag postHashtag = super.build();
            if (postHashtag.getPost() != null) {
                postHashtag.getPost().addHashtag(postHashtag);
            }
            return postHashtag;
        }
    }

    @Override
    public boolean softDelete() {
        if (super.softDelete()) {
            post.getHashtags().remove(this);
            return true;
        }
        return false;
    }
}
