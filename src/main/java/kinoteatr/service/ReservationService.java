package kinoteatr.service;

import org.springframework.ui.Model;
import kinoteatr.model.ReserveSeatConfiguration;

import java.security.Principal;

public interface ReservationService {

    String showMovieReservationPage(final String movieName, final Model model);

    String showMovieReservationSeatPage(final String movieName, final Long repertoireId, final Model model);

    String reservation(final ReserveSeatConfiguration reserveSeatConfiguration, final Long repertoireId, final Principal principal);
}