package org.eclipse.dirigible.components.base.artefact;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ArtefactRepository<A extends Artefact, ID> extends JpaRepository<A, ID> {

    List<A> findByLocation(String location);

    Optional<A> findByName(String name);

    Optional<A> findByKey(String key);

    void setRunningToAll(boolean running);

}
