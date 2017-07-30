package ru.lonedeveloper.flash.codingtask.domain;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.roo.addon.layers.repository.jpa.RooJpaRepository;

@RooJpaRepository(domainType = Solution.class)
public interface SolutionRepository {

	List<Solution> findByIp(final String ip, final Sort sort);

	int countByIpAndTaskAndSuccessful(final String ip, final Task task, final boolean successful);
}
