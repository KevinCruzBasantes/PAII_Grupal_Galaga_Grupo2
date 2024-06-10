package ec.edu.uce.PersistenciaGalaga.repository;

import ec.edu.uce.PersistenciaGalaga.models.GameState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameStateRepository extends JpaRepository<GameState, Long> {
    List<GameState> findByPlayerId(Long playerId);
}
