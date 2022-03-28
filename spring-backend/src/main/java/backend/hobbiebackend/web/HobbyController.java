package backend.hobbiebackend.web;


import backend.hobbiebackend.model.dto.HobbyInfoDto;
import backend.hobbiebackend.model.dto.HobbyInfoUpdateDto;
import backend.hobbiebackend.model.entities.*;
import backend.hobbiebackend.service.CategoryService;
import backend.hobbiebackend.service.HobbyService;
import backend.hobbiebackend.service.LocationService;
import backend.hobbiebackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/hobbies")
@CrossOrigin(origins = "http://localhost:4200")
public class HobbyController {


    private final HobbyService hobbyService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public HobbyController(HobbyService hobbyService, CategoryService categoryService, LocationService locationService, UserService userService, ModelMapper modelMapper) {

        this.hobbyService = hobbyService;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }




    @PostMapping
    public  ResponseEntity<HttpStatus> saveHobby(@RequestBody HobbyInfoDto info){

        Hobby offer = this.modelMapper.map(info, Hobby.class);
        Category category = this.categoryService.findByName(info.getCategory());
        Location location = this.locationService.getLocationByName(info.getLocation());
        offer.setLocation(location);
        offer.setCategory(category);
        BusinessOwner business = this.userService.findBusinessByUsername(info.getCreator());
        Set<Hobby> hobby_offers = business.getHobby_offers();
        hobby_offers.add(offer);
        business.setHobby_offers(hobby_offers);
        this.hobbyService.createHobby(offer);
        this.userService.saveUpdatedUser(business);


        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping(value ="/is-saved")
    public boolean isHobbySaved(@RequestParam Long id, @RequestParam String username){
        return  this.hobbyService.isHobbySaved(id, username);
    }

    @GetMapping(value ="/{id}")
    public Hobby getHobbyDetails(@PathVariable Long id){

      return  this.hobbyService.findHobbieById(id);
    }


    @PostMapping ("/save")
    public ResponseEntity<Long> save(@RequestParam Long id, @RequestParam String username){
        Hobby hobby = this.hobbyService.findHobbieById(id);

        boolean isSaved =  this.hobbyService.saveHobbyForClient(hobby,username);

        if (!isSaved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);


    }

    @DeleteMapping("/remove")
    public ResponseEntity<Long> removeHobby(@RequestParam Long id, @RequestParam String username){

        Hobby hobby = this.hobbyService.findHobbieById(id);

        boolean isRemoved =  this.hobbyService.removeHobbyForClient(hobby,username);

        if (!isRemoved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);

    }


    @PutMapping
    public ResponseEntity<?> updateHobby(@RequestBody HobbyInfoUpdateDto info) throws Exception {
        Hobby offer = this.modelMapper.map(info, Hobby.class);
        Category category = this.categoryService.findByName(info.getCategory());
        Location location = this.locationService.getLocationByName(info.getLocation());
        offer.setLocation(location);
        offer.setCategory(category);
            this.hobbyService.saveUpdatedHobby(offer);
            return  new ResponseEntity<Hobby>(offer, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteHobby(@PathVariable Long id) throws Exception {
        boolean isRemoved = this.hobbyService.deleteHobby(id);

        if (!isRemoved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/saved")
    public List<Hobby> savedHobbies(@RequestParam String username)  {
        AppClient appClient = this.userService.findAppClientByUsername(username);
        return this.hobbyService.findSavedHobbies(appClient);

    }
}

