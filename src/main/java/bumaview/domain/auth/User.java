package bumaview.domain.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import bumaview.domain.answers.Answer;
import bumaview.domain.scores.Score;

import java.util.List;

@Table(name = "users")
@Getter
@Entity
@NoArgsConstructor
public class User {
    @Id
    private String id;

    private String nickname;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Score> scores;

    @OneToMany(mappedBy = "user")
    private List<Answer> answers;
    
    public User(String id, String nickname, String password, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }
}
