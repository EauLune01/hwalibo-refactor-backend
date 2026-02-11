package hwalibo.refactor.user.domain;

import hwalibo.refactor.global.domain.BaseTimeEntity;
import hwalibo.refactor.global.domain.Gender;
import hwalibo.refactor.global.domain.Role;
import hwalibo.refactor.global.domain.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "providerId"})
        }
)
@SQLDelete(sql = "UPDATE users SET status = 'DELETED', deleted_at = CURRENT_TIMESTAMP WHERE user_id = ?")
@SQLRestriction("status = 'ACTIVE'")
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String provider;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    private double rate;

    @Column(name = "num_review")
    private Integer numReview;

    @Column(length = 512)
    private String profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private String refreshToken;

    @Column(length = 1024)
    private String naverRefreshToken;

    /**
     * 생성자
     */
    private User(String username, String name, String provider, String providerId, String profile, Gender gender) {
        this.username = username;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.profile = profile;
        this.gender = gender;
        this.role = Role.ROLE_USER;
        this.status = UserStatus.ACTIVE;
        this.numReview = 0;
        this.rate = 0.0;
    }

    // 인증 전용 private 생성자
    private User(Long id, String username, String name, Role role, Gender gender) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.gender = gender;
        this.status = UserStatus.ACTIVE; // 인증된 사용자는 기본적으로 활성 상태로 간주
    }

    /**
     * 정적 팩토리 메소드
     */
    public static User create(String provider, String providerId, String name, String profile, Gender gender) {
        String generatedUsername = provider + "_" + providerId;
        return new User(generatedUsername, name, provider, providerId, profile, gender);
    }

    //인증 전용 정적 팩토리 메서드
    public static User fromClaims(Long id, String username, String name, Role role, Gender gender) {
        return new User(id, username, name, role, gender);
    }

    /* ================= 비즈니스 메서드 (Domain Methods) ================= */

    public void updateName(String newName) {
        this.name = newName;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profile = profileImageUrl;
    }

    public void updateProfile(String newName, String profileImageUrl) {
        if (newName != null) this.name = newName;
        if (profileImageUrl != null) this.profile = profileImageUrl;
    }

    public void addReview() {
        if (this.numReview == null) this.numReview = 0;
        this.numReview++;
    }

    public void removeReview() {
        if (this.numReview != null && this.numReview > 0) {
            this.numReview--;
        }
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNaverRefreshToken(String naverRefreshToken) {
        this.naverRefreshToken = naverRefreshToken;
    }


    public void withdrawAndAnonymize() {
        this.name = "탈퇴한 사용자";
        this.profile = null;
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        this.refreshToken = null;
        this.naverRefreshToken = null;
    }

    public void reActivate() {
        this.status = UserStatus.ACTIVE;
        this.deletedAt = null;
    }

    /* ================= UserDetails 구현 ================= */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return this.username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return this.status == UserStatus.ACTIVE; }
}
