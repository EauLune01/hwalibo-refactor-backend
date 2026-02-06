package hwalibo.refactor.user.service;

import hwalibo.refactor.global.exception.auth.UnauthorizedException;
import hwalibo.refactor.global.exception.user.DuplicateUserNameException;
import hwalibo.refactor.global.exception.user.IdenticalNameException;
import hwalibo.refactor.global.exception.user.UserNotFoundException;
import hwalibo.refactor.user.domain.User;
import hwalibo.refactor.user.dto.command.UserNameUpdateCommand;
import hwalibo.refactor.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    private final UserRepository userRepository;
    private final UserCacheService userCacheService;

    public void updateUserName(Long userId, UserNameUpdateCommand command) {
        User user = userRepository.findById(userId)
                .map(u -> {
                    validateNicknameChange(u, command.getNewName());
                    return u;
                })
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        user.updateName(command.getNewName());
        userCacheService.evictUserRate(user.getId());
    }

    /******************** Helper Method ********************/

    private void validateNicknameChange(User user, String newName) {
        if (user.getName().equals(newName)) {
            throw new IdenticalNameException("현재 닉네임과 동일한 닉네임입니다.");
        }
        if (userRepository.existsByName(newName)) {
            throw new DuplicateUserNameException("이미 존재하는 닉네임입니다.");
        }
    }
}
