package ec.edu.uce.PersistenciaGalaga.controller;

import ec.edu.uce.PersistenciaGalaga.models.GameState;
import ec.edu.uce.PersistenciaGalaga.models.Player;
import ec.edu.uce.PersistenciaGalaga.services.GameStateServices;
import ec.edu.uce.PersistenciaGalaga.services.PlayerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class GameStateController {
    @Autowired
    private GameStateServices gameStateServices;
    @Autowired
    private PlayerServices playerServices;

    @PostMapping("/saveState")
    public ResponseEntity<Void> saveGameState(@RequestBody GameState gameState, @RequestBody Long playerId) {
        // Obtener el jugador por ID
        Player player = playerServices.getPlayerById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // Asignar el jugador al estado del juego
        gameState.setPlayer(player);

        // Guardar el estado del juego
        gameStateServices.saveGameState(gameState);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/player/{playerId}/gameStates")
    public ResponseEntity<List<GameState>> getGameStatesByPlayerId(@PathVariable Long playerId) {
        List<GameState> gameStates = gameStateServices.getGameStatesByPlayerId(playerId);
        if (gameStates.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(gameStates, HttpStatus.OK);
    }
    @PostMapping("/savePausedState")
    public ResponseEntity<Void> savePausedGameState(@RequestBody GameState gameState) {
        gameStateServices.savePausedGameState(gameState);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}