package ec.edu.uce.PersistenciaGalaga.controller;

import ec.edu.uce.PersistenciaGalaga.models.GameState;
import ec.edu.uce.PersistenciaGalaga.models.Player;
import ec.edu.uce.PersistenciaGalaga.services.PlayerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private PlayerServices playerServices;

    @PostMapping("/register")
    public ResponseEntity<Player> registerPlayer(@RequestBody Player player) {
        try {
            Player savedPlayer = playerServices.savePlayer(player);
            return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getAllPlayers() {
        try {
            List<Player> players = playerServices.getAllPlayers();
            if (players.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(players, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") long id) {
        Optional<Player> playerData = playerServices.getPlayerById(id);
        return playerData.map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/player/{id}")
    public ResponseEntity<HttpStatus> deletePlayer(@PathVariable("id") long id) {
        try {
            playerServices.deletePlayer(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para guardar el estado del juego, renombrado para evitar conflicto
    @PostMapping("/player/saveState")
    public ResponseEntity<Void> saveGameState(@RequestBody GameState gameState) {
        // Aquí debes implementar la lógica para guardar el estado del juego
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
