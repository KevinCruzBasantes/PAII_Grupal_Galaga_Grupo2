package ec.edu.uce.PersistenciaGalaga.repository;

import ec.edu.uce.PersistenciaGalaga.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository <Player,Long> {

}
