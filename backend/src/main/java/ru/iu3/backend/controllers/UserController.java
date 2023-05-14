package ru.iu3.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.iu3.backend.models.Museum;
import ru.iu3.backend.models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.iu3.backend.repositories.UserRepository;
import ru.iu3.backend.repositories.MuseumRepository;
import java.util.*;
import java.util.Optional;
import ru.iu3.backend.tools.DataValidationException;
import ru.iu3.backend.tools.Utils;

import javax.validation.Valid;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1")

public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MuseumRepository museumRepository;


    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return userRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "login")));
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") Long userId)
            throws DataValidationException {
        User user = userRepository.findById(userId).orElseThrow(()->new DataValidationException("Пользователь с таким индексом не найден"));
        return ResponseEntity.ok(user);
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId, @Valid @RequestBody User userDetails) throws DataValidationException{
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("Пользователь с таким индексом не найден"));
            user.login = userDetails.login;
            user.email = userDetails.email;
            String np = userDetails.np;
            if (np != null && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.password = Utils.ComputeHash(np, salt);
                user.salt = salt;
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            if (ex.getMessage().contains("users.name_UNIQUE"))
                throw new DataValidationException("Этот пользователь уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }

    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User users) throws DataValidationException {
        try {
            User nu = userRepository.save(users);
            return new ResponseEntity<Object>(nu, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex.getMessage().contains("users.name_UNIQUE"))
                throw new DataValidationException("Этот пользователь уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userID, @Validated @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userID);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for(Museum m: museums) {
                Optional<Museum> mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    ++cnt;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("added", String.valueOf(cnt));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId, @Validated @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : u.museums) {
                u.removeMuseum(m);
                cnt++;
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deleteusers")
    public ResponseEntity<Object> deleteUsers(@Valid @RequestBody List<User> users) {
        userRepository.deleteAll(users);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}