package com.bank.service;

import com.bank.exception.NotFoundException;
import com.bank.repository.UserRepository;
import com.bank.dto.response.UserProfileResponseDto;
import com.bank.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(NotFoundException::new);
    }

    public User getByIban(String iban) {
        return userRepository.getByIban(iban)
                                .orElseThrow(NotFoundException::new);
    }

    public UserProfileResponseDto getProfileById(Long id) {
        User user = userRepository.findById(id)
                                .orElseThrow(NotFoundException::new);

        UserProfileResponseDto responseDto = modelMapper.map(user, UserProfileResponseDto.class);

        return responseDto;
    }

    public void setUser(User user) {
        userRepository.save(user);
    }

}
