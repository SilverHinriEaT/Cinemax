package kinoteatr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import kinoteatr.model.Movie;
import kinoteatr.model.Repertoire;
import kinoteatr.model.Reservation;
import kinoteatr.model.ReserveSeatConfiguration;
import kinoteatr.model.SeatReservation;
import kinoteatr.model.Ticket;
import kinoteatr.repository.MovieRepository;
import kinoteatr.repository.RepertoireRepository;
import kinoteatr.repository.ReservationRepository;
import kinoteatr.repository.TicketRepository;
import kinoteatr.repository.UserRepository;
import kinoteatr.service.ReservationService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.lang.Boolean.TRUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private static final List<String> rows = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");

    private final RepertoireRepository repertoireRepository;
    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Override
    public String showMovieReservationPage(final String movieName, final Model model) {
        final Movie movie = movieRepository.findByTitle(movieName);
        final List<Repertoire> repertoires = repertoireRepository.findByMovieId(movie.getId());
        model.addAttribute("repertoires", repertoires);
        return "reservation-movie";
    }

    @Override
    public String showMovieReservationSeatPage(final String movieName, final Long repertoireId, final Model model) {
        reserveSeats(model, repertoireId);
        model.addAttribute("movieName", movieName);
        addRows(model, repertoireId);
        return "reservation-seat-movie";
    }

    @Override
    public String reservation(final ReserveSeatConfiguration reserveSeatConfiguration, final Long repertoireId, final Principal principal) {
        final List<String> reservedSeats = getReservedSeats(reserveSeatConfiguration);
        if (reservedSeats.size() > 0 && reservedSeats.size() <= 15) {
            final UUID uuid = UUID.randomUUID();
            final Ticket ticket = new Ticket();
            ticket.setSeat(String.join(",", reservedSeats));
            ticket.setUuid(uuid);
            ticketRepository.save(ticket);

            final Reservation reservation = new Reservation();
            reservation.setTicket(ticketRepository.findByUuid(uuid).orElse(null));
            Repertoire repertoire = repertoireRepository.findById(repertoireId).orElse(null);
            try {
                if (repertoire != null) {
                    reservation.setMovie(movieRepository.findByTitle(repertoire.getMovie().getTitle()));
                }
            } catch (NullPointerException e) {}
            reservation.setRepertoire(repertoire);
            reservation.setUser(userRepository.findByUsername(principal.getName()));
            reservationRepository.save(reservation);
            return "redirect:/successful";
        } else {
            return "redirect:/unsuccessful";
        }
    }

    private void reserveSeats(final Model model, @PathVariable("repertoireId") final Long repertoireId) {
        final ReserveSeatConfiguration reserveSeatConfigurationMovie = new ReserveSeatConfiguration();
        final SeatReservation seatReservation = new SeatReservation();
        final Map<String, Boolean> mapMovie = new HashMap<>();
        getReservedSeats(repertoireId).forEach(seat -> mapMovie.put(seat, true));
        reserveSeatConfigurationMovie.setMap(mapMovie);
        reserveSeatConfigurationMovie.setSeatReservation(seatReservation);
        model.addAttribute("seatInfo", reserveSeatConfigurationMovie);
    }

    private Set<String> getReservedSeats(@PathVariable("repertoireId") final Long repertoireId) {
        final Repertoire repertoire = repertoireRepository.getOne(repertoireId);
        final Set<String> reservedSeats = new HashSet<>();
        for (final Reservation reservation : repertoire.getReservations()) {
            reservedSeats.addAll(Arrays.asList(reservation.getTicket().getSeat().split(",")));
        }
        log.info("Зарезервовані місця: " + reservedSeats);
        return reservedSeats;
    }

    private void addRows(final Model model, final Long repertoireId) {
        model.addAttribute("repertoireId", repertoireId);
        model.addAttribute("rows", rows);
    }

    private List<String> getReservedSeats(@ModelAttribute("seatInfo") final ReserveSeatConfiguration reserveSeatConfiguration) {
        final List<String> reservedSeats = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : reserveSeatConfiguration.getMap().entrySet()) {
            if (TRUE.equals(entry.getValue())) {
                reservedSeats.add(entry.getKey());
            }
        }
        return reservedSeats;
    }
}