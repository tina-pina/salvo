package com.codeoftheweb.salvo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.query.Param;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
//List<Player> findByLastName(String lastName);
    Player findByUserName(@Param("name") String userName);
}



