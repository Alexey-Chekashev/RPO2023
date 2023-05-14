package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.Country;
import ru.iu3.backend.models.Painting;
import ru.iu3.backend.repositories.MuseumRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.*;
import ru.iu3.backend.tools.DataValidationException;

import javax.validation.Valid;

/**
 * Класс - контроллер музея
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1")
public class MuseumController {
    // Репозиторий нашего музея
    @Autowired
    MuseumRepository museumRepository;

    /**
     * Метод, который выдаёт список музеев
     * @return - список музеев, представленный в формате JSON
     */
    @GetMapping("/museums")
    public Page<Museum> getAllMuseums(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return museumRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }
    @GetMapping("/museums/{id}")
    public ResponseEntity<Museum> getMuseum(@PathVariable(value = "id") Long museumId)
            throws DataValidationException {
        Museum museum = museumRepository.findById(museumId).orElseThrow(()->new DataValidationException("Музей с таким индексом не найден"));
        return ResponseEntity.ok(museum);
    }
    /**
     * Метод, который осуществляет предоставление доступа к картине из вывода музея
     * @param museumID - ID картины
     * @return - блок картин, если таковы есть
     */
    @GetMapping("/museums/{id}/paintings")
    public ResponseEntity<List<Painting>> getPaintingMuseums(@PathVariable(value = "id") Long museumID) {
        Optional<Museum> cc = museumRepository.findById(museumID);
        if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().paintings);
        }

        return ResponseEntity.ok(new ArrayList<Painting>());
    }

    /**
     * Метод, который добавляет country в таблиц
     * RequestBody - это наш экземпляр (через curl передаётся в виде JSON)
     * @param museum - наш экземпляр класса museum
     * @return - статус (ОК/НЕ ОК)
     */
    @PostMapping("/museums")
    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum) throws DataValidationException {
        try {
            // Попытка сохранить что-либо в базу данных
            Museum newMusem = museumRepository.save(museum);
            return new ResponseEntity<Object>(newMusem, HttpStatus.OK);
        } catch (Exception exception) {
            // Указываем тип ошибки
            if (exception.getMessage().contains("museum.name_UNIQUE"))
                throw new DataValidationException("Этот музей уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }


    /**
     * Метод, который обновляет данные в таблице
     * @param museumID - указываем id по которому будем обновлять данные
     * @param museumDetails - сводки по Museum
     * @return - ОК/НЕ ОК
     */
    @PutMapping("/museums/{id}")
    public ResponseEntity<Museum> updateMuseum(@PathVariable(value = "id") Long museumId, @Valid @RequestBody Museum museumDetails)  throws DataValidationException{
        try {
            Museum museum = museumRepository.findById(museumId).orElseThrow(() -> new DataValidationException("Музей с таким индексом не найден"));
            museum.name = museumDetails.name;
            museum.location = museumDetails.location;
            museumRepository.save(museum);
            return ResponseEntity.ok(museum);
        }
        catch (Exception ex) {
            if (ex.getMessage().contains("museum.name_UNIQUE"))
                throw new DataValidationException("Этот музей уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }
    /**
     * Метод, который удаляет информацию из базы данных
     * @param - по какому ID-шнику удаляем информацию
     * @return - возвращает true, если удалено успешно, false - в противном случае
     */
    @PostMapping("/deletemuseums")
    public ResponseEntity<Object> deleteMuseums(@Valid @RequestBody List<Museum> museums) {
        museumRepository.deleteAll(museums);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}