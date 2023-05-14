package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.*;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.CountryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ru.iu3.backend.tools.DataValidationException;
import javax.validation.Valid;
/**
 * Метод, который отражает логику работы таблицы художников
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class ArtistController {
    // Здесь используется два репозитория: репозиторий артистов и репозиторий стран
    @Autowired
    ArtistRepository artistsRepository;

    @Autowired
    CountryRepository countryRepository;

    /**
     * Метод, который возвращает список артистов для данной БД
     *
     * @return - список артистов, который представлен в JSON
     */
    @GetMapping("/artists")
    public Page getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistsRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId)
            throws DataValidationException {
        Artist artist = artistsRepository.findById(artistId)
                .orElseThrow(() -> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artist);
    }

    /**
     * Метод, который добавляет артистов в базу данных
     *
     * @param artists - Структура данных, которая поступает из PostMan в виде JSON-файла
     *                где распарсивается и представлется в нужном для нас виде
     * @return - Статус. 404, если ок. В противном случае, будет выдавать ошибку
     * @throws Exception - выброс исключения. Обязательное требование
     */
    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artists) throws DataValidationException {
        try {

            // Извлекаем самостоятельно страну из пришедших данных
            artists.country = countryRepository.findByName(artists.country.name).orElseThrow(() -> new DataValidationException("Страна с таким индексом не найдена"));
            // Формируем новый объект класса Artists и сохраняем его в репозиторий
            Artist nc = artistsRepository.save(artists);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception exception) {
            // Указываем тип ошибки
            if (exception.getMessage().contains("ConstraintViolationException")) {
                throw new DataValidationException("Этот художник уже есть в базе");
            } else {
                throw new DataValidationException("Неизвестная ошибка");
            }
        }
    }
    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long artistId, @Valid @RequestBody Artist artistDetails)  throws DataValidationException{
        try {
            Artist artist = artistsRepository.findById(artistId).orElseThrow(() -> new DataValidationException("Художник с таким индексом не найден"));
            artist.name = artistDetails.name;
            artist.country = countryRepository.findByName(artistDetails.country.name).orElseThrow(() -> new DataValidationException("Страна с таким именем не найдена"));
            artist.age = artistDetails.age;
            artistsRepository.save(artist);
            return ResponseEntity.ok(artist);
        }
        catch (Exception ex) {
            if (ex.getMessage().contains("artist.name_UNIQUE"))
                throw new DataValidationException("Этот художник уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }


    /**
     * Метод, который обновляет данные для художников
     * @param artistsID - ID художника, по которому будет осуществляться собственно поиск
     * @param artistDetails - детальная информация по художникам
     * @return - возвращает заголовок. Если всё ок, то 200. Иначе будет ошибка
     */

    /**
     * Метод, который удаляет художников
     * @param  - ID художника, который будет удалён из базы данных
     * @return - вернёт 200, если всё было ок
     */
    @PostMapping("/deleteartists")
    public ResponseEntity<Object> deleteArtists(@Valid @RequestBody List<Artist> artists) {
        artistsRepository.deleteAll(artists);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}