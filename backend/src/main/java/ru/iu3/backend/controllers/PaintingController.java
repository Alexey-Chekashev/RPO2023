package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.repositories.MuseumRepository;
import ru.iu3.backend.repositories.ArtistRepository;
import ru.iu3.backend.repositories.PaintingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.iu3.backend.tools.DataValidationException;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Класс - контроллер модели картин
 * Класс - контроллер картин
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1")
public class PaintingController {
    // По аналогии у нас будет два репозитория
    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    MuseumRepository museumRepository;

    @Autowired
    ArtistRepository artistRepository;
    /**
     * Метод, который возвращает список всех картин, которые есть в базе данных
     * @return - список картин
     */
    @GetMapping("/paintings")
        public Page getAllPaintings(@RequestParam("page") int page, @RequestParam("limit") int limit) {
            return paintingRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
        }

        @GetMapping("/paintings/{id}")
        public ResponseEntity getPainting(@PathVariable(value = "id") Long paintingId)
            throws DataValidationException
        {
            Painting painting = paintingRepository.findById(paintingId)
                    .orElseThrow(()-> new DataValidationException("Картина с таким индексом не найдена"));
            return ResponseEntity.ok(painting);
        }
    /**
     * Метод, который добавляет картины в базу данных
     * @param painting - картины
     * @return - заголовок. Ок/не ок
     */
    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody Painting painting) throws DataValidationException {
        try {
            painting.artistid = artistRepository.findByName(painting.artistid.name).orElseThrow(() -> new DataValidationException("Художник с таким индексом не найден"));
            painting.museumid = museumRepository.findByName(painting.museumid.name).orElseThrow(() -> new DataValidationException("Музей с таким индексом не найден"));
            Painting nc = paintingRepository.save(painting);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        } catch (Exception exception) {
            // Указываем тип ошибки
            if (exception.getMessage().contains("paintings.name_UNIQUE"))
                throw new DataValidationException("Эта картина уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    /**
     * Метод, обновляющий данные по картинам
     * @param - ID картины
     * @param paintingDetails - сведения по картинам
     * @return - ОК/не ОК
     */
    @PutMapping("/paintings/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long paintingId, @Valid @RequestBody Painting paintingDetails)  throws DataValidationException{
        try {
            Painting painting = paintingRepository.findById(paintingId).orElseThrow(() -> new DataValidationException("Картина с таким индексом не найдена"));
            painting.name = paintingDetails.name;
            painting.artistid = artistRepository.findByName(paintingDetails.artistid.name).orElseThrow(() -> new DataValidationException("Художник с таким именем не найден"));
            painting.museumid = museumRepository.findByName(paintingDetails.museumid.name).orElseThrow(() -> new DataValidationException("Музей с таким именем не найден"));
            painting.year = paintingDetails.year;
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        }
        catch (Exception ex) {
            if (ex.getMessage().contains("paintings.name_UNIQUE"))
                throw new DataValidationException("Эта картина уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    /**
     * Метод, который осуществляет удаление картины
     * @param  - ID картины
     * @return - статус: удален/не удален
     */
    @PostMapping("/deletepaintings")
    public ResponseEntity<Object> deletePaintings(@Valid @RequestBody List<Painting> paintings) {
        paintingRepository.deleteAll(paintings);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}