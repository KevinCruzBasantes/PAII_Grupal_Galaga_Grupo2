package ec.edu.uce.PersistenciaGalaga.services;

import ec.edu.uce.PersistenciaGalaga.models.GameState;
import ec.edu.uce.PersistenciaGalaga.repository.GameStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameStateServices {

    @Autowired
    private GameStateRepository repository;

    public GameState saveGameState(GameState gameState) {
        return repository.save(gameState);
    }

    public List<GameState> getGameStatesByPlayerId(Long playerId) {
        return repository.findByPlayerId(playerId);
    }
    // Additional method to set game state to paused
    public GameState savePausedGameState(GameState gameState) {
        gameState.setState("paused");
        return repository.save(gameState);
    }
}
