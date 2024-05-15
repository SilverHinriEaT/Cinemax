package kinoteatr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import kinoteatr.model.Movie;
import kinoteatr.model.Repertoire;
import kinoteatr.repository.MovieRepository;
import kinoteatr.repository.RepertoireRepository;
import kinoteatr.service.MovieService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final RepertoireRepository repertoireRepository;

    @Override
    public String getMovies(final Model model) {
        final List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        return "movieIndex";
    }

    @Override
    public String addMovie(final Movie movie, final BindingResult result, final Model model) {
        if (result.hasErrors()) {
            return "add-movie";
        }
        movieRepository.save(movie);
        log.info("Додано новий фільм: " + movie.getTitle());
        return "redirect:/movies/list";
    }

    @Override
    public String showUpdateFormMovie(final long id, final Model model) {
        final Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неправильний ID: " + id));
        model.addAttribute("movie", movie);
        return "update-movie";
    }

    @Override
    public String updateMovie(final long id, final Movie movie) {
        final Movie movieFromDb = movieRepository.getOne(id);
        movieFromDb.setCategory(movie.getCategory());
        movieFromDb.setDescription(movie.getDescription());
        movieFromDb.setLength(movie.getLength());
        movieFromDb.setMinAge(movie.getMinAge());
        movieFromDb.setImageUrl(movie.getImageUrl());
        movieFromDb.setTitle(movie.getTitle());
        log.info("Дані фільму відредаговано " + movie.getTitle());
        return "redirect:/movies/list";
    }

    @Override
    public String deleteMovie(final long id, final Model model) {
        final List<Repertoire> repertoires = repertoireRepository.findByMovieId(id);
        repertoires.forEach(r -> repertoireRepository.deleteById(r.getId()));
        final Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Неправильний ID : " + id));
        movieRepository.delete(movie);
        final List<Movie> movies = movieRepository.findAll();
        model.addAttribute("movies", movies);
        log.info("Фільм видалено " + movie.getTitle());
        return "movieIndex";
    }

    @Override
    public String showMovieRepertoireForm(final String movieName, final Model model) {
        final Movie movieRepertoire = movieRepository.findByTitle(movieName);
        model.addAttribute("movieRepertoire", movieRepertoire);
        model.addAttribute("repertoire", new Repertoire());
        return "movie-repertoire";
    }

    @Override
    public String addMovieRepertoire(final Repertoire repertoire, final Long movieId, final BindingResult result) {
        repertoire.setMovie(movieRepository.getOne(movieId));
        repertoireRepository.save(repertoire);
        log.info("Додано сеанс для фільму з ID " + movieId);
        return "redirect:/movies/list";
    }

    @Override
    public String showUpdateMovieRepertoireForm(final String movieName, final Long repertoireId, final Model model) {
        final Repertoire repertoire = repertoireRepository.getOne(repertoireId);
        final Movie movieRepertoire = movieRepository.findByTitle(movieName);
        model.addAttribute("movieRepertoire", movieRepertoire);
        model.addAttribute("repertoire", repertoire);
        return "movie-repertoire";
    }

    @Override
    public String updateMovieRepertoire(final Repertoire repertoire, final Long repertoireId, final BindingResult result) {
        final Repertoire repertoireFromDb = repertoireRepository.getOne(repertoireId);
        repertoireFromDb.setDate(repertoire.getDate());
        log.info("Оновлено дані фільму: " + repertoire.getMovie().getTitle());
        return "redirect:/movies/list";
    }

    @Override
    public String deleteMovieRepertoire(final Long repertoireId, final Model model) {
        repertoireRepository.deleteById(repertoireId);
        log.info("Видалено сеанс для фільму з ID " + repertoireId);
        return "redirect:/movies/list";
    }
}