package ec.edu.uce.PersistenciaGalaga.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String userName;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameState> gameStates = new ArrayList<>();

    public Player() {
    }

    public Player(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<GameState> getGameStates() {
        return gameStates;
    }

    public void setGameStates(List<GameState> gameStates) {
        this.gameStates = gameStates;
    }

    public void addGameState(GameState gameState) {
        gameStates.add(gameState);
        gameState.setPlayer(this);
    }

    public void removeGameState(GameState gameState) {
        gameStates.remove(gameState);
        gameState.setPlayer(null);
    }
}
