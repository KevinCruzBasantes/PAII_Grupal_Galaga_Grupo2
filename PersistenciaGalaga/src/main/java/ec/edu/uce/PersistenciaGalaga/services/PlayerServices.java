package ec.edu.uce.PersistenciaGalaga.services;

import ec.edu.uce.PersistenciaGalaga.models.Player;
import ec.edu.uce.PersistenciaGalaga.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerServices {

    @Autowired
    private PlayerRepository repository;

    public Player savePlayer(Player player) {
        return repository.save(player);
    }

    public Optional<Player> getPlayerById(Long id) {
        return repository.findById(id);
    }

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public void deletePlayer(Long id) {
        repository.deleteById(id);
    }

}
