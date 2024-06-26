package kinoteatr.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import kinoteatr.model.ReserveSeatConfiguration;
import kinoteatr.service.ReservationService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
class ReservationController {

    static final class Routes {
        static final String MOVIE_ROOT = "/movies/{movieName}";
        static final String RESERVATION_ROOT = "/reservation";
        static final String REPERTOIRE_ROOT = "/{repertoireId}";
        static final String MOVIE_RESERVATION = MOVIE_ROOT + RESERVATION_ROOT;
        static final String MOVIE_RESERVATION_ID = MOVIE_ROOT + RESERVATION_ROOT + REPERTOIRE_ROOT;
        static final String SEAT_RESERVATION = RESERVATION_ROOT + "/save/{repertoireId}";
    }

    private final ReservationService reservationService;

    @GetMapping(Routes.MOVIE_RESERVATION)
    public String movieReservationPage(@PathVariable("movieName") final String movieName, final Model model) {
        return reservationService.showMovieReservationPage(movieName, model);
    }

    @GetMapping(Routes.MOVIE_RESERVATION_ID)
    public String movieReservationSeatPage(@PathVariable("movieName") final String movieName,
                                           @PathVariable("repertoireId") final Long repertoireId, final Model model) {
        return reservationService.showMovieReservationSeatPage(movieName, repertoireId, model);
    }

    @PostMapping(Routes.SEAT_RESERVATION)
    public String reserve(@ModelAttribute("seatInfo") final ReserveSeatConfiguration reserveSeatConfiguration,
                          @PathVariable("repertoireId") final Long repertoireId, final Principal principal) {
        return reservationService.reservation(reserveSeatConfiguration, repertoireId, principal);
    }
}