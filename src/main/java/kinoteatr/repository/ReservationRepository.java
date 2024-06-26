package kinoteatr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import kinoteatr.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {}