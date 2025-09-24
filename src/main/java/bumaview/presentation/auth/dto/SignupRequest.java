package bumaview.presentation.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    
    @NotBlank(message = "ID는 필수입니다")
    @Size(min = 3, max = 20, message = "ID는 3-20자 사이여야 합니다")
    private String id;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 50, message = "닉네임은 50자 이하여야 합니다")
    private String nickname;
    
    @NotBlank(message = "패스워드는 필수입니다")
    @Size(min = 8, message = "패스워드는 최소 8자 이상이어야 합니다")
    private String password;
    
    public SignupRequest(String id, String nickname, String password) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
    }
}