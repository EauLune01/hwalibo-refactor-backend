package hwalibo.refactor.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import hwalibo.refactor.user.dto.result.UserReviewResult;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewResponse {

    private Long id;
    private String toiletName;
    private String gender;
    private String line;
    private String desc;
    private Double star;
    private List<ImageInfoResponse> photo;
    private List<String> tag;
    private Boolean isDisabledAccess;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageInfoResponse {
        private Long id;
        private String url;
    }

    public static UserReviewResponse from(UserReviewResult result) {
        return UserReviewResponse.builder()
                .id(result.getId())
                .toiletName(result.getToiletName())
                .gender(result.getGender())
                .line(result.getLine())
                .desc(result.getContent())
                .star(result.getRating())
                .photo(result.getPhotos().stream()
                        .map(img -> new ImageInfoResponse(img.getId(), img.getUrl()))
                        .toList())
                .tag(result.getTags().stream()
                        .map(Enum::name)
                        .toList())
                .isDisabledAccess(result.isDisabledAccess())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}
